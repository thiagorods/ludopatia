package com.ludopatia.game.infrastructure.model.dao;

import com.ludopatia.game.infrastructure.model.entities.Estrategia;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EstrategiaDao {

    List<Estrategia> listarTodas() throws SQLException;

    Optional<Estrategia> buscarPorId(long id) throws SQLException;

    List<Estrategia> listarPorTipo(long idTipo) throws SQLException;
}
