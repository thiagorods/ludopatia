package com.ludopatia.game.utils.validation;

public enum ResultadoValidacaoNome {

    VALIDO,
    NOME_VAZIO,
    NOME_CURTO,
    NOME_LONGO,
    CARACTERES_INVALIDOS;

    public boolean isValido() {
        return this == VALIDO;
    }
}
