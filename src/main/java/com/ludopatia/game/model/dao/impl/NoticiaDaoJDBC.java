package com.ludopatia.game.model.dao.impl;

import com.ludopatia.game.model.dao.NoticiaDao;
import com.ludopatia.game.model.entities.Noticia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class NoticiaDaoJDBC implements NoticiaDao {

    private final Connection connection;

    public NoticiaDaoJDBC(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public long criar(Noticia noticia) throws SQLException {

        Objects.requireNonNull(noticia);

        if (noticia.getId() != 0) {
            throw new IllegalArgumentException(
                    "Uma nova notícia deve possuir ID igual a zero."
            );
        }

        String sql = """
                INSERT INTO noticia (titulo, descricao, tipo)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, noticia.getTitulo());
            statement.setString(2, noticia.getDescricao());
            statement.setString(3, noticia.getTipo());

            statement.executeUpdate();
        }

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(
                     "SELECT last_insert_rowid()")) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        }

        throw new SQLException(
                "Não foi possível recuperar o ID da notícia."
        );
    }

    @Override
    public Optional<Noticia> buscarPorId(long id) throws SQLException {

        String sql = """
                SELECT id_noticia, titulo, descricao, tipo
                FROM noticia
                WHERE id_noticia = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(mapearNoticia(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Noticia> listarTodas() throws SQLException {

        String sql = """
                SELECT id_noticia, titulo, descricao, tipo
                FROM noticia
                ORDER BY id_noticia
                """;

        List<Noticia> noticias = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                noticias.add(mapearNoticia(rs));
            }
        }

        return noticias;
    }

    private Noticia mapearNoticia(ResultSet rs) throws SQLException {

        return new Noticia(
                rs.getLong("id_noticia"),
                rs.getString("titulo"),
                rs.getString("descricao"),
                rs.getString("tipo")
        );
    }
}
