package com.ludopatia.game.infrastructure.model.dao.impl;

import com.ludopatia.game.infrastructure.model.dao.PaisDao;
import com.ludopatia.game.infrastructure.model.entities.PaisCategoria;
import com.ludopatia.game.infrastructure.model.entities.Pais;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

public class PaisDaoJDBC implements PaisDao {

    private final Connection connection;

    public PaisDaoJDBC(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public List<Pais> listarTodos() throws SQLException {

        String sql = """
                SELECT id, nome, continente, populacao_total,
                       acessibilidade, receptividade,
                       vulnerabilidade, resistencia,
                       bonus_expansao_percentual, categoria, descricao
                FROM pais
                ORDER BY nome COLLATE NOCASE
                """;

        List<Pais> paises = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);

             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Pais pais = mapearPais(resultSet);
                paises.add(pais);
            }
        }

        return paises;
    }

    @Override
    public Optional<Pais> buscarPorId(long id) throws SQLException {

        String sql = """
                SELECT id, nome, continente, populacao_total,
                       acessibilidade, receptividade,
                       vulnerabilidade, resistencia,
                       bonus_expansao_percentual, categoria, descricao
                FROM pais
                WHERE id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapearPais(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    private Pais mapearPais(ResultSet resultSet) throws SQLException {

        PaisCategoria categoria;

        try {
            categoria = PaisCategoria.valueOf(
                    resultSet.getString("categoria")
            );

        } catch (IllegalArgumentException | NullPointerException e) {
            throw new SQLException(
                    "Categoria inválida cadastrada no banco de dados.",
                    e
            );
        }

        return new Pais(
                resultSet.getInt("id"),
                resultSet.getString("nome"),
                resultSet.getString("continente"),
                resultSet.getLong("populacao_total"),
                resultSet.getInt("acessibilidade"),
                resultSet.getInt("receptividade"),
                resultSet.getInt("vulnerabilidade"),
                resultSet.getInt("resistencia"),
                resultSet.getDouble("bonus_expansao_percentual"),
                categoria,
                resultSet.getString("descricao")
        );
    }
}
