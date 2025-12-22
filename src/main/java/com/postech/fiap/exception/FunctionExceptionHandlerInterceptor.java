package com.postech.fiap.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.jboss.logging.Logger;

@FunctionExceptionHandler
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class FunctionExceptionHandlerInterceptor {

    @Inject
    Logger log;

    @Inject
    ObjectMapper objectMapper;

    @AroundInvoke
    public Object handleException(InvocationContext context) throws Exception {
        try {
            return context.proceed();
        } catch (EvaluationFunctionException e) {
            ErrorDTO error = new ErrorDTO(e.getMessage(), e.getCode());
            log.error("Business Error processed: " + objectMapper.writeValueAsString(error));
            return null;
        } catch (Exception e) {
            log.error("Unexpected Error processing function", e);
            throw e;
        }
    }
}