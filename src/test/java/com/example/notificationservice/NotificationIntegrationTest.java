package com.example.notificationservice;

import com.example.notificationservice.dto.UserEventDto;
import com.example.notificationservice.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SuppressWarnings("unused")
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"user-events"})
public class NotificationIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    @Test
    public void testSendUserCreatedEvent() throws Exception {
        UserEventDto event = new UserEventDto(
                1L,
                "test@example.com",
                "CREATED",
                "2024-01-01T12:00:00"
        );

        String message = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("user-events", message).get();

        verify(emailService, timeout(5000).times(1))
                .sendUserCreatedEmail("test@example.com");
    }
}