package com.ludopatia.game.infrastructure.model.dao.impl;

import com.ludopatia.game.infrastructure.model.dao.EstrategiaDao;
import com.ludopatia.game.infrastructure.model.entities.Estrategia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EstrategiaDaoJDBC implements EstrategiaDao {

    private final Connection connection;

    private static final String COLUNAS = """
            id_estrategia, id_tipo, titulo, descricao, icone,
            custo_influencia, custo_capital,
            valor_exposicao, valor_conversao, valor_retencao,
            valor_agravamento, valor_recuperacao,
            valor_conscientizacao, valor_expansao
            """;

    public EstrategiaDaoJDBC(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public List<Estrategia> listarTodas() throws SQLException {

        String sql = "SELECT " + COLUNAS +
                " FROM estrategia ORDER BY id_tipo, id_estrategia";

        List<Estrategia> estrategias = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                estrategias.add(mapearEstrategia(rs));
            }
        }

        return estrategias;
    }

    @Override
    public Optional<Estrategia> buscarPorId(long id)
            throws SQLException {

        String sql = "SELECT " + COLUNAS +
                " FROM estrategia WHERE id_estrategia = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(mapearEstrategia(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Estrategia> listarPorTipo(long idTipo)
            throws SQLException {

        String sql = "SELECT " + COLUNAS +
                " FROM estrategia WHERE id_tipo = ? ORDER BY id_estrategia";

        List<Estrategia> estrategias = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, idTipo);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    estrategias.add(mapearEstrategia(rs));
                }
            }
        }

        return estrategias;
    }

    private Estrategia mapearEstrategia(ResultSet rs) throws SQLException {

        int custoInfluencia = rs.getInt("custo_influencia");

        if (rs.wasNull()) {
            throw new SQLException(
                    "Estratégia sem custo de Influência cadastrado."
            );
        }

        int valorCapital = rs.getInt("custo_capital");
        Integer custoCapital = rs.wasNull() ? null : valorCapital;

        return new Estrategia(
                rs.getLong("id_estrategia"),
                rs.getLong("id_tipo"),
                rs.getString("titulo"),
                rs.getString("descricao"),
                rs.getString("icone"),
                custoInfluencia,
                custoCapital,
                rs.getDouble("valor_exposicao"),
                rs.getDouble("valor_conversao"),
                rs.getDouble("valor_retencao"),
                rs.getDouble("valor_agravamento"),
                rs.getDouble("valor_recuperacao"),
                rs.getDouble("valor_conscientizacao"),
                rs.getDouble("valor_expansao")
        );
    }
}
