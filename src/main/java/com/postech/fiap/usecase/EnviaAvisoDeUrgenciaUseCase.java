package com.postech.fiap.usecase;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EnviaAvisoDeUrgenciaUseCase {

    public void enviaAvisoDeUrgencia() {
        Log.info("Enviou aviso de urgencia");
    }

}
