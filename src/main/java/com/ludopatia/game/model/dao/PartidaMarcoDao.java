package com.ludopatia.game.model.dao;

import com.ludopatia.game.model.entities.PartidaMarco;

import java.sql.SQLException;
import java.util.List;

public interface PartidaMarcoDao {

    boolean registrar(PartidaMarco partidaMarco)
            throws SQLException;

    boolean existe(long idPartida, long idMarco)
            throws SQLException;

    List<PartidaMarco> listarPorPartida(long idPartida)
            throws SQLException;
}
