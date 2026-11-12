package com.deepconnect.ats.tenant.service;

import com.deepconnect.ats.common.exception.InternalServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Service
public class TenantLogoStorage {
    private static final long MAX_BYTES = 1024 * 1024;
    private static final int MAX_DIMENSION = 140;
    private static final Pattern FILE_NAME = Pattern.compile("[0-9a-f-]{36}\\.(png|jpg|svg)");
    private static final Pattern SVG_LENGTH = Pattern.compile("(?:\\d+(?:\\.\\d+)?|\\.\\d+)(?:px)?");
    private final Path directory;

    public TenantLogoStorage(@Value("${ats.storage.root:./uploads}") String root) {
        this.directory = Path.of(root).toAbsolutePath().normalize().resolve("tenant-logos");
    }

    public String store(MultipartFile file, String oldPath) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("Logo storage requires an active transaction");
        }
        if (file.isEmpty() || file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("Logo must be non-empty and no larger than 1 MB");
        }
        String original = file.getOriginalFilename();
        String lower = original == null ? "" : original.toLowerCase(Locale.ROOT);
        String extension = lower.endsWith(".png") ? "png" : lower.endsWith(".jpg") || lower.endsWith(".jpeg") ? "jpg"
                : lower.endsWith(".svg") ? "svg" : "";
        if (extension.isEmpty()) throw new IllegalArgumentException("Logo must be JPG, PNG or SVG");
        Path previous = oldPath == null ? null : resolve(oldPath);
        try {
            byte[] data = file.getBytes();
            if (data.length == 0 || data.length > MAX_BYTES) throw new IllegalArgumentException("Logo must be no larger than 1 MB");
            if (extension.equals("svg")) validateSvg(data);
            else validateRaster(data, extension);
            Files.createDirectories(directory);
            String name = UUID.randomUUID() + "." + extension;
            Path target = directory.resolve(name);
            Files.createFile(target);
            try {
                Files.write(target, data, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                deleteQuietly(target);
                throw e;
            }
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status != STATUS_COMMITTED) deleteQuietly(target);
                    else if (previous != null) deleteQuietly(previous);
                }
            });
            return name;
        } catch (IOException e) {
            throw new InternalServerException("Failed to store tenant logo: " + e.getMessage());
        }
    }

    public Path getPath(String name) {
        if (name == null) throw new IllegalArgumentException("Tenant has no logo");
        Path path = resolve(name);
        if (!Files.isRegularFile(path)) throw new InternalServerException("Tenant logo not found");
        return path;
    }

    private Path resolve(String name) {
        if (!FILE_NAME.matcher(name).matches()) throw new InternalServerException("Invalid tenant logo path");
        return directory.resolve(name);
    }

    private void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Failed to remove tenant logo {}", path, e);
        }
    }

    private void validateRaster(byte[] data, String extension) throws IOException {
        try (ImageInputStream input = ImageIO.createImageInputStream(new ByteArrayInputStream(data))) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) throw new IllegalArgumentException("Invalid image file");
            ImageReader reader = readers.next();
            try {
                String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                if (!format.equals(extension) && !(extension.equals("jpg") && format.equals("jpeg"))) {
                    throw new IllegalArgumentException("Logo file content does not match its format");
                }
                reader.setInput(input);
                checkDimensions(reader.getWidth(0), reader.getHeight(0));
                if (reader.read(0) == null) throw new IllegalArgumentException("Invalid image file");
            } finally {
                reader.dispose();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid image file", e);
        }
    }

    private void validateSvg(byte[] data) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            Document doc = factory.newDocumentBuilder().parse(new ByteArrayInputStream(data));
            Element root = doc.getDocumentElement();
            if (!"svg".equals(root.getLocalName()) || !"http://www.w3.org/2000/svg".equals(root.getNamespaceURI())) {
                throw new IllegalArgumentException("Invalid SVG logo");
            }
            double width;
            double height;
            if (root.hasAttribute("width") && root.hasAttribute("height")) {
                width = svgLength(root.getAttribute("width"));
                height = svgLength(root.getAttribute("height"));
            } else if (!root.hasAttribute("width") && !root.hasAttribute("height")) {
                String[] viewBox = root.getAttribute("viewBox").trim().split("[\\s,]+");
                if (viewBox.length != 4) throw new IllegalArgumentException("SVG must specify pixel dimensions or a viewBox");
                if (!Double.isFinite(Double.parseDouble(viewBox[0])) || !Double.isFinite(Double.parseDouble(viewBox[1]))) {
                    throw new IllegalArgumentException("Invalid SVG viewBox");
                }
                width = Double.parseDouble(viewBox[2]);
                height = Double.parseDouble(viewBox[3]);
            } else {
                throw new IllegalArgumentException("SVG must specify both width and height");
            }
            checkDimensions(width, height);
            validateSvgNode(doc, 0);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalArgumentException("Invalid SVG logo", e);
        }
    }

    private double svgLength(String value) {
        if (!SVG_LENGTH.matcher(value).matches()) throw new IllegalArgumentException("SVG dimensions must be in pixels");
        return Double.parseDouble(value.replace("px", ""));
    }

    private void checkDimensions(double width, double height) {
        if (!Double.isFinite(width) || !Double.isFinite(height) || width <= 0 || height <= 0
                || width > MAX_DIMENSION || height > MAX_DIMENSION) {
            throw new IllegalArgumentException("Logo dimensions must not exceed 140 x 140 pixels");
        }
    }

    private void validateSvgNode(Node node, int depth) {
        if (depth > 128) throw new IllegalArgumentException("SVG nesting is too deep");
        if (node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) throw new IllegalArgumentException("Unsupported SVG content");
        if (node instanceof Element element) {
            String tag = element.getLocalName();
            if (!"http://www.w3.org/2000/svg".equals(element.getNamespaceURI())
                    || tag == null || tag.equalsIgnoreCase("script") || tag.equalsIgnoreCase("foreignObject")
                    || tag.equalsIgnoreCase("animate")
                    || tag.equalsIgnoreCase("animateTransform") || tag.equalsIgnoreCase("set")) {
                throw new IllegalArgumentException("Unsupported SVG content");
            }
            if (tag.equalsIgnoreCase("style") && unsafeCss(element.getTextContent())) {
                throw new IllegalArgumentException("Unsupported SVG style");
            }
            NamedNodeMap attrs = element.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
                Node attr = attrs.item(i);
                String name = attr.getNodeName().toLowerCase(Locale.ROOT);
                String value = attr.getNodeValue().trim();
                if (name.startsWith("on") || (name.equals("style") && unsafeCss(value))
                        || ((name.equals("href") || name.endsWith(":href")) && !value.startsWith("#"))
                        || (!name.equals("style") && value.toLowerCase(Locale.ROOT).contains("url(")
                            && !value.matches("url\\(#[A-Za-z0-9_.:-]+\\)"))) {
                    throw new IllegalArgumentException("Unsupported SVG content");
                }
            }
        }
        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) validateSvgNode(child, depth + 1);
    }

    private boolean unsafeCss(String value) {
        String lower = value.toLowerCase(Locale.ROOT);
        return lower.contains("@") || lower.contains("\\") || lower.contains("url(");
    }
}
