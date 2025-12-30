-- V1__create_evaluation_table.sql
-- Criar sequência e tabela básica para EvaluationEntity

CREATE SEQUENCE IF NOT EXISTS evaluation_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS evaluation (
    id BIGINT PRIMARY KEY DEFAULT nextval('evaluation_id_seq'),
    description TEXT,
    rating INTEGER NOT NULL,
    date_hour_creation TIMESTAMP
);
