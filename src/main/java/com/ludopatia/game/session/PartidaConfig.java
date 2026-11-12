package com.ludopatia.game.session;

public record PartidaConfig(String nomeCasa, int idPaisInicial) {
    public boolean temPaisInicial() {
        return idPaisInicial != -1;
    }
}
