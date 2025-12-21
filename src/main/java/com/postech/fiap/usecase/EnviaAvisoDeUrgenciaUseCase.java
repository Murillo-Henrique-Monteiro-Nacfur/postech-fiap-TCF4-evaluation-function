package com.postech.fiap.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.fiap.dto.EmailAvisoDTO;
import com.postech.fiap.gateway.AvisoUrgenciaGateway;
import com.postech.fiap.model.EvaluationEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class EnviaAvisoDeUrgenciaUseCase {

    private final AvisoUrgenciaGateway avisoUrgenciaGateway;

    public EnviaAvisoDeUrgenciaUseCase(AvisoUrgenciaGateway avisoUrgenciaGateway) {
        this.avisoUrgenciaGateway = avisoUrgenciaGateway;
    }

    public void enviaAvisoDeUrgencia(EvaluationEntity newEvaluation) {
        String message = "Atenção! Uma nova avaliação com nota baixa foi registrada.\n" +
                "Descrição: " + newEvaluation.getDescription() + "\n" +
                "Nota: " + newEvaluation.getRating() + "\n" +
                "Data e Hora: " + newEvaluation.getDateHourCriation();
        List<String> emails = PanacheEntityBase.find("SELECT a.email FROM AdministratorEntity a").project(String.class).list();
        EmailAvisoDTO emailAvisoDTO = new EmailAvisoDTO(emails, message, "[URGENTE] - Avaliação com nota baixa");
        String avisoUrgenciaJson;
        try {
            avisoUrgenciaJson = new ObjectMapper().writeValueAsString(emailAvisoDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter EmailAvisoDTO para JSON", e);
        }
        avisoUrgenciaGateway.enviarAviso(avisoUrgenciaJson);

    }

}
