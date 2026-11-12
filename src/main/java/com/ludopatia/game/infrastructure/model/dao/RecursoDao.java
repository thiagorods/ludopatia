package com.ludopatia.game.infrastructure.model.dao;

import com.ludopatia.game.infrastructure.model.entities.Recurso;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RecursoDao {

    List<Recurso> listarTodos() throws SQLException;

    Optional<Recurso> buscarPorId(long id) throws SQLException;

    Optional<Recurso> buscarPorNome(String nome) throws SQLException;
}
