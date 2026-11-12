package com.ludopatia.game.infrastructure.database;

import com.ludopatia.game.infrastructure.model.entities.TipoEstrategia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public final class TipoEstrategiaSeeder {

    private TipoEstrategiaSeeder() {
    }

    public static void popular(Connection connection) throws SQLException {

        String sql = """
                INSERT INTO tipo_estrategia (
                    id_tipo, nome, descricao
                )
                VALUES (?, ?, ?)
                ON CONFLICT(id_tipo) DO UPDATE SET
                    nome = excluded.nome,
                    descricao = excluded.descricao
                """;

        List<TipoEstrategia> tipos = List.of(

                new TipoEstrategia(
                        1L,
                        "Publicidade",
                        "Aumenta o alcance da casa de apostas e a " +
                                "probabilidade de conversão de pessoas expostas."
                ),

                new TipoEstrategia(
                        2L,
                        "Plataforma",
                        "Melhora a conversão e a continuidade da " +
                                "participação na simulação."
                ),

                new TipoEstrategia(
                        3L,
                        "Resistência",
                        "Reduz o agravamento e modifica as probabilidades " +
                                "de recuperação e conscientização."
                )
        );

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            for (TipoEstrategia tipo : tipos) {

                statement.setLong(1, tipo.getId());
                statement.setString(2, tipo.getNome());
                statement.setString(3, tipo.getDescricao());

                statement.addBatch();
            }

            statement.executeBatch();
        }
    }
}
