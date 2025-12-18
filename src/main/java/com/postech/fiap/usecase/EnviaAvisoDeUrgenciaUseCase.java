package com.postech.fiap.usecase;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class EnviaAvisoDeUrgenciaUseCase {
    private static final Logger log = Logger.getLogger(EnviaAvisoDeUrgenciaUseCase.class);

    public void enviaAvisoDeUrgencia() {
        log.info("Enviou aviso de urgencia");

        String topicName = "topic-email-sender";
        String projectId = "neon-trilogy-479114-v8";
        String message = "teste";

        try {

            // Create the PubsubMessage object
            // (This is different than the PubsubMessage POJO used in Pub/Sub-triggered functions)
            ByteString byteStr = ByteString.copyFrom(message, StandardCharsets.UTF_8);
            PubsubMessage pubsubApiMessage = PubsubMessage.newBuilder().setData(byteStr).build();

            Publisher publisher = Publisher.newBuilder(
                    ProjectTopicName.of(projectId, topicName)).build();

            // Attempt to publish the message
            String responseMessage;
            try {
                publisher.publish(pubsubApiMessage).get();
                responseMessage = "Message published.";
            } catch (InterruptedException | ExecutionException e) {
                log.info( "Error publishing Pub/Sub message: " + e.getMessage(), e);
                responseMessage = "Error publishing Pub/Sub message; see logs for more info.";
            } finally {
                if (publisher != null) {
                    // When finished with the publisher, shutdown to free up resources.
                    publisher.shutdown();
                    publisher.awaitTermination(1, TimeUnit.MINUTES);
                }
            }
        } catch (InterruptedException | IOException e) {
            log.error(e.getMessage(), e);
        }
        //responseWriter.write(responseMessage);


    }

}
