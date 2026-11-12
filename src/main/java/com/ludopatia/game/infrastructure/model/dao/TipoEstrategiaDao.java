package com.ludopatia.game.infrastructure.model.dao;

import com.ludopatia.game.infrastructure.model.entities.TipoEstrategia;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TipoEstrategiaDao {

    List<TipoEstrategia> listarTodos() throws SQLException;

    Optional<TipoEstrategia> buscarPorId(long id) throws SQLException;
}
