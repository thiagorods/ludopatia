package com.ludopatia.game.model.dao.impl;

import com.ludopatia.game.model.dao.PartidaMarcoDao;
import com.ludopatia.game.model.entities.PartidaMarco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PartidaMarcoDaoJDBC implements PartidaMarcoDao {

    private final Connection connection;

    public PartidaMarcoDaoJDBC(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public boolean registrar(PartidaMarco partidaMarco)
            throws SQLException {

        Objects.requireNonNull(partidaMarco);

        String sql = """
                INSERT INTO partida_marco (
                    id_partida,
                    id_marco,
                    ciclo_concluido
                )
                VALUES (?, ?, ?)
                ON CONFLICT(id_partida, id_marco) DO NOTHING
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, partidaMarco.getIdPartida());
            statement.setLong(2, partidaMarco.getIdMarco());
            statement.setInt(3, partidaMarco.getCicloConcluido());

            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean existe(long idPartida, long idMarco)
            throws SQLException {

        String sql = """
                SELECT 1
                FROM partida_marco
                WHERE id_partida = ?
                  AND id_marco = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, idPartida);
            statement.setLong(2, idMarco);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public List<PartidaMarco> listarPorPartida(long idPartida)
            throws SQLException {

        String sql = """
                SELECT id_partida, id_marco, ciclo_concluido
                FROM partida_marco
                WHERE id_partida = ?
                ORDER BY ciclo_concluido, id_marco
                """;

        List<PartidaMarco> historico = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, idPartida);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    historico.add(new PartidaMarco(
                            rs.getLong("id_partida"),
                            rs.getLong("id_marco"),
                            rs.getInt("ciclo_concluido")
                    ));
                }
            }
        }

        return historico;
    }
}
