package com.ludopatia.game.model.dao;

import com.ludopatia.game.model.entities.PartidaPais;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PartidaPaisDao {

    void adicionar(PartidaPais partidaPais) throws SQLException;

    Optional<PartidaPais> buscarPorPartidaEPais(
            long idPartida, long idPais
    ) throws SQLException;

    List<PartidaPais> listarPorPartida(long idPartida)
            throws SQLException;

    boolean atualizar(PartidaPais partidaPais) throws SQLException;
}
