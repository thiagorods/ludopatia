package com.ludopatia.game.infrastructure.model.dao;

import com.ludopatia.game.infrastructure.model.entities.Pais;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PaisDao {

    List<Pais> listarTodos() throws SQLException;

    Optional<Pais> buscarPorId(long id) throws SQLException;

}
