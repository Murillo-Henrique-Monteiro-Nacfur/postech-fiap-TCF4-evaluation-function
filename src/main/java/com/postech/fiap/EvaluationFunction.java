package com.postech.fiap;

import com.postech.fiap.dto.EvaluationDTO;
import com.postech.fiap.exception.FunctionExceptionHandler;
import com.postech.fiap.model.EvaluationEntity;
import com.postech.fiap.usecase.CreateEvaluationUseCase;
import com.postech.fiap.usecase.SendUrgentWarningUseCase;
import io.quarkus.funqy.Funq;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@FunctionExceptionHandler
public class EvaluationFunction {


    private final CreateEvaluationUseCase createEvaluationUseCase;
    private final SendUrgentWarningUseCase sendUrgentWarningUseCase;

    public EvaluationFunction(CreateEvaluationUseCase createEvaluationUseCase, SendUrgentWarningUseCase sendUrgentWarningUseCase) {
        this.createEvaluationUseCase = createEvaluationUseCase;
        this.sendUrgentWarningUseCase = sendUrgentWarningUseCase;
    }

    @Funq
    public void processNewEvaluation(EvaluationDTO newEvaluation) {
        EvaluationEntity evaluation = new EvaluationEntity(newEvaluation.getDescricao(), newEvaluation.getNota());
        createEvaluationUseCase.createEvaluation(evaluation);
        if (newEvaluation.getNota() < 5) {
            sendUrgentWarningUseCase.enviaAvisoDeUrgencia(evaluation);
        }
    }
}