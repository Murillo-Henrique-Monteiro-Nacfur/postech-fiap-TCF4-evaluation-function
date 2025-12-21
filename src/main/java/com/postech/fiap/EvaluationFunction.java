package com.postech.fiap;

import com.postech.fiap.dto.EvaluationDTO;
import com.postech.fiap.model.EvaluationEntity;
import com.postech.fiap.usecase.CreateEvaluationUseCase;
import com.postech.fiap.usecase.EnviaAvisoDeUrgenciaUseCase;
import io.quarkus.funqy.Funq;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EvaluationFunction {


    private final CreateEvaluationUseCase createEvaluationUseCase;
    private final EnviaAvisoDeUrgenciaUseCase enviaAvisoDeUrgenciaUseCase;

    public EvaluationFunction(CreateEvaluationUseCase createEvaluationUseCase, EnviaAvisoDeUrgenciaUseCase enviaAvisoDeUrgenciaUseCase) {
        this.createEvaluationUseCase = createEvaluationUseCase;
        this.enviaAvisoDeUrgenciaUseCase = enviaAvisoDeUrgenciaUseCase;
    }

    @Funq
    public void processNewEvaluation(EvaluationDTO newEvaluation) {
        EvaluationEntity evaluation = new EvaluationEntity(newEvaluation.getDescricao(), newEvaluation.getNota());
        createEvaluationUseCase.createEvaluation(evaluation);
        if (newEvaluation.getNota() < 5) {
            enviaAvisoDeUrgenciaUseCase.enviaAvisoDeUrgencia(evaluation);
        }
    }
}
