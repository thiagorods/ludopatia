package com.ludopatia.game.infrastructure.model.dao.impl;

import com.ludopatia.game.infrastructure.model.dao.PartidaNoticiaDao;
import com.ludopatia.game.infrastructure.model.entities.PartidaNoticia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PartidaNoticiaDaoJDBC implements PartidaNoticiaDao {

    private final Connection connection;

    public PartidaNoticiaDaoJDBC(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void registrar(PartidaNoticia partidaNoticia)
            throws SQLException {

        Objects.requireNonNull(partidaNoticia);

        String sql = """
                INSERT INTO partida_noticias (
                    id_partida,
                    id_noticia,
                    ciclo_gerado
                )
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, partidaNoticia.getIdPartida());
            statement.setLong(2, partidaNoticia.getIdNoticia());
            statement.setInt(3, partidaNoticia.getCicloGerado());

            statement.executeUpdate();
        }
    }

    @Override
    public List<PartidaNoticia> listarPorPartida(long idPartida)
            throws SQLException {

        String sql = """
                SELECT id_partida, id_noticia, ciclo_gerado
                FROM partida_noticias
                WHERE id_partida = ?
                ORDER BY ciclo_gerado, id_noticia
                """;

        List<PartidaNoticia> noticias = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, idPartida);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    noticias.add(new PartidaNoticia(
                            rs.getLong("id_partida"),
                            rs.getLong("id_noticia"),
                            rs.getInt("ciclo_gerado")
                    ));
                }
            }
        }

        return noticias;
    }
}
