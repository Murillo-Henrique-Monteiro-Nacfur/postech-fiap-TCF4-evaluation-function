package com.postech.fiap.exception;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
class ErrorDTOTest {

    @Test
    void shouldCreateErrorDTOWithConstructor() {
        String message = "Something went wrong";
        String code = "ERROR_001";

        ErrorDTO errorDTO = new ErrorDTO(message, code);

        assertThat(errorDTO).isNotNull();
        assertThat(errorDTO.getMessage()).isEqualTo(message);
        assertThat(errorDTO.getCode()).isEqualTo(code);
        assertThat(errorDTO.getTimestamp()).isNotNull();
        assertThat(errorDTO.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void shouldCreateEmptyErrorDTO() {
        ErrorDTO errorDTO = new ErrorDTO();

        assertThat(errorDTO).isNotNull();
        assertThat(errorDTO.getMessage()).isNull();
        assertThat(errorDTO.getCode()).isNull();
        assertThat(errorDTO.getTimestamp()).isNull();
    }
}