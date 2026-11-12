package com.ludopatia.game.infrastructure.model.entities;

public class Estrategia {

    private final long id;
    private final long idTipo;

    private final String titulo;
    private final String descricao;
    private final String icone;

    private final int custoInfluencia;
    private final Integer custoCapital;

    private final double valorExposicao;
    private final double valorConversao;
    private final double valorRetencao;
    private final double valorAgravamento;
    private final double valorRecuperacao;
    private final double valorConscientizacao;
    private final double valorExpansao;

    public Estrategia(
            long id,
            long idTipo,
            String titulo,
            String descricao,
            String icone,
            int custoInfluencia,
            Integer custoCapital,
            double valorExposicao,
            double valorConversao,
            double valorRetencao,
            double valorAgravamento,
            double valorRecuperacao,
            double valorConscientizacao,
            double valorExpansao) {

        if (id <= 0 || idTipo <= 0) {
            throw new IllegalArgumentException("IDs inválidos.");
        }

        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Título inválido.");
        }

        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição inválida.");
        }

        if (custoInfluencia < 0) {
            throw new IllegalArgumentException("Custo de influência inválido.");
        }

        if (custoCapital != null && custoCapital < 0) {
            throw new IllegalArgumentException("Custo de capital inválido.");
        }

        double[] efeitos = {
                valorExposicao, valorConversao, valorRetencao,
                valorAgravamento, valorRecuperacao,
                valorConscientizacao, valorExpansao
        };

        for (double efeito : efeitos) {
            if (!Double.isFinite(efeito)) {
                throw new IllegalArgumentException("Efeito numérico inválido.");
            }
        }

        if (valorExposicao < 0 || valorExposicao > 1) {
            throw new IllegalArgumentException(
                    "Exposição deve estar entre 0 e 1."
            );
        }

        this.id = id;
        this.idTipo = idTipo;
        this.titulo = titulo;
        this.descricao = descricao;
        this.icone = icone;
        this.custoInfluencia = custoInfluencia;
        this.custoCapital = custoCapital;
        this.valorExposicao = valorExposicao;
        this.valorConversao = valorConversao;
        this.valorRetencao = valorRetencao;
        this.valorAgravamento = valorAgravamento;
        this.valorRecuperacao = valorRecuperacao;
        this.valorConscientizacao = valorConscientizacao;
        this.valorExpansao = valorExpansao;
    }

    public long getId() { return id; }

    public long getIdTipo() { return idTipo; }

    public String getTitulo() { return titulo; }

    public String getDescricao() { return descricao; }

    public String getIcone() { return icone; }

    public int getCustoInfluencia() { return custoInfluencia; }

    public Integer getCustoCapital() { return custoCapital; }

    public double getValorExposicao() { return valorExposicao; }

    public double getValorConversao() { return valorConversao; }

    public double getValorRetencao() { return valorRetencao; }

    public double getValorAgravamento() { return valorAgravamento; }

    public double getValorRecuperacao() { return valorRecuperacao; }

    public double getValorConscientizacao() {
        return valorConscientizacao;
    }

    public double getValorExpansao() { return valorExpansao; }
}
