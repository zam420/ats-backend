package com.deepconnect.ats.common.email;

import java.util.List;
import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.exception.CustomException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private MimeMessageHelper setEmailParamsAndGetMimeMessageHelper(MimeMessage message, String[] toList, String[] ccList, String subject, String body) throws MessagingException{
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setSubject(subject);
        helper.setFrom(fromEmail);
        helper.setTo(toList);
        if(ccList!=null && ccList.length > 0){
            helper.setCc(ccList);
        }
        helper.setText(body, true); // `true` enables HTML content
        return helper;
    }

    public void sendEmail(String to, String subject, String body) {
        this.sendEmail(new String[]{to}, null, subject, body);
    }

    public void sendEmail(String to, String cc, String subject, String body) {
        this.sendEmail(new String[]{to}, new String[]{cc}, subject, body);
    }

    public void sendEmail(List<String> toList, List<String> ccList, String subject, String body) {
        this.sendEmail(toList.toArray(String[]::new), ccList.toArray(String[]::new), subject, body);
    }

    public void sendEmail(String[] toList, String[] ccList, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            this.setEmailParamsAndGetMimeMessageHelper(message, toList, ccList, subject, body);
            mailSender.send(message);
        } catch (Exception ex) {
            throw new CustomException(CustomExceptionType.REGISTER_ACTIVATION_EMAIL_FAILURE);
        }
    }

    public void sendEmailWithAttachment(String to, String subject, String body, String attachmentPath, String displayFileName) {
        this.sendEmailWithAttachment(to, null, subject, body, attachmentPath, displayFileName);
    }

    public void sendEmailWithAttachment(String to, String cc, String subject, String body, String attachmentPath, String displayFileName) {
        this.sendEmailWithAttachment(new String[]{to}, new String[]{cc}, subject, body, attachmentPath, displayFileName);
    }

    public void sendEmailWithAttachment(List<String> toList, List<String> ccList, String subject, String body, String attachmentPath, String displayFileName) {
        this.sendEmailWithAttachment(toList.toArray(String[]::new), ccList.toArray(String[]::new), subject, body, attachmentPath, displayFileName);
    }

    public void sendEmailWithAttachment(String[] toList, String[] ccList, String subject, String body, String attachmentPath, String displayFileName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = this.setEmailParamsAndGetMimeMessageHelper(message, toList, ccList, subject, body);
            helper.addAttachment(displayFileName, new FileSystemResource(new File(attachmentPath)));
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
