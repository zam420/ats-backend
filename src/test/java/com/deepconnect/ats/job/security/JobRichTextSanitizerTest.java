package com.deepconnect.ats.job.security;

import static org.junit.jupiter.api.Assertions.*;

import com.deepconnect.ats.job.dto.JobPositionDetailsRes;
import com.deepconnect.ats.job.model.JobPosition;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class JobRichTextSanitizerTest {
    @Test
    void separatesHeadingTextFromFollowingContent() {
        assertEquals("About Remote People: At Remote People, we're the best.",
            JobRichTextSanitizer.shortDescription("<h2>About Remote People</h2>At Remote People, we're the best.", 70));
        assertEquals("One: Two: Three: Four: Five: Six: text",
            JobRichTextSanitizer.shortDescription("<h1>One</h1><h2>Two</h2><h3>Three</h3>"
                + "<h4>Four</h4><h5>Five</h5><h6><strong>Six</strong> </h6><p>text</p><h2> </h2>", 70));
        assertEquals("\uE000 Heading: text",
            JobRichTextSanitizer.shortDescription("<p>\uE000</p><h2>Heading</h2>text", 70));
    }

    @Test
    void addsHeadingSeparatorAtTheConfiguredWordBoundary() {
        String beforeHeading = "word ".repeat(69);
        assertEquals((beforeHeading + "Title:").trim(),
            JobRichTextSanitizer.shortDescription("<p>" + beforeHeading + "</p><h1>Title continues</h1>", 70));
        assertEquals((beforeHeading + "Title:").trim(),
            JobRichTextSanitizer.shortDescription("<p>" + beforeHeading + "</p><h1>Title</h1><p>More</p>", 70));
        assertEquals("word ".repeat(70).trim(),
            JobRichTextSanitizer.shortDescription("<p>" + "word ".repeat(70) + "</p><h1>Title</h1>", 70));
        assertEquals("One Two Three:", JobRichTextSanitizer.shortDescription("<p>One</p><h2>Two Three Four</h2>", 3));
        assertThrows(IllegalArgumentException.class, () -> JobRichTextSanitizer.shortDescription("text", 0));
    }

    @Test
    void extractsUpToSeventyPlainTextWordsFromSafeHtml() {
        String words = java.util.stream.IntStream.rangeClosed(1, 71)
                .mapToObj(i -> "word" + i).collect(java.util.stream.Collectors.joining(" "));
        String result = JobRichTextSanitizer.shortDescription("<p>Intro &amp; welcome</p><p>" + words
                + "</p><script>hidden</script>", 70);
        assertEquals(70, result.split(" ").length);
        assertTrue(result.startsWith("Intro & welcome word1"));
        assertTrue(result.endsWith("word67"));
        assertFalse(result.contains("hidden"));
        assertEquals("First Second Third", JobRichTextSanitizer.shortDescription("<p>First<br>Second</p><p>Third</p>", 70));
        assertEquals("", JobRichTextSanitizer.shortDescription("<p><br></p>", 70));
    }

    @Test
    void preservesQuillFormattingAndSafeLinks() {
        String input = "<h2 class=\"ql-align-center\">Title</h2>"
                + "<p><strong>Bold</strong> <em>Italic</em> <span style=\"color: rgb(230, 0, 0); background-color: #ffff00\">Color</span></p>"
                + "<ul data-checked=\"true\"><li>First</li><li class=\"ql-indent-1\">Second</li></ul>"
                + "<pre class=\"ql-syntax\">code</pre><a href=\"https://example.com\" target=\"_blank\">Link</a>";
        String result = JobRichTextSanitizer.sanitize(input);

        assertTrue(result.contains("<h2 class=\"ql-align-center\">Title</h2>"));
        assertTrue(result.contains("<strong>Bold</strong>"));
        assertTrue(result.contains("color: rgb(230, 0, 0); background-color: #ffff00"));
        assertTrue(result.contains("data-checked=\"true\""));
        assertTrue(result.contains("class=\"ql-indent-1\""));
        assertTrue(result.contains("<pre class=\"ql-syntax\">code</pre>"));
        assertTrue(result.contains("href=\"https://example.com\""));
        assertTrue(result.contains("rel=\"noopener noreferrer\""));
        assertEquals(result, JobRichTextSanitizer.sanitize(result));
    }

    @Test
    void removesActiveContentAndUnsafeAttributes() {
        String input = "<script>alert(1)</script><p onclick=\"alert(1)\" class=\"evil ql-align-right\" "
                + "style=\"background-image: url(javascript:alert(1)); color: red\">Text</p>"
                + "<img src=x onerror=alert(1)><svg onload=alert(1)></svg>"
                + "<a href=\"javascript:alert(1)\" target=\"_blank\">bad</a>"
                + "<a href=\"&#x6a;avascript:alert(1)\">encoded</a>";
        String result = JobRichTextSanitizer.sanitize(input);

        assertFalse(result.contains("script"));
        assertFalse(result.contains("onclick"));
        assertFalse(result.contains("onerror"));
        assertFalse(result.contains("<img"));
        assertFalse(result.contains("<svg"));
        assertFalse(result.contains("javascript:"));
        assertFalse(result.contains("background-image"));
        assertFalse(result.contains("evil"));
        assertTrue(result.contains("class=\"ql-align-right\""));
        assertTrue(result.contains("style=\"color: red\""));
        assertTrue(result.contains(">bad</a>"));
    }

    @Test
    void sanitizesLegacyValuesInDtoAndEntitySerialization() {
        JobPosition position = new JobPosition();
        position.setDescription("<p onclick=\"alert(1)\">Description</p>");
        // position.setMandatoryRequirements("<img src=x onerror=alert(1)><p>Required</p>");
        // position.setPreferredRequirements("<script>alert(1)</script><p>Preferred</p>");

        JobPositionDetailsRes dto = JobPositionDetailsRes.from(position);
        assertEquals("<p>Description</p>", dto.getDescription());

        JsonNode json = new ObjectMapper().valueToTree(position);
        assertEquals(dto.getDescription(), json.get("description").asText());
        assertNull(JobRichTextSanitizer.sanitize(null));
    }
}
