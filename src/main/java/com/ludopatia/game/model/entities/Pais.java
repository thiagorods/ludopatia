
package com.ludopatia.game.model.entities;

public class Pais {

    private final long id;
    private final String nome;
    private final String continente;
    private final long populacaoTotal;

    // Atributos estáticos
    private final int acessibilidade;
    private final int receptividade;
    private final int vulnerabilidade;
    private final int resistencia;

    // Informações de expansão e classificação
    private final double bonusExpansaoPercentual;
    private final PaisCategoria categoria;
    private final String descricao;

    public Pais(long id, String nome, String continente,
                long populacaoTotal,
                int acessibilidade, int receptividade,
                int vulnerabilidade, int resistencia,
                double bonusExpansaoPercentual,
                PaisCategoria categoria,
                String descricao) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do país inválido.");
        }

        if (continente == null || continente.isBlank()) {
            throw new IllegalArgumentException("Continente inválido.");
        }

        if (populacaoTotal <= 0) {
            throw new IllegalArgumentException("População inválida.");
        }

        validarAtributo(acessibilidade);
        validarAtributo(receptividade);
        validarAtributo(vulnerabilidade);
        validarAtributo(resistencia);

        if (!Double.isFinite(bonusExpansaoPercentual)
                || bonusExpansaoPercentual < 0) {
            throw new IllegalArgumentException(
                    "Bônus de expansão inválido."
            );
        }

        if (categoria == null) {
            throw new IllegalArgumentException("Categoria inválida.");
        }

        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição inválida.");
        }

        this.id = id;
        this.nome = nome;
        this.continente = continente;
        this.populacaoTotal = populacaoTotal;
        this.acessibilidade = acessibilidade;
        this.receptividade = receptividade;
        this.vulnerabilidade = vulnerabilidade;
        this.resistencia = resistencia;
        this.bonusExpansaoPercentual = bonusExpansaoPercentual;
        this.categoria = categoria;
        this.descricao = descricao;
    }

    private static void validarAtributo(int valor) {
        if (valor < 0 || valor > 100) {
            throw new IllegalArgumentException(
                    "Os atributos devem estar entre 0 e 100."
            );
        }
    }

    public long getId() { return id; }

    public String getNome() { return nome; }

    public String getContinente() { return continente; }

    public long getPopulacaoTotal() { return populacaoTotal; }

    public int getAcessibilidade() { return acessibilidade; }

    public int getReceptividade() { return receptividade; }

    public int getVulnerabilidade() { return vulnerabilidade; }

    public int getResistencia() { return resistencia; }

    public double getBonusExpansaoPercentual() {
        return bonusExpansaoPercentual;
    }

    public PaisCategoria getCategoria() { return categoria; }

    public String getDescricao() { return descricao; }
}
