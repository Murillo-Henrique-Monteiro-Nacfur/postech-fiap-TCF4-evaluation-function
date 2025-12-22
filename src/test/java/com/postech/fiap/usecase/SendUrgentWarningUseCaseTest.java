package com.postech.fiap.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.fiap.dto.EmailAvisoDTO;
import com.postech.fiap.exception.EvaluationFunctionException;
import com.postech.fiap.gateway.UrgentWarningGateway;
import com.postech.fiap.model.EvaluationEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendUrgentWarningUseCaseTest {

    @InjectMocks
    private SendUrgentWarningUseCase sendUrgentWarningUseCase;

    @Mock
    private UrgentWarningGateway urgentWarningGateway;

    @Test
    void shouldSendUrgencyNoticeSuccessfully() {
        EvaluationEntity evaluation = mock(EvaluationEntity.class);
        when(evaluation.getDescription()).thenReturn("Bad service");
        when(evaluation.getRating()).thenReturn(2);
        when(evaluation.getDateHourCriation()).thenReturn(LocalDateTime.of(2023, 10, 1, 12, 0));

        List<String> emails = List.of("admin1@test.com", "admin2@test.com");

        PanacheQuery queryMock = mock(PanacheQuery.class);
        PanacheQuery projectQueryMock = mock(PanacheQuery.class);

        doReturn(projectQueryMock).when(queryMock).project(String.class);
        doReturn(emails).when(projectQueryMock).list();

        try (MockedStatic<PanacheEntityBase> panacheMock = mockStatic(PanacheEntityBase.class)) {
            panacheMock.when(() -> PanacheEntityBase.find(anyString())).thenReturn(queryMock);
            panacheMock.when(() -> PanacheEntityBase.find(anyString(), any(Object[].class))).thenReturn(queryMock);

            sendUrgentWarningUseCase.enviaAvisoDeUrgencia(evaluation);

            ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
            verify(urgentWarningGateway).sendWarning(jsonCaptor.capture());

            String jsonCaptured = jsonCaptor.getValue();
            
            ObjectMapper objectMapper = new ObjectMapper();
            EmailAvisoDTO dto = objectMapper.readValue(jsonCaptured, EmailAvisoDTO.class);
            
            assertThat(dto.getDestinations()).containsExactlyElementsOf(emails);
            assertThat(dto.getSubject()).isEqualTo("[URGENTE] - Avaliação com nota baixa");
            assertThat(dto.getMessage()).contains("Bad service")
                                        .contains("2")
                                        .contains("2023-10-01T12:00");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldThrowEvaluationFunctionExceptionWhenJsonProcessingFails() {
        EvaluationEntity evaluation = mock(EvaluationEntity.class);
        when(evaluation.getDescription()).thenReturn("Bad service");
        when(evaluation.getRating()).thenReturn(2);
        when(evaluation.getDateHourCriation()).thenReturn(LocalDateTime.of(2023, 10, 1, 12, 0));

        List<String> emails = List.of("admin1@test.com");

        PanacheQuery queryMock = mock(PanacheQuery.class);
        PanacheQuery projectQueryMock = mock(PanacheQuery.class);

        doReturn(projectQueryMock).when(queryMock).project(String.class);
        doReturn(emails).when(projectQueryMock).list();

        try (MockedStatic<PanacheEntityBase> panacheMock = mockStatic(PanacheEntityBase.class);
             MockedConstruction<ObjectMapper> mockedObjectMapper = mockConstruction(ObjectMapper.class,
                     (mock, context) -> when(mock.writeValueAsString(any())).thenThrow(new JsonProcessingException("Error") {}))) {

            panacheMock.when(() -> PanacheEntityBase.find(anyString())).thenReturn(queryMock);
            panacheMock.when(() -> PanacheEntityBase.find(anyString(), any(Object[].class))).thenReturn(queryMock);

            assertThatThrownBy(() -> sendUrgentWarningUseCase.enviaAvisoDeUrgencia(evaluation))
                    .isInstanceOf(EvaluationFunctionException.class)
                    .hasMessage("Erro ao gerar aviso de urgencia")
                    .extracting("code").isEqualTo("JSON_PROCESSING_ERROR");

            verify(urgentWarningGateway, never()).sendWarning(anyString());
        }
    }
}