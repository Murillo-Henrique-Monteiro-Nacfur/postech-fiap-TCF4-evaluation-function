package com.postech.fiap.config;

import io.quarkus.vertx.http.runtime.CurrentVertxRequest;
import io.vertx.core.http.HttpServerResponse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;

public class VertxProducers {

    CurrentVertxRequest currentVertxRequest;

    public VertxProducers(CurrentVertxRequest currentVertxRequest) {
        this.currentVertxRequest = currentVertxRequest;
    }

    @Produces
    @RequestScoped
    public HttpServerResponse produceResponse() {
        return currentVertxRequest.getCurrent().response();
    }
}
