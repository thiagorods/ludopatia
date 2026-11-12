package com.ludopatia.game.infrastructure.model.entities;

public class Partida {

    private final long id;
    private final int idPaisInicial;

    private final String nomeEmpresa;
    private final StatusPartida status;

    private final int ciclo;
    private final int valorInfluencia;
    private final Integer valorCapital;

    public Partida(
            long id,
            int idPaisInicial,
            String nomeEmpresa,
            StatusPartida status,
            int ciclo,
            int valorInfluencia,
            Integer valorCapital) {

        // O ID 0 representa uma partida ainda não salva.
        if (id < 0) {
            throw new IllegalArgumentException("ID da partida inválido.");
        }

        if (idPaisInicial <= 0) {
            throw new IllegalArgumentException("ID do país inválido.");
        }

        if (nomeEmpresa == null || nomeEmpresa.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome da empresa não pode estar vazio."
            );
        }

        if (status == null) {
            throw new IllegalArgumentException("Status inválido.");
        }

        if (ciclo < 0) {
            throw new IllegalArgumentException("Ciclo inválido.");
        }

        if (valorInfluencia < 0) {
            throw new IllegalArgumentException(
                    "A Influência não pode ser negativa."
            );
        }

        if (valorCapital != null && valorCapital < 0) {
            throw new IllegalArgumentException(
                    "O Capital não pode ser negativo."
            );
        }

        this.id = id;
        this.idPaisInicial = idPaisInicial;
        this.nomeEmpresa = nomeEmpresa;
        this.status = status;
        this.ciclo = ciclo;
        this.valorInfluencia = valorInfluencia;
        this.valorCapital = valorCapital;
    }

    public long getId() {
        return id;
    }

    public int getIdPaisInicial() {
        return idPaisInicial;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public StatusPartida getStatus() {
        return status;
    }

    public int getCiclo() {
        return ciclo;
    }

    public int getValorInfluencia() {
        return valorInfluencia;
    }

    public Integer getValorCapital() {
        return valorCapital;
    }
}
