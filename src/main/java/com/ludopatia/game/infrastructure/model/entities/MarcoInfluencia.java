package com.ludopatia.game.infrastructure.model.entities;

import java.util.Objects;

public class MarcoInfluencia {

    private final long id;
    private final String nome;
    private final String descricao;
    private final TipoCondicaoMarco tipoCondicao;
    private final int valorCondicao;
    private final int valorRecompensa;

    public MarcoInfluencia(
            long id,
            String nome,
            String descricao,
            TipoCondicaoMarco tipoCondicao,
            int valorCondicao,
            int valorRecompensa) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "ID do marco inválido."
            );
        }

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "Nome do marco inválido."
            );
        }

        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException(
                    "Descrição do marco inválida."
            );
        }

        if (valorCondicao <= 0) {
            throw new IllegalArgumentException(
                    "O valor da condição deve ser positivo."
            );
        }

        if (valorRecompensa < 0) {
            throw new IllegalArgumentException(
                    "A recompensa não pode ser negativa."
            );
        }

        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.tipoCondicao = Objects.requireNonNull(tipoCondicao);
        this.valorCondicao = valorCondicao;
        this.valorRecompensa = valorRecompensa;
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoCondicaoMarco getTipoCondicao() {
        return tipoCondicao;
    }

    public int getValorCondicao() {
        return valorCondicao;
    }

    public int getValorRecompensa() {
        return valorRecompensa;
    }
}
