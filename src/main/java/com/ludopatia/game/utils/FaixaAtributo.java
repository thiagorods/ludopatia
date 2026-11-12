package com.ludopatia.game.utils;

/**
 * Faixa qualitativa dos atributos estáticos (escala de 0 a 100), conforme o Apêndice 1 da Mecânica de Jogo.
 */
public final class FaixaAtributo {

    private FaixaAtributo() {
    }

    public static String rotulo(int valor) {
        if (valor <= 20) return "Muito Baixo";
        if (valor <= 40) return "Baixo";
        if (valor <= 60) return "Médio";
        if (valor <= 80) return "Alto";
        return "Muito Alto";
    }
}

