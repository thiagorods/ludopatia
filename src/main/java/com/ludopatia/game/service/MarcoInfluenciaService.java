package com.ludopatia.game.service;

import com.ludopatia.game.model.dao.MarcoInfluenciaDao;
import com.ludopatia.game.model.dao.PartidaMarcoDao;

import com.ludopatia.game.model.dao.impl.MarcoInfluenciaDaoJDBC;
import com.ludopatia.game.model.dao.impl.PartidaMarcoDaoJDBC;

import com.ludopatia.game.model.entities.MarcoInfluencia;
import com.ludopatia.game.model.entities.PartidaMarco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MarcoInfluenciaService {

    private final Connection connection;

    public MarcoInfluenciaService(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    public List<MarcoInfluencia> avaliarEConceder(
            long idPartida,
            int ciclo,
            Integer paisesControlados) throws SQLException {

        if (idPartida <= 0 || ciclo < 0) {
            throw new IllegalArgumentException(
                    "Partida ou ciclo inválido."
            );
        }

        if (paisesControlados != null && paisesControlados < 0) {
            throw new IllegalArgumentException(
                    "Quantidade de países inválida."
            );
        }

        // A recompensa precisa ser salva na mesma
        // transação que registra a conquista.
        if (connection.getAutoCommit()) {
            throw new IllegalStateException(
                    "A concessão de marcos exige uma transação."
            );
        }

        MarcoInfluenciaDao marcoDao =
                new MarcoInfluenciaDaoJDBC(connection);

        PartidaMarcoDao partidaMarcoDao =
                new PartidaMarcoDaoJDBC(connection);

        // Quantidade de estratégias distintas na partida.
        long totalEstrategias = consultarQuantidade("""
                SELECT COUNT(*)
                FROM partida_estrategia
                WHERE id_partida = ?
                """, idPartida);

        // Quantidade de categorias representadas.
        long categoriasDistintas = consultarQuantidade("""
                SELECT COUNT(DISTINCT e.id_tipo)
                FROM partida_estrategia pe
                JOIN estrategia e
                    ON e.id_estrategia = pe.id_estrategia
                WHERE pe.id_partida = ?
                """, idPartida);

        // Maior número de estratégias de uma categoria.
        long maiorQuantidadeCategoria = consultarQuantidade("""
                SELECT COALESCE(MAX(quantidade), 0)
                FROM (
                    SELECT COUNT(*) AS quantidade
                    FROM partida_estrategia pe
                    JOIN estrategia e
                        ON e.id_estrategia = pe.id_estrategia
                    WHERE pe.id_partida = ?
                    GROUP BY e.id_tipo
                )
                """, idPartida);

        List<MarcoInfluencia> concedidos = new ArrayList<>();

        for (MarcoInfluencia marco : marcoDao.listarTodos()) {

            long progresso;

            switch (marco.getTipoCondicao()) {

                case ESTRATEGIAS_DISTINTAS ->
                        progresso = totalEstrategias;

                case CATEGORIAS_DISTINTAS ->
                        progresso = categoriasDistintas;

                case ESTRATEGIAS_MESMA_CATEGORIA ->
                        progresso = maiorQuantidadeCategoria;

                case PAISES_CONTROLADOS -> {
                    // A quantidade deve vir do sistema
                    // que controla os territórios da partida.
                    if (paisesControlados == null) {
                        continue;
                    }

                    progresso = paisesControlados;
                }

                default -> throw new IllegalStateException(
                        "Condição não implementada."
                );
            }

            if (progresso < marco.getValorCondicao()) {
                continue;
            }

            // Tenta registrar o marco apenas uma vez.
            boolean novo = partidaMarcoDao.registrar(
                    new PartidaMarco(
                            idPartida,
                            marco.getId(),
                            ciclo
                    )
            );

            if (novo) {
                acrescentarInfluencia(
                        idPartida,
                        marco.getValorRecompensa()
                );

                concedidos.add(marco);
            }
        }

        return concedidos;
    }

    private long consultarQuantidade(
            String sql,
            long idPartida) throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, idPartida);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        }
    }

    private void acrescentarInfluencia(
            long idPartida,
            int recompensa) throws SQLException {

        String sql = """
                UPDATE partida
                SET valor_influencia = valor_influencia + ?
                WHERE id_partida = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, recompensa);
            statement.setLong(2, idPartida);

            if (statement.executeUpdate() != 1) {
                throw new SQLException(
                        "Não foi possível conceder a recompensa."
                );
            }
        }
    }
}
