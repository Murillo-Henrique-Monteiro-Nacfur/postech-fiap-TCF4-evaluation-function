package com.postech.fiap.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.fiap.dto.EmailAvisoDTO;
import com.postech.fiap.exception.EvaluationFunctionException;
import com.postech.fiap.gateway.UrgentWarningGateway;
import com.postech.fiap.model.AdministratorEntity;
import com.postech.fiap.model.EvaluationEntity;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ApplicationScoped
public class SendUrgentWarningUseCase {

    private static final String SUBJECT_URGENT_EVALUATION_WITH_LOW_RATING = "[URGENTE] - Avaliação com nota baixa";
    private static final String MESSAGE_TEMPLATE = "Atenção! Uma nova avaliação com nota baixa foi registrada.\n Descrição: %s\n Nota: %d\n Data e Hora: %s";
    private static final String DD_MM_YYYY_HH_MM_SS = "dd/MM/yyyy HH:mm:ss";
    private static final String DATE_NOT_AVAILABLE = "Data não disponível";

    private final UrgentWarningGateway urgentWarningGateway;

    public SendUrgentWarningUseCase(UrgentWarningGateway urgentWarningGateway) {
        this.urgentWarningGateway = urgentWarningGateway;
    }

    public void enviaAvisoDeUrgencia(EvaluationEntity newEvaluation) {
        String message = String.format(MESSAGE_TEMPLATE,
                newEvaluation.getDescription(),
                newEvaluation.getRating(),
                formatDate(newEvaluation.getDateHourCriation()));

        List<String> emails = getEmailsAdministrators();
        EmailAvisoDTO emailAvisoDTO = new EmailAvisoDTO(emails, message, SUBJECT_URGENT_EVALUATION_WITH_LOW_RATING);
        String avisoUrgenciaJson = getAvisoUrgenciaJson(emailAvisoDTO);
        urgentWarningGateway.sendWarning(avisoUrgenciaJson);

    }

    private String getAvisoUrgenciaJson(EmailAvisoDTO emailAvisoDTO) {
        try {
            return new ObjectMapper().writeValueAsString(emailAvisoDTO);
        } catch (JsonProcessingException e) {
            throw new EvaluationFunctionException("Erro ao gerar aviso de urgencia", "JSON_PROCESSING_ERROR");
        }
    }

    private List<String> getEmailsAdministrators() {
        return AdministratorEntity.find("SELECT a.email FROM AdministratorEntity a").project(String.class).list();
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return DATE_NOT_AVAILABLE;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DD_MM_YYYY_HH_MM_SS);
        return dateTime.format(formatter);
    }

}
