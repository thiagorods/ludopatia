package com.ludopatia.game.utils.validation;

public class ValidadorNomeCasa {

    public static ResultadoValidacaoNome validar(String nome) {

        if (nome == null || nome.isBlank()) {
            return ResultadoValidacaoNome.NOME_VAZIO;
        }

        if (nome.length() < 3) {
            return ResultadoValidacaoNome.NOME_CURTO;
        }

        if (nome.length() > 20) {
            return ResultadoValidacaoNome.NOME_LONGO;
        }

        if (!nome.matches("(?=(?:.*[a-zA-Z]){2})[a-zA-Z0-9\\-&*](?:[a-zA-Z0-9\\s\\-&*]*[a-zA-Z0-9\\-&*])?")) {
            return ResultadoValidacaoNome.CARACTERES_INVALIDOS;
        }

        return ResultadoValidacaoNome.VALIDO;
    }
}
