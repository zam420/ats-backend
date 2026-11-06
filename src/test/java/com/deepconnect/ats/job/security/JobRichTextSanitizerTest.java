package com.deepconnect.ats.job.security;

import static org.junit.jupiter.api.Assertions.*;

import com.deepconnect.ats.job.dto.JobPositionRes;
import com.deepconnect.ats.job.model.JobPosition;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class JobRichTextSanitizerTest {
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
        position.setMandatoryRequirements("<img src=x onerror=alert(1)><p>Required</p>");
        position.setPreferredRequirements("<script>alert(1)</script><p>Preferred</p>");

        JobPositionRes dto = JobPositionRes.from(position);
        assertEquals("<p>Description</p>", dto.getDescription());
        assertEquals("<p>Required</p>", dto.getMandatoryRequirements());
        assertEquals("<p>Preferred</p>", dto.getPreferredRequirements());

        JsonNode json = new ObjectMapper().valueToTree(position);
        assertEquals(dto.getDescription(), json.get("description").asText());
        assertEquals(dto.getMandatoryRequirements(), json.get("mandatoryRequirements").asText());
        assertEquals(dto.getPreferredRequirements(), json.get("preferredRequirements").asText());
        assertNull(JobRichTextSanitizer.sanitize(null));
    }
}
