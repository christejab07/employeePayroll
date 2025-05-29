package com.erp.employeepayroll.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPayslipEmail(String to, String subject, String content) {
        logger.info("Preparing to send email to: {}", to);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("christejab@gmail.com"); // Add a from address
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true enables HTML content

            mailSender.send(message);
            logger.info("Email successfully sent to: {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send email to: {}. Error: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email to " + to, e);
        }
    }
}
//# application-dev.properties
//spring.mail.host=sandbox.smtp.mailtrap.io
//spring.mail.port=2525
//spring.mail.username=11c792a1f55e19
//spring.mail.password=0e9e4cddca0286
//spring.mail.properties.mail.smtp.auth=true
//spring.mail.properties.mail.smtp.starttls.enable=true