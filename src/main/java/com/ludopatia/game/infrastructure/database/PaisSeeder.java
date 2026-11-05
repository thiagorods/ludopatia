
package com.ludopatia.game.infrastructure.database;

import com.ludopatia.game.model.entities.Pais;
import com.ludopatia.game.model.entities.PaisCategoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public final class PaisSeeder {

    private PaisSeeder() {
    }

    public static void popular(Connection connection) throws SQLException {

        String sql = """
                INSERT INTO pais (
                    id, nome, continente, populacao_total,
                    acessibilidade, receptividade,
                    vulnerabilidade, resistencia,
                    bonus_expansao_percentual, categoria, descricao
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    nome = excluded.nome,
                    continente = excluded.continente,
                    populacao_total = excluded.populacao_total,
                    acessibilidade = excluded.acessibilidade,
                    receptividade = excluded.receptividade,
                    vulnerabilidade = excluded.vulnerabilidade,
                    resistencia = excluded.resistencia,
                    bonus_expansao_percentual = excluded.bonus_expansao_percentual,
                    categoria = excluded.categoria,
                    descricao = excluded.descricao
                """;

        List<Pais> paises = List.of(

                new Pais(
                        1L, "Brasil", "América do Sul", 210_000_000L,
                        90, 85, 75, 80, 40.0,
                        PaisCategoria.CRESCIMENTO_PROMISSOR,
                        "Mercado muito receptivo e favorável à expansão global, " +
                                "mas com forte regulamentação institucional."
                ),

                new Pais(
                        2L, "Peru", "América do Sul", 35_000_000L,
                        80, 75, 65, 40, 20.0,
                        PaisCategoria.ENTRADA_FACIL,
                        "Mercado regional acessível, com boas condições para " +
                                "crescimento interno e conexões estratégicas, mas com " +
                                "influência internacional limitada."
                ),

                new Pais(
                        3L, "Estados Unidos", "América do Norte", 340_000_000L,
                        95, 75, 55, 65, 55.0,
                        PaisCategoria.CRESCIMENTO_PROMISSOR,
                        "Mercado global consolidado, com importantes conexões. " +
                                "Possui grande potencial de crescimento, mas dificuldade " +
                                "considerável."
                ),

                new Pais(
                        4L, "Reino Unido", "Europa", 70_000_000L,
                        70, 90, 35, 95, 70.0,
                        PaisCategoria.MERCADO_DESAFIADOR,
                        "Mercado de referência global. Difícil de conquistar, " +
                                "mas com grande potencial de influência sobre outros " +
                                "países conectados."
                ),

                new Pais(
                        5L, "Itália", "Europa", 60_000_000L,
                        60, 80, 45, 75, 30.0,
                        PaisCategoria.CRESCIMENTO_PROMISSOR,
                        "Mercado tradicional com grande público e conexões " +
                                "potenciais, mas possui dificuldade relativa de " +
                                "entrada e expansão."
                ),

                new Pais(
                        6L, "Rússia", "Europa e Ásia", 140_000_000L,
                        55, 65, 50, 80, 10.0,
                        PaisCategoria.EXPANSAO_LIMITADA,
                        "Mercado intercontinental com barreiras de entrada. " +
                                "Oferece potencial de crescimento interno, mas retorno " +
                                "estratégico limitado sobre países conectados."
                ),

                new Pais(
                        7L, "China", "Ásia", 1_400_000_000L,
                        35, 55, 50, 100, 80.0,
                        PaisCategoria.MERCADO_BLOQUEADO,
                        "Mercado inviável. Possui grande dificuldade de domínio, " +
                                "mas com retorno muito valioso."
                ),

                new Pais(
                        8L, "Nigéria", "África", 235_000_000L,
                        80, 85, 90, 25, 15.0,
                        PaisCategoria.ENTRADA_FACIL,
                        "Mercado emergente com condições favoráveis para uma " +
                                "expansão inicial. Possui facilidade de entrada, mas " +
                                "retorno estratégico internacional limitado."
                ),

                new Pais(
                        9L, "África do Sul", "África", 65_000_000L,
                        65, 70, 75, 55, 25.0,
                        PaisCategoria.CRESCIMENTO_PROMISSOR,
                        "Mercado com grande potencial de crescimento e retorno. " +
                                "Possui dificuldade considerável e limites de expansão."
                ),

                new Pais(
                        10L, "Austrália", "Oceania", 25_000_000L,
                        80, 65, 45, 80, 20.0,
                        PaisCategoria.EXPANSAO_LIMITADA,
                        "Mercado com barreiras relevantes à expansão e potencial " +
                                "de crescimento interno. Sua principal vantagem " +
                                "estratégica está em suas conexões."
                )
        );

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            for (Pais pais : paises) {

                statement.setLong(1, pais.getId());
                statement.setString(2, pais.getNome());
                statement.setString(3, pais.getContinente());
                statement.setLong(4, pais.getPopulacaoTotal());

                statement.setInt(5, pais.getAcessibilidade());
                statement.setInt(6, pais.getReceptividade());
                statement.setInt(7, pais.getVulnerabilidade());
                statement.setInt(8, pais.getResistencia());

                statement.setDouble(9, pais.getBonusExpansaoPercentual());
                statement.setString(10, pais.getCategoria().name());
                statement.setString(11, pais.getDescricao());

                statement.addBatch();
            }

            statement.executeBatch();
        }
    }
}
