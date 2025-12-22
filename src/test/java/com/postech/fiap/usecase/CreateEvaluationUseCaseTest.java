package com.postech.fiap.usecase;

import com.postech.fiap.exception.EvaluationFunctionException;
import com.postech.fiap.model.EvaluationEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@QuarkusTest
class CreateEvaluationUseCaseTest {

    @Inject
    CreateEvaluationUseCase createEvaluationUseCase;

    @Test
    void shouldCreateEvaluationSuccessfully() {
        EvaluationEntity evaluation = mock(EvaluationEntity.class);
        when(evaluation.getRating()).thenReturn(8);
        when(evaluation.getDescription()).thenReturn("Good service");

        createEvaluationUseCase.createEvaluation(evaluation);

        verify(evaluation, times(1)).persist();
    }

    @Test
    void shouldThrowExceptionWhenRatingIsInvalid() {
        EvaluationEntity evaluation = mock(EvaluationEntity.class);
        when(evaluation.getRating()).thenReturn(11); // Invalid rating

        assertThatThrownBy(() -> createEvaluationUseCase.createEvaluation(evaluation))
                .isInstanceOf(EvaluationFunctionException.class)
                .hasMessage("A nota deve estar entre 0 e 10.");

        verify(evaluation, never()).persist();
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsNull() {
        EvaluationEntity evaluation = mock(EvaluationEntity.class);
        when(evaluation.getRating()).thenReturn(5);
        when(evaluation.getDescription()).thenReturn(null);

        assertThatThrownBy(() -> createEvaluationUseCase.createEvaluation(evaluation))
                .isInstanceOf(EvaluationFunctionException.class)
                .hasMessage("A descrição não pode ser vazia.");

        verify(evaluation, never()).persist();
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsEmpty() {
        EvaluationEntity evaluation = mock(EvaluationEntity.class);
        when(evaluation.getRating()).thenReturn(5);
        when(evaluation.getDescription()).thenReturn("");

        assertThatThrownBy(() -> createEvaluationUseCase.createEvaluation(evaluation))
                .isInstanceOf(EvaluationFunctionException.class)
                .hasMessage("A descrição não pode ser vazia.");

        verify(evaluation, never()).persist();
    }
}