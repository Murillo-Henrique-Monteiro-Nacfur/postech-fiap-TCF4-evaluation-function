package com.postech.fiap.usecase;

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
            throw new IllegalArgumentException("A nota deve estar entre 0 e 10.");
        }
        if (evaluation.getDescription() == null || evaluation.getDescription().isEmpty()) {
            throw new IllegalArgumentException("A descrição não pode ser vazia.");
        }
    }

}
