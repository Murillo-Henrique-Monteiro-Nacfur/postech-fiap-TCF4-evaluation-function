package com.postech.fiap.java;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class EvaluationFunctionTest {

    @Inject
    EvaluationFunction function; // Injeta a função diretamente

    @Test
    void testOnCloudEvent() {
        // 1. Prepara um CloudEvent de exemplo usando o builder
//        CloudEvent event = CloudEvent.Builder()
//                .withId(UUID.randomUUID().toString())
//                .withSource(URI.create("//storage.googleapis.com/projects/_/buckets/MY-BUCKET-NAME"))
//                .withType("google.cloud.storage.object.v1.finalized")
//                .withSubject("objects/MY_FILE.txt")
//                .withData("{\"bucket\":\"MY_BUCKET\",\"name\":\"MY_FILE.txt\"}".getBytes())
//                .build();

        // 2. Chama o método diretamente
        // 3. Verifica se nenhuma exceção é lançada
        //assertDoesNotThrow(() -> function.onCloudEvent(event));
    }
}
