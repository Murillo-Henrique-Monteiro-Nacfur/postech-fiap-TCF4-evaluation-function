package com.postech.fiap.config;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class TimezoneConfigTest {

    @Inject
    TimezoneConfig timezoneConfig;

    private TimeZone originalTimeZone;

    @BeforeEach
    void setUp() {
        originalTimeZone = TimeZone.getDefault();
    }

    @AfterEach
    void tearDown() {
        TimeZone.setDefault(originalTimeZone);
    }

    @Test
    void shouldSetTimeZoneToSaoPauloOnStart() {
        timezoneConfig.onStart(new StartupEvent());

        assertThat(TimeZone.getDefault().getID()).isEqualTo("America/Sao_Paulo");
    }
}