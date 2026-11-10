package com.ludopatia.game.model.entities;

public class PartidaPais {

    private final long idPartida;
    private final long idPais;

    private final long numPopulacao;
    private final long numExpostos;
    private final long numJogadores;
    private final long numJogadoresRecorrentes;
    private final long numJogadoresEmRisco;
    private final long numRecuperados;

    private final Double valorReacaoPais;

    public PartidaPais(
            long idPartida,
            long idPais,
            long numPopulacao,
            long numExpostos,
            long numJogadores,
            long numJogadoresRecorrentes,
            long numJogadoresEmRisco,
            long numRecuperados,
            Double valorReacaoPais) {

        if (idPartida <= 0 || idPais <= 0) {
            throw new IllegalArgumentException(
                    "IDs da partida e do país devem ser positivos."
            );
        }

        if (numPopulacao < 0 ||
                numExpostos < 0 ||
                numJogadores < 0 ||
                numJogadoresRecorrentes < 0 ||
                numJogadoresEmRisco < 0 ||
                numRecuperados < 0) {

            throw new IllegalArgumentException(
                    "Os estados populacionais não podem ser negativos."
            );
        }

        if (valorReacaoPais != null &&
                !Double.isFinite(valorReacaoPais)) {

            throw new IllegalArgumentException(
                    "O valor de reação do país deve ser finito."
            );
        }

        this.idPartida = idPartida;
        this.idPais = idPais;
        this.numPopulacao = numPopulacao;
        this.numExpostos = numExpostos;
        this.numJogadores = numJogadores;
        this.numJogadoresRecorrentes = numJogadoresRecorrentes;
        this.numJogadoresEmRisco = numJogadoresEmRisco;
        this.numRecuperados = numRecuperados;
        this.valorReacaoPais = valorReacaoPais;
    }

    public long getIdPartida() {
        return idPartida;
    }

    public long getIdPais() {
        return idPais;
    }

    public long getNumPopulacao() {
        return numPopulacao;
    }

    public long getNumExpostos() {
        return numExpostos;
    }

    public long getNumJogadores() {
        return numJogadores;
    }

    public long getNumJogadoresRecorrentes() {
        return numJogadoresRecorrentes;
    }

    public long getNumJogadoresEmRisco() {
        return numJogadoresEmRisco;
    }

    public long getNumRecuperados() {
        return numRecuperados;
    }

    public Double getValorReacaoPais() {
        return valorReacaoPais;
    }
}
