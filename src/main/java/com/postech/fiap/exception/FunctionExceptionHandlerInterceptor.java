package com.postech.fiap.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.http.HttpServerResponse;
import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.jboss.logging.Logger;

@FunctionExceptionHandler
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class FunctionExceptionHandlerInterceptor {

    Logger log;
    ObjectMapper objectMapper;
    HttpServerResponse response;

    public FunctionExceptionHandlerInterceptor(Logger log, ObjectMapper objectMapper, HttpServerResponse response) {
        this.log = log;
        this.objectMapper = objectMapper;
        this.response = response;
    }

    @AroundInvoke
    public Object handleException(InvocationContext context) throws Exception {
        try {
            return context.proceed();
        } catch (EvaluationFunctionException e) {
            ErrorDTO error = new ErrorDTO(e.getMessage(), e.getCode());
            response.setStatusCode(506)
                    .putHeader("Content-Type", "application/json")
                    .end(objectMapper.writeValueAsString(error));
            log.error("Business Error processed: " + objectMapper.writeValueAsString(error));
            return null;
        } catch (Exception e) {
            log.error("Unexpected Error processing function", e);
            throw e;
        }
    }
}