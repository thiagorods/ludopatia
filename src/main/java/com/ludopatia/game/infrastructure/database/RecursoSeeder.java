
package com.ludopatia.game.infrastructure.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class RecursoSeeder {

    private static final long ID_INFLUENCIA = 1L;

    // TODO: Definir o valor inicial com a equipe.
    private static final int VALOR_INICIAL_INFLUENCIA = 0;

    private RecursoSeeder() {
    }

    public static void popular(Connection connection) throws SQLException {

        String sql = """
                INSERT INTO recurso (
                    id_recurso,
                    nome,
                    descricao,
                    valor_inicial
                )
                VALUES (?, ?, ?, ?)
                ON CONFLICT(id_recurso) DO UPDATE SET
                    nome = excluded.nome,
                    descricao = excluded.descricao,
                    valor_inicial = excluded.valor_inicial
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, ID_INFLUENCIA);

            statement.setString(2, "Influência");

            statement.setString(3,
                    "Recurso utilizado para ativar estratégias durante " +
                            "a partida. Pode ser obtido por meio de recompensas " +
                            "e marcos conquistados pelo jogador."
            );

            statement.setInt(4, VALOR_INICIAL_INFLUENCIA);

            statement.executeUpdate();
        }
    }
}
