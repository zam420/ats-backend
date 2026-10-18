package com.deepconnect.ats.service.mail;

import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.common.microsoft.GraphClient;
import com.deepconnect.ats.model.AtsEmailRecord;
import com.deepconnect.ats.repository.AtsEmailRecordRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final GraphClient graphClient;
    private final AtsEmailRecordRepository emailRepository;
    private final MailFrequencyControlService frequencyControl;
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username:}") private String fromAddress;

    public List<ResumeEmail> getEmails(String inbox, LocalDateTime timeFrom) {
        if (inbox == null || inbox.isBlank()) throw new InternalServerException("Inbox address has not been configured");
        String mailbox = UriUtils.encodePathSegment(inbox, StandardCharsets.UTF_8);
        String since = timeFrom.atZone(ZoneId.of("Asia/Shanghai")).toOffsetDateTime().toString();
        String url = "/users/" + mailbox + "/mailFolders/inbox/messages?$filter=receivedDateTime%20gt%20" +
                UriUtils.encodeQueryParam(since, StandardCharsets.UTF_8) +
                "&$orderby=receivedDateTime%20desc&$top=100&$select=id,subject,body,from,receivedDateTime,sentDateTime";
        List<ResumeEmail> emails = new ArrayList<>();
        while (url != null) {
            JsonNode page;
            try { page = graphClient.get(url); }
            catch (HttpClientErrorException.NotFound e) { log.error("get mails from{} failed,error:{}", inbox, e.getMessage()); return List.of(); }
            JsonNode values = page.path("value");
            if (values.isArray()) values.forEach(item -> emails.add(toResumeEmail(item, inbox)));
            JsonNode next = page.get("@odata.nextLink");
            url = next == null || next.isNull() ? null : next.asText();
        }
        return emails;
    }

    private ResumeEmail toResumeEmail(JsonNode item, String mailbox) {
        JsonNode from = item.path("from").path("emailAddress");
        JsonNode body = item.path("body");
        return new ResumeEmail(item.path("id").asText(), item.path("subject").asText(""),
                body.path("content").asText(""), body.path("contentType").asText("text"),
                from.path("name").asText(""), from.path("address").asText(""),
                parseDate(item.path("receivedDateTime")), parseDate(item.path("sentDateTime")), mailbox);
    }

    private OffsetDateTime parseDate(JsonNode value) {
        try { return OffsetDateTime.parse(value.asText()); }
        catch (Exception ignored) { return null; }
    }

    public List<AtsEmailRecord> getEmailRecords(Integer status) {
        return emailRepository.findByStatus(status);
    }

    @Transactional
    public Long sendMail(AtsEmailRecord record, boolean synchronous) {
        frequencyControl.check(List.of(record));
        record.setStatus(0);
        if (synchronous) {
            try { doSend(record); record.setStatus(1); }
            catch (Exception e) { record.setStatus(2); record.setRemark(e.getMessage() == null ? e.toString() : e.getMessage()); }
        }
        return emailRepository.save(record).getId();
    }

    @Transactional
    public void sendInBulk(List<AtsEmailRecord> records, boolean synchronous) {
        frequencyControl.check(records);
        for (AtsEmailRecord record : records) {
            record.setStatus(0);
            if (synchronous) {
                try { doSend(record); record.setStatus(1); }
                catch (Exception e) { record.setStatus(2); record.setRemark(e.getMessage() == null ? e.toString() : e.getMessage()); }
            }
        }
        emailRepository.saveAll(records);
    }
    public void doSend(AtsEmailRecord record) {
        if (fromAddress == null || fromAddress.isBlank()) throw new InternalServerException("Primary account has not been configured");
        List<String> to = safe(record.getTo());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(fromAddress);
            helper.setTo(to.toArray(String[]::new));
            List<String> cc = safe(record.getCc());
            List<String> bcc = safe(record.getBcc());
            if (!cc.isEmpty()) helper.setCc(cc.toArray(String[]::new));
            if (!bcc.isEmpty()) helper.setBcc(bcc.toArray(String[]::new));
            helper.setSubject(Objects.toString(record.getSubject(), ""));
            helper.setText(Objects.toString(record.getBody(), ""), Boolean.TRUE.equals(record.getIsHtmlMail()));
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new InternalServerException("Failed to build mail: " + e.getMessage());
        }
    }

    private List<String> safe(List<String> values) { return values == null ? Collections.emptyList() : values; }
}
