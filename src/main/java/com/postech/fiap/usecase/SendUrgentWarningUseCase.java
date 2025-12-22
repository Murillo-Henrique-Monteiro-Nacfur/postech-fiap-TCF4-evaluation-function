package com.postech.fiap.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.fiap.dto.EmailAvisoDTO;
import com.postech.fiap.exception.EvaluationFunctionException;
import com.postech.fiap.gateway.UrgentWarningGateway;
import com.postech.fiap.model.AdministratorEntity;
import com.postech.fiap.model.EvaluationEntity;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class SendUrgentWarningUseCase {

    private static final String SUBJECT_URGENT_EVALUATION_WITH_LOW_RATING = "[URGENTE] - Avaliação com nota baixa";
    private static final String MESSAGE_TEMPLATE = "Atenção! Uma nova avaliação com nota baixa foi registrada.\n Descrição: %s\n Nota: %d\n Data e Hora: %s";

    private final UrgentWarningGateway urgentWarningGateway;

    public SendUrgentWarningUseCase(UrgentWarningGateway urgentWarningGateway) {
        this.urgentWarningGateway = urgentWarningGateway;
    }

    public void enviaAvisoDeUrgencia(EvaluationEntity newEvaluation) {
        String message = String.format(MESSAGE_TEMPLATE,
                newEvaluation.getDescription(),
                newEvaluation.getRating(),
                newEvaluation.getDateHourCriation());

        List<String> emails = getEmailsAdministrators();
        EmailAvisoDTO emailAvisoDTO = new EmailAvisoDTO(emails, message, SUBJECT_URGENT_EVALUATION_WITH_LOW_RATING);
        String avisoUrgenciaJson;
        try {
            avisoUrgenciaJson = new ObjectMapper().writeValueAsString(emailAvisoDTO);
        } catch (JsonProcessingException e) {
            throw new EvaluationFunctionException("Erro ao gerar aviso de urgencia", "JSON_PROCESSING_ERROR");
        }
        urgentWarningGateway.sendWarning(avisoUrgenciaJson);

    }

    private List<String> getEmailsAdministrators() {
        return AdministratorEntity.find("SELECT a.email FROM AdministratorEntity a").project(String.class).list();
    }

}
