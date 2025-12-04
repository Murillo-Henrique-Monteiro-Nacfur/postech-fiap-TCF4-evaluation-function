package com.postech.fiap.java;

import io.quarkus.funqy.Funq;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Base64;
import java.util.Map;

@ApplicationScoped
public class EvaluationFunction {

    // Classe para representar a estrutura externa da mensagem do Pub/Sub
    public static class PubSubMessage {
        public Message message;
        public String subscription;
    }

    // Classe para representar o conteúdo da mensagem
    public static class Message {
        public String data; // O dado vem codificado em Base64
        public Map<String, String> attributes;
        public String messageId;
    }

    /**
     * Esta função é acionada pelo Funqy e processa o wrapper JSON padrão do Pub/Sub.
     * O Funqy mapeia automaticamente o JSON de entrada para o objeto PubSubMessage.
     * @param payload O payload do Pub/Sub.
     */
    @Funq
    public void processPubSubMessage(PubSubMessage payload) {
        if (payload == null || payload.message == null) {
            System.out.println("Payload ou mensagem nulos.");
            return;
        }

        System.out.println("Receive event Id: " + payload.message.messageId);
        System.out.println("Subscription: " + payload.subscription);

        // Decodifica a mensagem de Base64 para String
        String decodedData = new String(Base64.getDecoder().decode(payload.message.data));
        System.out.println("Receive event Data: " + decodedData);
    }
}
