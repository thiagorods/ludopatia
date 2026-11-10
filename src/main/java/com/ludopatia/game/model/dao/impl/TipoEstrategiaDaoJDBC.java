package com.ludopatia.game.model.dao.impl;

import com.ludopatia.game.model.dao.TipoEstrategiaDao;
import com.ludopatia.game.model.entities.TipoEstrategia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TipoEstrategiaDaoJDBC implements TipoEstrategiaDao {

    private final Connection connection;

    public TipoEstrategiaDaoJDBC(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public List<TipoEstrategia> listarTodos() throws SQLException {

        String sql = """
                SELECT id_tipo, nome, descricao
                FROM tipo_estrategia
                ORDER BY id_tipo
                """;

        List<TipoEstrategia> tipos = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                tipos.add(mapearTipo(rs));
            }
        }

        return tipos;
    }

    @Override
    public Optional<TipoEstrategia> buscarPorId(long id)
            throws SQLException {

        String sql = """
                SELECT id_tipo, nome, descricao
                FROM tipo_estrategia
                WHERE id_tipo = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(mapearTipo(rs));
                }
            }
        }

        return Optional.empty();
    }

    private TipoEstrategia mapearTipo(ResultSet rs) throws SQLException {

        return new TipoEstrategia(
                rs.getLong("id_tipo"),
                rs.getString("nome"),
                rs.getString("descricao")
        );
    }
}
