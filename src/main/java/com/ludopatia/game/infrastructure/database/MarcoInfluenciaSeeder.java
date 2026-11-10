package com.ludopatia.game.infrastructure.database;

import com.ludopatia.game.model.entities.TipoCondicaoMarco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MarcoInfluenciaSeeder {

    public static void popular(Connection connection)
            throws SQLException {

        String sql = """
                INSERT INTO marco_influencia (
                    id_marco,
                    nome,
                    descricao,
                    tipo_condicao,
                    valor_condicao,
                    valor_recompensa
                )
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT(id_marco) DO UPDATE SET
                    nome = excluded.nome,
                    descricao = excluded.descricao,
                    tipo_condicao = excluded.tipo_condicao,
                    valor_condicao = excluded.valor_condicao,
                    valor_recompensa = excluded.valor_recompensa
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            // MARCOS DE ESTRATÉGIAS

            adicionar(statement, 1,
                    "Primeiro Investimento",
                    "Ativar a primeira estratégia da partida.",
                    TipoCondicaoMarco.ESTRATEGIAS_DISTINTAS,
                    1, 2);

            adicionar(statement, 2,
                    "Portfólio Inicial",
                    "Ativar 3 estratégias diferentes.",
                    TipoCondicaoMarco.ESTRATEGIAS_DISTINTAS,
                    3, 3);

            adicionar(statement, 3,
                    "Diversificação Estratégica",
                    "Ativar pelo menos uma estratégia de cada categoria.",
                    TipoCondicaoMarco.CATEGORIAS_DISTINTAS,
                    3, 4);

            adicionar(statement, 4,
                    "Especialização Estratégica",
                    "Ativar 5 estratégias da mesma categoria.",
                    TipoCondicaoMarco.ESTRATEGIAS_MESMA_CATEGORIA,
                    5, 4);

            adicionar(statement, 5,
                    "Arsenal Ampliado",
                    "Ativar 12 estratégias diferentes na partida.",
                    TipoCondicaoMarco.ESTRATEGIAS_DISTINTAS,
                    12, 5);

            adicionar(statement, 6,
                    "Liderança Estratégica",
                    "Ativar 20 estratégias diferentes na partida.",
                    TipoCondicaoMarco.ESTRATEGIAS_DISTINTAS,
                    20, 7);

            // MARCOS DE EXPANSÃO

            adicionar(statement, 7,
                    "Primeira Expansão",
                    "Controlar 2 países, incluindo o país inicial.",
                    TipoCondicaoMarco.PAISES_CONTROLADOS,
                    2, 2);

            adicionar(statement, 8,
                    "Expansão Inicial",
                    "Controlar 3 países.",
                    TipoCondicaoMarco.PAISES_CONTROLADOS,
                    3, 5);

            adicionar(statement, 9,
                    "Expansão Regional",
                    "Controlar 5 países.",
                    TipoCondicaoMarco.PAISES_CONTROLADOS,
                    5, 8);

            adicionar(statement, 10,
                    "Expansão Ampliada",
                    "Controlar 7 países.",
                    TipoCondicaoMarco.PAISES_CONTROLADOS,
                    7, 10);

            adicionar(statement, 11,
                    "Domínio Global",
                    "Controlar os 10 países disponíveis no mapa.",
                    TipoCondicaoMarco.PAISES_CONTROLADOS,
                    10, 12);

            // NOVO MARCO: INÍCIO DA PARTIDA

            adicionar(statement, 12,
                    "Comece seu Negócio",
                    "Escolher o primeiro país para iniciar sua empresa.",
                    TipoCondicaoMarco.PAISES_CONTROLADOS,
                    1, 5);
        }
    }

    private static void adicionar(
            PreparedStatement statement,
            long id,
            String nome,
            String descricao,
            TipoCondicaoMarco tipo,
            int valorCondicao,
            int valorRecompensa) throws SQLException {

        statement.setLong(1, id);
        statement.setString(2, nome);
        statement.setString(3, descricao);
        statement.setString(4, tipo.name());
        statement.setInt(5, valorCondicao);
        statement.setInt(6, valorRecompensa);

        statement.executeUpdate();
    }
}
