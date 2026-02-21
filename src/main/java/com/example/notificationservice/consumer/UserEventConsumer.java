package com.example.notificationservice.consumer;

import com.example.notificationservice.dto.UserEventDto;
import com.example.notificationservice.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserEventConsumer.class);

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    public UserEventConsumer(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void consume(String message) {
        try {
            // Парсим JSON в объект
            UserEventDto event = objectMapper.readValue(message, UserEventDto.class);

            log.info("Получено событие: {} для пользователя {}",
                    event.getOperation(), event.getEmail());

            String email = event.getEmail();
            String operation = event.getOperation();

            // Проверяем, что email не пустой
            if (email == null || email.trim().isEmpty()) {
                log.error("Получено событие с пустым email");
                return;
            }

            // Обрабатываем в зависимости от операции
            if ("CREATED".equals(operation)) {
                emailService.sendUserCreatedEmail(email);
                log.info("Отправлено письмо о создании на {}", email);

            } else if ("DELETED".equals(operation)) {
                emailService.sendUserDeletedEmail(email);
                log.info("Отправлено письмо об удалении на {}", email);

            } else {
                log.warn("Неизвестная операция: {} для email: {}", operation, email);
            }

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("Ошибка парсинга JSON: {}", e.getMessage());
            log.debug("Проблемное сообщение: {}", message);

        } catch (Exception e) {
            log.error("Непредвиденная ошибка при обработке сообщения: {}", e.getMessage(), e);
        }
    }
}