package com.ludopatia.game.model.dao.impl;

import com.ludopatia.game.model.dao.MarcoInfluenciaDao;
import com.ludopatia.game.model.entities.MarcoInfluencia;
import com.ludopatia.game.model.entities.TipoCondicaoMarco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MarcoInfluenciaDaoJDBC implements MarcoInfluenciaDao {

    private final Connection connection;

    public MarcoInfluenciaDaoJDBC(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public List<MarcoInfluencia> listarTodos()
            throws SQLException {

        String sql = """
                SELECT id_marco, nome, descricao,
                       tipo_condicao, valor_condicao,
                       valor_recompensa
                FROM marco_influencia
                ORDER BY id_marco
                """;

        List<MarcoInfluencia> marcos = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                marcos.add(mapearMarco(rs));
            }
        }

        return marcos;
    }

    @Override
    public Optional<MarcoInfluencia> buscarPorId(long id)
            throws SQLException {

        String sql = """
                SELECT id_marco, nome, descricao,
                       tipo_condicao, valor_condicao,
                       valor_recompensa
                FROM marco_influencia
                WHERE id_marco = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(mapearMarco(rs));
                }
            }
        }

        return Optional.empty();
    }

    private MarcoInfluencia mapearMarco(ResultSet rs)
            throws SQLException {

        TipoCondicaoMarco tipo;

        try {
            tipo = TipoCondicaoMarco.valueOf(
                    rs.getString("tipo_condicao")
            );
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new SQLException(
                    "Tipo de condição inválido no banco.", e
            );
        }

        return new MarcoInfluencia(
                rs.getLong("id_marco"),
                rs.getString("nome"),
                rs.getString("descricao"),
                tipo,
                rs.getInt("valor_condicao"),
                rs.getInt("valor_recompensa")
        );
    }
}
