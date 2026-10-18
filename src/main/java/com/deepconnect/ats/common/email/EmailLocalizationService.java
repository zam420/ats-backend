package com.deepconnect.ats.common.email;

import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.deepconnect.ats.common.enums.EmailType;
import com.deepconnect.ats.common.utils.LocalizationUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailLocalizationService {

    private final MessageSource messageSource;
    private final TemplateEngine templateEngine;

    private static String emailSubjectPrefix = "email.subject.";

    public EmailContent getEmailContent(EmailType emailType, Map<String, Object> contextVariables) {
            Locale locale = LocalizationUtils.getUserLocale();
            String subject = messageSource.getMessage(getFullMessageKey(emailType.getSubjectKey()), null, locale);
            String templateName = emailType.getTemplateKey() + "_" + locale.toString();
            String body = templateEngine.process(templateName, new Context(locale, contextVariables));
            return new EmailContent(subject, body);
    }

    private static String getFullMessageKey(String messageKey){
        return emailSubjectPrefix + messageKey;
    }

}
