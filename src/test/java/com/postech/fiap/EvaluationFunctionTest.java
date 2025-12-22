package com.postech.fiap;

import com.postech.fiap.dto.EvaluationDTO;
import com.postech.fiap.model.EvaluationEntity;
import com.postech.fiap.usecase.CreateEvaluationUseCase;
import com.postech.fiap.usecase.SendUrgentWarningUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluationFunctionTest {

    @InjectMocks
    private EvaluationFunction evaluationFunction;

    @Mock
    private CreateEvaluationUseCase createEvaluationUseCase;

    @Mock
    private SendUrgentWarningUseCase sendUrgentWarningUseCase;

    @Test
    void shouldCreateEvaluationAndSendUrgencyNoticeWhenScoreIsLessThanFive() {
        EvaluationDTO evaluationDTO = mock(EvaluationDTO.class);
        when(evaluationDTO.getDescricao()).thenReturn("Bad service");
        when(evaluationDTO.getNota()).thenReturn(4);

        evaluationFunction.processNewEvaluation(evaluationDTO);

        ArgumentCaptor<EvaluationEntity> evaluationCaptor = ArgumentCaptor.forClass(EvaluationEntity.class);
        verify(createEvaluationUseCase).createEvaluation(evaluationCaptor.capture());

        EvaluationEntity capturedEvaluation = evaluationCaptor.getValue();
        assertThat(capturedEvaluation.getDescription()).isEqualTo("Bad service");
        assertThat(capturedEvaluation.getRating()).isEqualTo(4);

        verify(sendUrgentWarningUseCase).enviaAvisoDeUrgencia(any(EvaluationEntity.class));
    }

    @Test
    void shouldCreateEvaluationAndNotSendUrgencyNoticeWhenScoreIsFiveOrMore() {
        EvaluationDTO evaluationDTO = mock(EvaluationDTO.class);
        when(evaluationDTO.getDescricao()).thenReturn("Good service");
        when(evaluationDTO.getNota()).thenReturn(5);

        evaluationFunction.processNewEvaluation(evaluationDTO);

        ArgumentCaptor<EvaluationEntity> evaluationCaptor = ArgumentCaptor.forClass(EvaluationEntity.class);
        verify(createEvaluationUseCase).createEvaluation(evaluationCaptor.capture());

        EvaluationEntity capturedEvaluation = evaluationCaptor.getValue();
        assertThat(capturedEvaluation.getDescription()).isEqualTo("Good service");
        assertThat(capturedEvaluation.getRating()).isEqualTo(5);

        verify(sendUrgentWarningUseCase, never()).enviaAvisoDeUrgencia(any(EvaluationEntity.class));
    }
}