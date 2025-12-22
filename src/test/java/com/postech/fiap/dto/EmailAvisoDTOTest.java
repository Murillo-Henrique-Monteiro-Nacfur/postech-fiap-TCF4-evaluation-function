package com.postech.fiap.dto;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
class EmailAvisoDTOTest {

    @Test
    void shouldCreateEmailAvisoDTOWithAllArgsConstructor() {
        List<String> destinations = List.of("test@example.com", "admin@example.com");
        String message = "Urgent alert!";
        String subject = "Alert";

        EmailAvisoDTO emailAvisoDTO = new EmailAvisoDTO(destinations, message, subject);

        assertThat(emailAvisoDTO).isNotNull();
        assertThat(emailAvisoDTO.getDestinations()).isEqualTo(destinations);
        assertThat(emailAvisoDTO.getMessage()).isEqualTo(message);
        assertThat(emailAvisoDTO.getSubject()).isEqualTo(subject);
    }

    @Test
    void shouldSetAndGetValuesCorrectly() {
        EmailAvisoDTO emailAvisoDTO = new EmailAvisoDTO();
        List<String> destinations = List.of("user@example.com");
        String message = "Notification";
        String subject = "Info";

        emailAvisoDTO.setDestinations(destinations);
        emailAvisoDTO.setMessage(message);
        emailAvisoDTO.setSubject(subject);

        assertThat(emailAvisoDTO.getDestinations()).containsExactly("user@example.com");
        assertThat(emailAvisoDTO.getMessage()).isEqualTo("Notification");
        assertThat(emailAvisoDTO.getSubject()).isEqualTo("Info");
    }
}