package com.postech.fiap.gateway;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class GooglePubSubGateway implements AvisoUrgenciaGateway {

    private static final Logger log = Logger.getLogger(GooglePubSubGateway.class);

    @ConfigProperty(name = "pubsub.topic.name")
    String topicName;

    @ConfigProperty(name = "quarkus.google.cloud.project-id")
    String projectId;

    private Publisher publisher;

    private Publisher getPublisher() throws IOException {
        if (publisher == null) {
            publisher = Publisher.newBuilder(ProjectTopicName.of(projectId, topicName)).build();
        }
        return publisher;
    }

    @Override
    public void enviarAviso(String mensagem) {
        try {
            ByteString byteStr = ByteString.copyFrom(mensagem, StandardCharsets.UTF_8);
            PubsubMessage pubsubApiMessage = PubsubMessage.newBuilder().setData(byteStr).build();

            getPublisher().publish(pubsubApiMessage).get();
            log.info("Mensagem publicada com sucesso: " + mensagem);

        } catch (Exception e) {
            log.error("Erro ao publicar mensagem no PubSub", e);
        }
    }

    @PreDestroy
    public void cleanup() {
        if (publisher != null) {
            try {
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                log.error("Erro ao fechar publisher", e);
            }
        }
    }
}
