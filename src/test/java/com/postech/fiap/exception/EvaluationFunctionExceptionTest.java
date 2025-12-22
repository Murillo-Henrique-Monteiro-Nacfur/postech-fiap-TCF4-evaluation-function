package com.postech.fiap.exception;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
class EvaluationFunctionExceptionTest {

    @Test
    void shouldCreateExceptionWithDefaultCode() {
        String message = "Default error";

        EvaluationFunctionException exception = new EvaluationFunctionException(message);

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCode()).isEqualTo("BUSINESS_ERROR");
    }

    @Test
    void shouldCreateExceptionWithCustomCode() {
        String message = "Custom error";
        String code = "CUSTOM_CODE";

        EvaluationFunctionException exception = new EvaluationFunctionException(message, code);

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCode()).isEqualTo(code);
    }
}