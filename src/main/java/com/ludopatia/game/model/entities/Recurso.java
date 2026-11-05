
package com.ludopatia.game.model.entities;

public class Recurso {

    private final long id;
    private final String nome;
    private final String descricao;
    private final int valorInicial;

    public Recurso(long id, String nome, String descricao, int valorInicial) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID do recurso inválido.");
        }

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do recurso inválido.");
        }

        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição do recurso inválida.");
        }

        if (valorInicial < 0) {
            throw new IllegalArgumentException(
                    "O valor inicial não pode ser negativo."
            );
        }

        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.valorInicial = valorInicial;
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

    public int getValorInicial() {
        return valorInicial;
    }
}
