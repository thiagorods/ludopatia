package com.ludopatia.game.infrastructure.model.dao;

import com.ludopatia.game.infrastructure.model.entities.PartidaPais;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PartidaPaisDao {

    void adicionar(PartidaPais partidaPais) throws SQLException;

    Optional<PartidaPais> buscarPorPartidaEPais(
            long idPartida, int idPais
    ) throws SQLException;

    List<PartidaPais> listarPorPartida(long idPartida)
            throws SQLException;

    boolean atualizar(PartidaPais partidaPais) throws SQLException;
}
