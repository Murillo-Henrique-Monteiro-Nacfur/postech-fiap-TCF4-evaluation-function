package com.postech.fiap.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class EvaluationDTOTest {

    @Inject
    ObjectMapper objectMapper;

    @Test
    void shouldDeserializeCorrectly() throws Exception {
        String json = "{\"descricao\":\"Excellent service\",\"nota\":10}";

        EvaluationDTO dto = objectMapper.readValue(json, EvaluationDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getDescricao()).isEqualTo("Excellent service");
        assertThat(dto.getNota()).isEqualTo(10);
    }
}