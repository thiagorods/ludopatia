package com.ludopatia.game.model.entities;

public class Noticia {

    private final long id;
    private final String titulo;
    private final String descricao;
    private final String tipo;

    public Noticia(long id, String titulo,
                   String descricao, String tipo) {

        // ID 0 representa uma notícia ainda não salva.
        if (id < 0) {
            throw new IllegalArgumentException("ID da notícia inválido.");
        }

        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Título inválido.");
        }

        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição inválida.");
        }

        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("Tipo inválido.");
        }

        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.tipo = tipo;
    }

    public long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getTipo() {
        return tipo;
    }
}
