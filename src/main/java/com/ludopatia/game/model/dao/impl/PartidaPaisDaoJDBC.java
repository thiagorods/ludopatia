package com.ludopatia.game.model.dao.impl;

import com.ludopatia.game.model.dao.PartidaPaisDao;
import com.ludopatia.game.model.entities.PartidaPais;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PartidaPaisDaoJDBC implements PartidaPaisDao {

    private final Connection connection;

    public PartidaPaisDaoJDBC(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void adicionar(PartidaPais partidaPais)
            throws SQLException {

        Objects.requireNonNull(partidaPais);

        String sql = """
                INSERT INTO partida_pais (
                    id_partida,
                    id_pais,
                    num_populacao,
                    num_expostos,
                    num_jogadores,
                    num_jogadores_recorrentes,
                    num_jogadores_em_risco,
                    num_recuperados,
                    valor_reacao_pais
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, partidaPais.getIdPartida());
            statement.setLong(2, partidaPais.getIdPais());

            statement.setLong(3, partidaPais.getNumPopulacao());
            statement.setLong(4, partidaPais.getNumExpostos());
            statement.setLong(5, partidaPais.getNumJogadores());
            statement.setLong(6, partidaPais.getNumJogadoresRecorrentes());
            statement.setLong(7, partidaPais.getNumJogadoresEmRisco());
            statement.setLong(8, partidaPais.getNumRecuperados());

            definirReacao(statement, 9, partidaPais.getValorReacaoPais());

            statement.executeUpdate();
        }
    }

    @Override
    public Optional<PartidaPais> buscarPorPartidaEPais(
            long idPartida, long idPais) throws SQLException {

        String sql = """
                SELECT *
                FROM partida_pais
                WHERE id_partida = ?
                  AND id_pais = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, idPartida);
            statement.setLong(2, idPais);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(mapearPartidaPais(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<PartidaPais> listarPorPartida(long idPartida)
            throws SQLException {

        String sql = """
                SELECT *
                FROM partida_pais
                WHERE id_partida = ?
                ORDER BY id_pais
                """;

        List<PartidaPais> paises = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, idPartida);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    paises.add(mapearPartidaPais(rs));
                }
            }
        }

        return paises;
    }

    @Override
    public boolean atualizar(PartidaPais partidaPais)
            throws SQLException {

        Objects.requireNonNull(partidaPais);

        String sql = """
                UPDATE partida_pais
                SET num_populacao = ?,
                    num_expostos = ?,
                    num_jogadores = ?,
                    num_jogadores_recorrentes = ?,
                    num_jogadores_em_risco = ?,
                    num_recuperados = ?,
                    valor_reacao_pais = ?
                WHERE id_partida = ?
                  AND id_pais = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, partidaPais.getNumPopulacao());
            statement.setLong(2, partidaPais.getNumExpostos());
            statement.setLong(3, partidaPais.getNumJogadores());
            statement.setLong(4, partidaPais.getNumJogadoresRecorrentes());
            statement.setLong(5, partidaPais.getNumJogadoresEmRisco());
            statement.setLong(6, partidaPais.getNumRecuperados());

            definirReacao(statement, 7, partidaPais.getValorReacaoPais());

            statement.setLong(8, partidaPais.getIdPartida());
            statement.setLong(9, partidaPais.getIdPais());

            return statement.executeUpdate() > 0;
        }
    }

    private void definirReacao(
            PreparedStatement statement,
            int indice,
            Double valor) throws SQLException {

        if (valor == null) {
            statement.setNull(indice, Types.REAL);
        } else {
            statement.setDouble(indice, valor);
        }
    }

    private PartidaPais mapearPartidaPais(ResultSet rs)
            throws SQLException {

        double reacao = rs.getDouble("valor_reacao_pais");
        Double valorReacaoPais = rs.wasNull() ? null : reacao;

        return new PartidaPais(
                rs.getLong("id_partida"),
                rs.getLong("id_pais"),
                rs.getLong("num_populacao"),
                rs.getLong("num_expostos"),
                rs.getLong("num_jogadores"),
                rs.getLong("num_jogadores_recorrentes"),
                rs.getLong("num_jogadores_em_risco"),
                rs.getLong("num_recuperados"),
                valorReacaoPais
        );
    }
}
