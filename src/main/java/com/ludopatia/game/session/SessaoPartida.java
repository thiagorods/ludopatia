package com.ludopatia.game.session;

import com.ludopatia.game.infrastructure.model.entities.Partida;

import java.util.Objects;
import java.util.Optional;

/**
 * Mantém os dados necessários para iniciar e acompanhar a sessão atual.
 *
 * PartidaConfig é a configuração anterior à criação no banco; Partida é a
 * representação persistida, disponível depois da escolha do país inicial.
 */
public final class SessaoPartida {

    private static volatile PartidaConfig config;
    private static volatile Partida partidaAtual;

    private SessaoPartida() { }

    public static void definir(PartidaConfig c) {
        config = Objects.requireNonNull(c);
        partidaAtual = null;
    }

    public static PartidaConfig obter() {
        PartidaConfig c = config;
        if (c == null) {
            throw new IllegalStateException("Nenhuma partida configurada.");
        }
        return c;
    }

    public static void definirPartidaAtual(Partida partida) {
        partidaAtual = Objects.requireNonNull(partida);
    }

    public static Optional<Partida> obterPartidaAtual() {
        return Optional.ofNullable(partidaAtual);
    }

    public static void limpar() {
        config = null;
        partidaAtual = null;
    }
}
