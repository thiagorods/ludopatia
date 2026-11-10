package com.ludopatia.game.model.dao;

import com.ludopatia.game.model.entities.PartidaNoticia;

import java.sql.SQLException;
import java.util.List;

public interface PartidaNoticiaDao {

    void registrar(PartidaNoticia partidaNoticia)
            throws SQLException;

    List<PartidaNoticia> listarPorPartida(long idPartida)
            throws SQLException;
}
