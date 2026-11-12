package com.deepconnect.ats.tenant.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class TenantLogoStorageTest {
    @TempDir Path root;
    TenantLogoStorage storage;

    @BeforeEach
    void setUp() {
        storage = new TenantLogoStorage(root.toString());
        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    void tearDown() {
        TransactionSynchronizationManager.clearSynchronization();
    }

    private MockMultipartFile raster(String name, String format, int width, int height) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        assertTrue(ImageIO.write(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB), format, output));
        return new MockMultipartFile("logo", name, "image/" + format, output.toByteArray());
    }

    private MockMultipartFile svg(String content) {
        return new MockMultipartFile("logo", "logo.svg", "image/svg+xml", content.getBytes(StandardCharsets.UTF_8));
    }

    private void complete(int status) {
        TransactionSynchronizationManager.getSynchronizations().forEach(s -> s.afterCompletion(status));
        TransactionSynchronizationManager.clearSynchronization();
        TransactionSynchronizationManager.initSynchronization();
    }

    @Test
    void acceptsBoundaryAndDeletesReplacementOnlyAfterCommit() throws Exception {
        String old = storage.store(raster("logo.png", "png", 140, 140), null);
        complete(TransactionSynchronization.STATUS_COMMITTED);
        String next = storage.store(raster("logo.jpg", "jpg", 1, 140), old);
        assertTrue(Files.exists(storage.getPath(old)));
        assertTrue(Files.exists(storage.getPath(next)));
        complete(TransactionSynchronization.STATUS_COMMITTED);
        assertFalse(Files.exists(root.resolve("tenant-logos").resolve(old)));
        assertTrue(Files.exists(storage.getPath(next)));
    }

    @Test
    void rollbackRemovesNewLogoAndKeepsPrevious() throws Exception {
        String old = storage.store(raster("logo.png", "png", 1, 1), null);
        complete(TransactionSynchronization.STATUS_COMMITTED);
        String next = storage.store(raster("logo.png", "png", 1, 1), old);
        complete(TransactionSynchronization.STATUS_ROLLED_BACK);
        assertTrue(Files.exists(storage.getPath(old)));
        assertFalse(Files.exists(root.resolve("tenant-logos").resolve(next)));
    }

    @Test
    void rejectsOversizeWrongTypeAndInvalidContent() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> storage.store(raster("large.png", "png", 141, 140), null));
        assertThrows(IllegalArgumentException.class, () -> storage.store(raster("wrong.jpg", "png", 1, 1), null));
        assertThrows(IllegalArgumentException.class, () -> storage.store(new MockMultipartFile("logo", "x.png", "image/png", new byte[1024 * 1024 + 1]), null));
        assertThrows(IllegalArgumentException.class, () -> storage.store(new MockMultipartFile("logo", "x.gif", "image/gif", new byte[]{1}), null));
        assertThrows(IllegalArgumentException.class, () -> storage.store(new MockMultipartFile("logo", "x.jpg", "image/jpeg", new byte[]{1, 2}), null));
    }

    @Test
    void acceptsExactlyOneMebibyteAndRejectsOneByteMore() {
        String start = "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"1\" height=\"1\">";
        String end = "</svg>";
        String content = start + " ".repeat(1024 * 1024 - start.length() - end.length()) + end;
        assertDoesNotThrow(() -> storage.store(svg(content), null));
        assertThrows(IllegalArgumentException.class, () -> storage.store(svg(content + " "), null));
    }

    @Test
    void validatesSvgDimensionsAndUnsafeContent() {
        String prefix = "<svg xmlns=\"http://www.w3.org/2000/svg\" ";
        String valid = storage.store(svg(prefix + "width=\"140\" height=\"140\"><style>.a{fill:red}</style><rect class=\"a\" width=\"10\" height=\"10\"/></svg>"), null);
        assertTrue(Files.exists(storage.getPath(valid)));
        assertDoesNotThrow(() -> storage.store(svg(prefix + "viewBox=\"0 0 140 140\"/>"), null));
        assertThrows(IllegalArgumentException.class, () -> storage.store(svg(prefix + "width=\"141\" height=\"1\"/>"), null));
        assertThrows(IllegalArgumentException.class, () -> storage.store(svg(prefix + "height=\"500\" viewBox=\"0 0 1 1\"/>"), null));
        assertThrows(IllegalArgumentException.class, () -> storage.store(svg(prefix + "width=\"1\" height=\"1\"><script>alert(1)</script></svg>"), null));
        assertThrows(IllegalArgumentException.class, () -> storage.store(svg(prefix + "width=\"1\" height=\"1\"><image href=\"https://example.com/a.png\"/></svg>"), null));
        assertThrows(IllegalArgumentException.class, () -> storage.store(svg("<!DOCTYPE svg [<!ENTITY x SYSTEM \"file:///etc/passwd\">]>" + prefix + "width=\"1\" height=\"1\">&x;</svg>"), null));
    }
}
