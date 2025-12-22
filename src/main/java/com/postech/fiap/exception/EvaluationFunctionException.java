package com.postech.fiap.exception;

public class EvaluationFunctionException extends RuntimeException {

    private final String code;

    public EvaluationFunctionException(String message) {
        super(message);
        this.code = "BUSINESS_ERROR";
    }

    public EvaluationFunctionException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}