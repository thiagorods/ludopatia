package com.ludopatia.game.model.dao.impl;

import com.ludopatia.game.model.dao.PartidaDao;
import com.ludopatia.game.model.entities.Partida;
import com.ludopatia.game.model.entities.StatusPartida;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PartidaDaoJDBC implements PartidaDao {

    private final Connection connection;

    public PartidaDaoJDBC(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public long criar(Partida partida) throws SQLException {

        Objects.requireNonNull(partida);

        if (partida.getId() != 0) {
            throw new IllegalArgumentException(
                    "A nova partida deve possuir ID igual a zero."
            );
        }

        String sql = """
                INSERT INTO partida (
                    id_pais_inicial,
                    nome_empresa,
                    status,
                    ciclo,
                    valor_influencia,
                    valor_capital
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, partida.getIdPaisInicial());
            statement.setString(2, partida.getNomeEmpresa());
            statement.setString(3, partida.getStatus().name());
            statement.setInt(4, partida.getCiclo());
            statement.setInt(5, partida.getValorInfluencia());

            if (partida.getValorCapital() == null) {
                statement.setNull(6, Types.INTEGER);
            } else {
                statement.setInt(6, partida.getValorCapital());
            }

            statement.executeUpdate();
        }

        // Recupera o ID criado pelo SQLite nesta conexão.
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(
                     "SELECT last_insert_rowid()")) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        }

        throw new SQLException("Não foi possível recuperar o ID da partida.");
    }

    @Override
    public Optional<Partida> buscarPorId(long id) throws SQLException {

        String sql = """
                SELECT id_partida, id_pais_inicial, nome_empresa,
                       status, ciclo, valor_influencia, valor_capital
                FROM partida
                WHERE id_partida = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(mapearPartida(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Partida> listarTodas() throws SQLException {

        String sql = """
                SELECT id_partida, id_pais_inicial, nome_empresa,
                       status, ciclo, valor_influencia, valor_capital
                FROM partida
                ORDER BY id_partida DESC
                """;

        List<Partida> partidas = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                partidas.add(mapearPartida(rs));
            }
        }

        return partidas;
    }

    @Override
    public boolean atualizar(Partida partida) throws SQLException {

        Objects.requireNonNull(partida);

        if (partida.getId() <= 0) {
            throw new IllegalArgumentException(
                    "Não é possível atualizar uma partida não salva."
            );
        }

        String sql = """
                UPDATE partida
                SET nome_empresa = ?,
                    status = ?,
                    ciclo = ?,
                    valor_influencia = ?,
                    valor_capital = ?
                WHERE id_partida = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, partida.getNomeEmpresa());
            statement.setString(2, partida.getStatus().name());
            statement.setInt(3, partida.getCiclo());
            statement.setInt(4, partida.getValorInfluencia());

            if (partida.getValorCapital() == null) {
                statement.setNull(5, Types.INTEGER);
            } else {
                statement.setInt(5, partida.getValorCapital());
            }

            statement.setLong(6, partida.getId());

            return statement.executeUpdate() > 0;
        }
    }

    private Partida mapearPartida(ResultSet rs) throws SQLException {

        StatusPartida status;

        try {
            status = StatusPartida.valueOf(rs.getString("status"));

        } catch (IllegalArgumentException | NullPointerException e) {
            throw new SQLException(
                    "Status inválido cadastrado na partida.", e
            );
        }

        int capital = rs.getInt("valor_capital");
        Integer valorCapital = rs.wasNull() ? null : capital;

        return new Partida(
                rs.getLong("id_partida"),
                rs.getLong("id_pais_inicial"),
                rs.getString("nome_empresa"),
                status,
                rs.getInt("ciclo"),
                rs.getInt("valor_influencia"),
                valorCapital
        );
    }
}
