package com.ludopatia.game.model.dao.impl;

import com.ludopatia.game.model.dao.RecursoDao;
import com.ludopatia.game.model.entities.Recurso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RecursoDaoJDBC implements RecursoDao {

    private final Connection connection;

    public RecursoDaoJDBC(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public List<Recurso> listarTodos() throws SQLException {

        String sql = """
                SELECT id_recurso, nome, descricao, valor_inicial
                FROM recurso
                ORDER BY nome COLLATE NOCASE
                """;

        List<Recurso> recursos = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);

             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Recurso recurso = mapearRecurso(resultSet);
                recursos.add(recurso);
            }
        }

        return recursos;
    }

    @Override
    public Optional<Recurso> buscarPorId(long id) throws SQLException {

        String sql = """
                SELECT id_recurso, nome, descricao, valor_inicial
                FROM recurso
                WHERE id_recurso = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapearRecurso(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Recurso> buscarPorNome(String nome) throws SQLException {

        String sql = """
                SELECT id_recurso, nome, descricao, valor_inicial
                FROM recurso
                WHERE nome = ? COLLATE NOCASE
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, nome);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapearRecurso(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    private Recurso mapearRecurso(ResultSet resultSet) throws SQLException {

        return new Recurso(
                resultSet.getLong("id_recurso"),
                resultSet.getString("nome"),
                resultSet.getString("descricao"),
                resultSet.getInt("valor_inicial")
        );
    }
}
