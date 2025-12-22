package com.postech.fiap.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.interceptor.InvocationContext;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FunctionExceptionHandlerInterceptorTest {

    @InjectMocks
    private FunctionExceptionHandlerInterceptor interceptor;

    @Mock
    private Logger log;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private InvocationContext context;

    @BeforeEach
    void setUp() throws Exception {
        setPrivateField(interceptor, "log", log);
        setPrivateField(interceptor, "objectMapper", objectMapper);
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void shouldProceedSuccessfully() throws Exception {
        Object expectedResult = "Success";
        when(context.proceed()).thenReturn(expectedResult);

        Object result = interceptor.handleException(context);

        assertThat(result).isEqualTo(expectedResult);
        verify(context, times(1)).proceed();
        verifyNoInteractions(log);
    }

    @Test
    void shouldHandleEvaluationFunctionException() throws Exception {
        String errorMessage = "Business error";
        String errorCode = "ERR_001";
        EvaluationFunctionException exception = new EvaluationFunctionException(errorMessage, errorCode);

        when(context.proceed()).thenThrow(exception);
        when(objectMapper.writeValueAsString(any(ErrorDTO.class))).thenReturn("{\"message\":\"Business error\"}");

        Object result = interceptor.handleException(context);

        assertThat(result).isNull();
        verify(log).error(contains("Business Error processed: {\"message\":\"Business error\"}"));
        verify(objectMapper).writeValueAsString(any(ErrorDTO.class));
    }

    @Test
    void shouldRethrowUnexpectedException() throws Exception {
        RuntimeException exception = new RuntimeException("Unexpected error");
        when(context.proceed()).thenThrow(exception);

        assertThatThrownBy(() -> interceptor.handleException(context))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Unexpected error");

        verify(log).error("Unexpected Error processing function",exception);
    }
}