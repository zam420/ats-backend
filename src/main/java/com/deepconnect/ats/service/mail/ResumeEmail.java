package com.deepconnect.ats.service.mail;

import java.time.OffsetDateTime;

public record ResumeEmail(String id, String subject, String body, String bodyType,
                          String senderName, String senderAddress,
                          OffsetDateTime receivedAt, OffsetDateTime sentAt,
                          String mailbox) {}
