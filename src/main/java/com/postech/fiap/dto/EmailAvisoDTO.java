package com.postech.fiap.dto;

import java.util.List;

public class EmailAvisoDTO {
    private List<String> destinations;
    private String message;
    private String subject;

    public EmailAvisoDTO() {
    }

    public EmailAvisoDTO(List<String> destinations, String message, String subject) {
        this.destinations = destinations;
        this.message = message;
        this.subject = subject;
    }

    public List<String> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<String> destinations) {
        this.destinations = destinations;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
