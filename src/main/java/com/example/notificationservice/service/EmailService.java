package com.example.notificationservice.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendUserCreatedEmail(String toEmail) {
        sendEmail(
                toEmail,
                "Аккаунт создан",
                "Здравствуйте! Ваш аккаунт на сайте был успешно создан."
        );
    }

    public void sendUserDeletedEmail(String toEmail) {
        sendEmail(
                toEmail,
                "Аккаунт удалён",
                "Здравствуйте! Ваш аккаунт был удалён."
        );
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom("noreply@example.com");

            mailSender.send(message);
            System.out.println(" Email отправлен на: " + to);
        } catch (Exception e) {
            System.err.println(" Ошибка отправки email: " + e.getMessage());
        }
    }
}