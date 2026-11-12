package com.ludopatia.game.infrastructure.model.entities;

public class PartidaNoticia {

    private final long idPartida;
    private final long idNoticia;
    private final int cicloGerado;

    public PartidaNoticia(
            long idPartida,
            long idNoticia,
            int cicloGerado) {

        if (idPartida <= 0 || idNoticia <= 0) {
            throw new IllegalArgumentException(
                    "IDs da partida e da notícia devem ser positivos."
            );
        }

        if (cicloGerado < 0) {
            throw new IllegalArgumentException(
                    "O ciclo não pode ser negativo."
            );
        }

        this.idPartida = idPartida;
        this.idNoticia = idNoticia;
        this.cicloGerado = cicloGerado;
    }

    public long getIdPartida() {
        return idPartida;
    }

    public long getIdNoticia() {
        return idNoticia;
    }

    public int getCicloGerado() {
        return cicloGerado;
    }
}
