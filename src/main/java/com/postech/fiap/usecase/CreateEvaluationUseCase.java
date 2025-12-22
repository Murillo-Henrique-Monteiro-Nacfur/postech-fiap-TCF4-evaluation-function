package com.postech.fiap.usecase;

import com.postech.fiap.exception.EvaluationFunctionException;
import com.postech.fiap.model.EvaluationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateEvaluationUseCase {

    @Transactional
    public void createEvaluation(EvaluationEntity evaluation) {
        validateEvaluation(evaluation);
        evaluation.persist();
    }

    private void validateEvaluation(EvaluationEntity evaluation) {
        if (evaluation.getRating() < 0 || evaluation.getRating() > 10) {
            throw new EvaluationFunctionException("A nota deve estar entre 0 e 10.", "INVALID_RATING");
        }
        if (evaluation.getDescription() == null || evaluation.getDescription().isEmpty()) {
            throw new EvaluationFunctionException("A descrição não pode ser vazia.", "INVALID_DESCRIPTION");
        }
    }

}
