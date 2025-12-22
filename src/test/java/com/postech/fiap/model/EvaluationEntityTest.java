package com.postech.fiap.model;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
class EvaluationEntityTest {

    @Test
    void shouldCreateEvaluationEntityWithNoArgs() {
        // Act
        EvaluationEntity evaluation = new EvaluationEntity();

        // Assert
        assertThat(evaluation).isNotNull();
        assertThat(evaluation.id).isNull();
        assertThat(evaluation.getDescription()).isNull();
        assertThat(evaluation.getRating()).isZero();
        assertThat(evaluation.getDateHourCriation()).isNull();
    }

    @Test
    void shouldCreateEvaluationEntityWithArgsAndSetDate() {
        // Arrange
        String description = "Great service";
        int rating = 5;

        // Act
        EvaluationEntity evaluation = new EvaluationEntity(description, rating);

        // Assert
        assertThat(evaluation.getDescription()).isEqualTo(description);
        assertThat(evaluation.getRating()).isEqualTo(rating);
        assertThat(evaluation.getDateHourCriation()).isNotNull();
        assertThat(evaluation.getDateHourCriation()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void shouldSetAndGetIdCorrectly() {
        // Arrange
        EvaluationEntity evaluation = new EvaluationEntity();
        Long id = 123L;

        // Act
        evaluation.id = id; // Acessando campo público diretamente conforme padrão Panache

        // Assert
        assertThat(evaluation.id).isEqualTo(id);
    }
}