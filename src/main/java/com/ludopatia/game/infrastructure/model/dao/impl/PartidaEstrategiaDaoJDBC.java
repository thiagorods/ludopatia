package com.ludopatia.game.infrastructure.model.dao.impl;

import com.ludopatia.game.infrastructure.model.dao.PartidaEstrategiaDao;
import com.ludopatia.game.infrastructure.model.entities.PartidaEstrategia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PartidaEstrategiaDaoJDBC
        implements PartidaEstrategiaDao {

    private final Connection connection;

    public PartidaEstrategiaDaoJDBC(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void adicionar(PartidaEstrategia partidaEstrategia)
            throws SQLException {

        Objects.requireNonNull(partidaEstrategia);

        String sql = """
                INSERT INTO partida_estrategia (
                    id_partida,
                    id_estrategia
                )
                VALUES (?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(
                    1, partidaEstrategia.getIdPartida()
            );

            statement.setLong(
                    2, partidaEstrategia.getIdEstrategia()
            );

            statement.executeUpdate();
        }
    }

    @Override
    public List<PartidaEstrategia> listarPorPartida(long idPartida)
            throws SQLException {

        String sql = """
                SELECT id_partida, id_estrategia
                FROM partida_estrategia
                WHERE id_partida = ?
                ORDER BY id_estrategia
                """;

        List<PartidaEstrategia> estrategias = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, idPartida);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    estrategias.add(new PartidaEstrategia(
                            rs.getLong("id_partida"),
                            rs.getLong("id_estrategia")
                    ));
                }
            }
        }

        return estrategias;
    }

    @Override
    public boolean existe(long idPartida, long idEstrategia)
            throws SQLException {

        String sql = """
                SELECT 1
                FROM partida_estrategia
                WHERE id_partida = ?
                  AND id_estrategia = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, idPartida);
            statement.setLong(2, idEstrategia);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public boolean remover(long idPartida, long idEstrategia)
            throws SQLException {

        String sql = """
                DELETE FROM partida_estrategia
                WHERE id_partida = ?
                  AND id_estrategia = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, idPartida);
            statement.setLong(2, idEstrategia);

            return statement.executeUpdate() > 0;
        }
    }
}
