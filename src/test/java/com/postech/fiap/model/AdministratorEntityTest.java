package com.postech.fiap.model;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
class AdministratorEntityTest {

    @Test
    void shouldCreateAdministratorEntityWithNoArgs() {
        AdministratorEntity admin = new AdministratorEntity();

        assertThat(admin).isNotNull();
        assertThat(admin.getId()).isNull();
        assertThat(admin.getName()).isNull();
        assertThat(admin.getEmail()).isNull();
    }

    @Test
    void shouldSetAndGetValuesCorrectly() {
        AdministratorEntity admin = new AdministratorEntity();
        Long id = 1L;
        String name = "Admin User";
        String email = "admin@example.com";

        admin.setId(id);
        admin.setName(name);
        admin.setEmail(email);

        assertThat(admin.getId()).isEqualTo(id);
        assertThat(admin.getName()).isEqualTo(name);
        assertThat(admin.getEmail()).isEqualTo(email);
    }
}