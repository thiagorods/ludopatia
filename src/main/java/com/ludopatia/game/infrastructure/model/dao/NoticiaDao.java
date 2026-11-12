package com.ludopatia.game.infrastructure.model.dao;

import com.ludopatia.game.infrastructure.model.entities.Noticia;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface NoticiaDao {

    long criar(Noticia noticia) throws SQLException;

    Optional<Noticia> buscarPorId(long id) throws SQLException;

    List<Noticia> listarTodas() throws SQLException;
}
