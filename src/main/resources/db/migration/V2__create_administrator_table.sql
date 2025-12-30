
-- V2__create_administrator_table.sql
-- Cria sequência e tabela para AdministratorEntity

CREATE SEQUENCE IF NOT EXISTS administrator_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS administrator (
    id BIGINT PRIMARY KEY DEFAULT nextval('administrator_id_seq'),
    name TEXT,
    email TEXT
);
