package com.ludopatia.game.model.dao.impl;

import com.ludopatia.game.model.dao.EstrategiaConexaoDao;
import com.ludopatia.game.model.entities.EstrategiaConexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EstrategiaConexaoDaoJDBC implements EstrategiaConexaoDao {

    private final Connection connection;

    public EstrategiaConexaoDaoJDBC(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public List<EstrategiaConexao> listarTodas() throws SQLException {

        String sql = """
                SELECT id_estrategia_origem, id_estrategia_destino
                FROM estrategia_conexao
                ORDER BY id_estrategia_origem, id_estrategia_destino
                """;

        List<EstrategiaConexao> conexoes = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                conexoes.add(mapearConexao(rs));
            }
        }

        return conexoes;
    }

    @Override
    public List<EstrategiaConexao> listarPorOrigem(long idOrigem)
            throws SQLException {

        String sql = """
                SELECT id_estrategia_origem, id_estrategia_destino
                FROM estrategia_conexao
                WHERE id_estrategia_origem = ?
                ORDER BY id_estrategia_destino
                """;

        List<EstrategiaConexao> conexoes = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, idOrigem);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    conexoes.add(mapearConexao(rs));
                }
            }
        }

        return conexoes;
    }

    @Override
    public List<EstrategiaConexao> listarPorDestino(long idDestino)
            throws SQLException {

        String sql = """
                SELECT id_estrategia_origem, id_estrategia_destino
                FROM estrategia_conexao
                WHERE id_estrategia_destino = ?
                ORDER BY id_estrategia_origem
                """;

        List<EstrategiaConexao> conexoes = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, idDestino);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    conexoes.add(mapearConexao(rs));
                }
            }
        }

        return conexoes;
    }

    @Override
    public boolean existe(long idOrigem, long idDestino)
            throws SQLException {

        String sql = """
                SELECT 1
                FROM estrategia_conexao
                WHERE id_estrategia_origem = ?
                  AND id_estrategia_destino = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, idOrigem);
            statement.setLong(2, idDestino);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public void adicionar(EstrategiaConexao conexao)
            throws SQLException {

        Objects.requireNonNull(conexao, "Conexão não pode ser nula.");

        String sql = """
                INSERT INTO estrategia_conexao (
                    id_estrategia_origem,
                    id_estrategia_destino
                )
                VALUES (?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, conexao.getIdEstrategiaOrigem());
            statement.setLong(2, conexao.getIdEstrategiaDestino());

            statement.executeUpdate();
        }
    }

    @Override
    public boolean remover(long idOrigem, long idDestino)
            throws SQLException {

        String sql = """
                DELETE FROM estrategia_conexao
                WHERE id_estrategia_origem = ?
                  AND id_estrategia_destino = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, idOrigem);
            statement.setLong(2, idDestino);

            return statement.executeUpdate() > 0;
        }
    }

    private EstrategiaConexao mapearConexao(ResultSet rs)
            throws SQLException {

        return new EstrategiaConexao(
                rs.getLong("id_estrategia_origem"),
                rs.getLong("id_estrategia_destino")
        );
    }
}
