package com.postech.fiap.gateway;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GooglePubSubGatewayTest {

    @InjectMocks
    private GooglePubSubGateway googlePubSubGateway;

    @Mock
    private Publisher publisher;

    @Mock
    private ApiFuture<String> apiFuture;

    @BeforeEach
    void setUp() throws Exception {
        setPrivateField(googlePubSubGateway, "topicName", "test-topic");
        setPrivateField(googlePubSubGateway, "projectId", "test-project");
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void shouldInitializePublisherWhenNull() throws IOException {
        try {
            setPrivateField(googlePubSubGateway, "publisher", null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try (MockedStatic<ProjectTopicName> projectTopicNameMock = mockStatic(ProjectTopicName.class);
             MockedStatic<Publisher> publisherMock = mockStatic(Publisher.class)) {

            ProjectTopicName topicName = ProjectTopicName.of("test-project", "test-topic");
            projectTopicNameMock.when(() -> ProjectTopicName.of("test-project", "test-topic")).thenReturn(topicName);

            Publisher.Builder builderMock = mock(Publisher.Builder.class);
            publisherMock.when(() -> Publisher.newBuilder(topicName)).thenReturn(builderMock);
            when(builderMock.build()).thenReturn(publisher);
            when(publisher.publish(any(PubsubMessage.class))).thenReturn(apiFuture);

            googlePubSubGateway.sendWarning("Test message");

            publisherMock.verify(() -> Publisher.newBuilder(topicName), times(1));
            verify(builderMock, times(1)).build();
            verify(publisher, times(1)).publish(any(PubsubMessage.class));
        }
    }

    @Test
    void shouldReuseExistingPublisher() throws Exception {
        setPrivateField(googlePubSubGateway, "publisher", publisher);
        when(publisher.publish(any(PubsubMessage.class))).thenReturn(apiFuture);

        googlePubSubGateway.sendWarning("Test message");

        verify(publisher, times(1)).publish(any(PubsubMessage.class));
    }

    @Test
    void shouldHandleInterruptedExceptionOnCleanup() throws Exception {
        setPrivateField(googlePubSubGateway, "publisher", publisher);
        doThrow(new InterruptedException("Cleanup failed")).when(publisher).awaitTermination(anyLong(), any(TimeUnit.class));

        googlePubSubGateway.cleanup();

        verify(publisher, times(1)).shutdown();
        verify(publisher, times(1)).awaitTermination(1, TimeUnit.MINUTES);
    }

    @Test
    void shouldPublishMessageSuccessfully() throws Exception {
        setPrivateField(googlePubSubGateway, "publisher", publisher);
        String message = "Test message";
        when(publisher.publish(any(PubsubMessage.class))).thenReturn(apiFuture);
        when(apiFuture.get()).thenReturn("message-id");

        googlePubSubGateway.sendWarning(message);

        verify(publisher, times(1)).publish(any(PubsubMessage.class));
        verify(apiFuture, times(1)).get();
    }

    @Test
    void shouldHandleExceptionWhenPublishingFails() throws Exception {
        setPrivateField(googlePubSubGateway, "publisher", publisher);
        String message = "Test message";
        when(publisher.publish(any(PubsubMessage.class))).thenReturn(apiFuture);
        when(apiFuture.get()).thenThrow(new InterruptedException("Publish failed"));

        googlePubSubGateway.sendWarning(message);

        verify(publisher, times(1)).publish(any(PubsubMessage.class));
    }

    @Test
    void shouldShutdownPublisherOnCleanup() throws Exception {
        setPrivateField(googlePubSubGateway, "publisher", publisher);

        googlePubSubGateway.cleanup();

        verify(publisher, times(1)).shutdown();
        verify(publisher, times(1)).awaitTermination(anyLong(), any());
    }
}