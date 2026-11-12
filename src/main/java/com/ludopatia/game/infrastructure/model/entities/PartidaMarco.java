package com.ludopatia.game.infrastructure.model.entities;

public class PartidaMarco {

    private final long idPartida;
    private final long idMarco;
    private final int cicloConcluido;

    public PartidaMarco(
            long idPartida,
            long idMarco,
            int cicloConcluido) {

        if (idPartida <= 0 || idMarco <= 0) {
            throw new IllegalArgumentException(
                    "IDs da partida e do marco devem ser positivos."
            );
        }

        if (cicloConcluido < 0) {
            throw new IllegalArgumentException(
                    "O ciclo não pode ser negativo."
            );
        }

        this.idPartida = idPartida;
        this.idMarco = idMarco;
        this.cicloConcluido = cicloConcluido;
    }

    public long getIdPartida() {
        return idPartida;
    }

    public long getIdMarco() {
        return idMarco;
    }

    public int getCicloConcluido() {
        return cicloConcluido;
    }
}
