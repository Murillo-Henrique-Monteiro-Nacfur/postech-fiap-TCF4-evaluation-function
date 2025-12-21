package com.postech.fiap.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "administrator")
public class AdministratorEntity extends PanacheEntityBase {

    @Id
    @SequenceGenerator(
            name = "administratorSequence",
            sequenceName = "administrator_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "administratorSequence")
    public Long id;

    private String name;
    private String email;

    public AdministratorEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
