package com.postech.fiap.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import java.time.LocalDateTime;

@Entity
@Table(name = "evaluation")
public class EvaluationEntity extends PanacheEntityBase {

    @Id
    @SequenceGenerator(
        name = "evaluationSequence",
        sequenceName = "evaluation_id_seq",
        allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "evaluationSequence")
    public Long id;

    private String description;
    private int rating;

    @Column(name = "date_hour_creation")
    private LocalDateTime dateHourCriation;

    public EvaluationEntity() {
    }

    public EvaluationEntity(String description, int rating) {
        this.description = description;
        this.rating = rating;
        this.dateHourCriation = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public int getRating() {
        return rating;
    }

    public LocalDateTime getDateHourCriation() {
        return dateHourCriation;
    }
}
