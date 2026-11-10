package com.ludopatia.game.model.dao;

import com.ludopatia.game.model.entities.PartidaEstrategia;

import java.sql.SQLException;
import java.util.List;

public interface PartidaEstrategiaDao {

    void adicionar(PartidaEstrategia partidaEstrategia)
            throws SQLException;

    List<PartidaEstrategia> listarPorPartida(long idPartida)
            throws SQLException;

    boolean existe(long idPartida, long idEstrategia)
            throws SQLException;

    boolean remover(long idPartida, long idEstrategia)
            throws SQLException;
}
