package com.postech.fiap.dto;

import java.util.List;

public class EmailAvisoDTO {
    private List<String> destinations;
    private String message;
    private String subject;

    public EmailAvisoDTO(List<String> destinations, String message, String subject) {
        this.destinations = destinations;
        this.message = message;
        this.subject = subject;
    }
}
