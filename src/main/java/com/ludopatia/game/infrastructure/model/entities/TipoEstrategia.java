package com.ludopatia.game.infrastructure.model.entities;

public class TipoEstrategia {

    private final long id;
    private final String nome;
    private final String descricao;

    public TipoEstrategia(long id, String nome, String descricao) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID do tipo inválido.");
        }

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do tipo inválido.");
        }

        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição do tipo inválida.");
        }

        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
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
}
