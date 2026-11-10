package com.ludopatia.game.model.entities;

public class PartidaEstrategia {

    private final long idPartida;
    private final long idEstrategia;

    public PartidaEstrategia(long idPartida, long idEstrategia) {

        if (idPartida <= 0) {
            throw new IllegalArgumentException(
                    "ID da partida inválido."
            );
        }

        if (idEstrategia <= 0) {
            throw new IllegalArgumentException(
                    "ID da estratégia inválido."
            );
        }

        this.idPartida = idPartida;
        this.idEstrategia = idEstrategia;
    }

    public long getIdPartida() {
        return idPartida;
    }

    public long getIdEstrategia() {
        return idEstrategia;
    }
}
