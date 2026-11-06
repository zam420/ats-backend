package com.deepconnect.ats.job.security;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Safelist;

public final class JobRichTextSanitizer {
    private static final Safelist ALLOWED_HTML = new Safelist()
            .addTags("p", "br", "strong", "b", "em", "i", "u", "s", "strike",
                    "h1", "h2", "h3", "h4", "h5", "h6", "blockquote", "pre",
                    "ol", "ul", "li", "a", "span", "sub", "sup")
            .addAttributes(":all", "class", "style")
            .addAttributes("a", "href", "target")
            .addAttributes("ul", "data-checked")
            .addProtocols("a", "href", "http", "https", "mailto");

    private static final Pattern QUILL_CLASS = Pattern.compile(
            "ql-(?:align-(?:center|right|justify)|indent-[1-8]|font-(?:serif|monospace)|size-(?:small|large|huge)|direction-rtl|syntax)");
    private static final Pattern COLOR = Pattern.compile(
            "(?i)(?:#[0-9a-f]{3}|#[0-9a-f]{6}|rgb\\(\\s*(?:[0-9]{1,3}\\s*,\\s*){2}[0-9]{1,3}\\s*\\)|black|white|red|orange|yellow|green|blue|purple)");

    private JobRichTextSanitizer() {
    }

    public static String sanitize(String html) {
        if (html == null) {
            return null;
        }
        Document clean = new Cleaner(ALLOWED_HTML).clean(Jsoup.parseBodyFragment(html));
        clean.outputSettings().prettyPrint(false);
        for (Element element : clean.body().getAllElements()) {
            if (element.hasAttr("class")) {
                List<String> classes = new ArrayList<>();
                for (String name : element.classNames()) {
                    if (QUILL_CLASS.matcher(name).matches()) {
                        classes.add(name);
                    }
                }
                if (classes.isEmpty()) {
                    element.removeAttr("class");
                } else {
                    element.attr("class", String.join(" ", classes));
                }
            }
            if (element.hasAttr("style")) {
                List<String> styles = new ArrayList<>();
                for (String declaration : element.attr("style").split(";")) {
                    String[] parts = declaration.split(":", 2);
                    if (parts.length == 2) {
                        String property = parts[0].trim().toLowerCase(java.util.Locale.ROOT);
                        String value = parts[1].trim();
                        if ((property.equals("color") || property.equals("background-color"))
                                && COLOR.matcher(value).matches()) {
                            styles.add(property + ": " + value);
                        }
                    }
                }
                if (styles.isEmpty()) {
                    element.removeAttr("style");
                } else {
                    element.attr("style", String.join("; ", styles));
                }
            }
            if (element.tagName().equals("a")) {
                if (!element.attr("target").equals("_blank") || !element.hasAttr("href")) {
                    element.removeAttr("target");
                } else {
                    element.attr("rel", "noopener noreferrer");
                }
            }
            if (element.hasAttr("data-checked")
                    && !element.attr("data-checked").equals("true")
                    && !element.attr("data-checked").equals("false")) {
                element.removeAttr("data-checked");
            }
        }
        return clean.body().html();
    }
}
