package com.example.notificationservice.controller;

import com.example.notificationservice.dto.UserEventDto;
import com.example.notificationservice.service.EmailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public String sendNotification(@RequestBody UserEventDto event) {
        String operation = event.getOperation();
        String email = event.getEmail();

        if ("CREATED".equals(operation)) {
            emailService.sendUserCreatedEmail(email);
            return "Письмо о создании отправлено на " + email;
        } else if ("DELETED".equals(operation)) {
            emailService.sendUserDeletedEmail(email);
            return "Письмо об удалении отправлено на " + email;
        } else {
            return "Неизвестная операция: " + operation;
        }
    }

    @PostMapping("/test")
    public String testEmail(@RequestParam String email) {
        emailService.sendUserCreatedEmail(email);
        return "Тестовое письмо отправлено на " + email;
    }
}