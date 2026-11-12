package com.ludopatia.game.infrastructure.model.dao;

import com.ludopatia.game.infrastructure.model.entities.MarcoInfluencia;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface MarcoInfluenciaDao {

    List<MarcoInfluencia> listarTodos() throws SQLException;

    Optional<MarcoInfluencia> buscarPorId(long id)
            throws SQLException;
}
