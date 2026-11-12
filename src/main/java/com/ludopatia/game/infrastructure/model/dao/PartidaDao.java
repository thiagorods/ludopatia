package com.ludopatia.game.infrastructure.model.dao;

import com.ludopatia.game.infrastructure.model.entities.Partida;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PartidaDao {

    long criar(Partida partida) throws SQLException;

    Optional<Partida> buscarPorId(long id) throws SQLException;

    List<Partida> listarTodas() throws SQLException;

    boolean atualizar(Partida partida) throws SQLException;
}
