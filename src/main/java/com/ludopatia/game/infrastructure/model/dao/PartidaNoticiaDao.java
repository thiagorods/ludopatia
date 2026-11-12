package com.ludopatia.game.infrastructure.model.dao;

import com.ludopatia.game.infrastructure.model.entities.PartidaNoticia;

import java.sql.SQLException;
import java.util.List;

public interface PartidaNoticiaDao {

    void registrar(PartidaNoticia partidaNoticia)
            throws SQLException;

    List<PartidaNoticia> listarPorPartida(long idPartida)
            throws SQLException;
}
