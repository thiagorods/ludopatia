package com.ludopatia.game.infrastructure.model.entities;

public class EstrategiaConexao {

    private final long idEstrategiaOrigem;
    private final long idEstrategiaDestino;

    public EstrategiaConexao(
            long idEstrategiaOrigem,
            long idEstrategiaDestino) {

        if (idEstrategiaOrigem <= 0 || idEstrategiaDestino <= 0) {
            throw new IllegalArgumentException(
                    "Os IDs das estratégias devem ser positivos."
            );
        }

        this.idEstrategiaOrigem = idEstrategiaOrigem;
        this.idEstrategiaDestino = idEstrategiaDestino;
    }

    public long getIdEstrategiaOrigem() {
        return idEstrategiaOrigem;
    }

    public long getIdEstrategiaDestino() {
        return idEstrategiaDestino;
    }
}
