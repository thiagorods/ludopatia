
package com.ludopatia.game.infrastructure.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    public static void inicializar() throws SQLException {

        String sql = """
                CREATE TABLE IF NOT EXISTS pais (
                    id INTEGER PRIMARY KEY,
                    nome TEXT NOT NULL UNIQUE,
                    continente TEXT NOT NULL,

                    populacao_total INTEGER NOT NULL DEFAULT 0
                        CHECK (populacao_total >= 0),

                    acessibilidade INTEGER NOT NULL
                        CHECK (acessibilidade BETWEEN 0 AND 100),

                    receptividade INTEGER NOT NULL
                        CHECK (receptividade BETWEEN 0 AND 100),

                    vulnerabilidade INTEGER NOT NULL
                        CHECK (vulnerabilidade BETWEEN 0 AND 100),

                    resistencia INTEGER NOT NULL
                        CHECK (resistencia BETWEEN 0 AND 100),

                    bonus_expansao_percentual REAL NOT NULL
                        CHECK (bonus_expansao_percentual >= 0),

                    categoria TEXT NOT NULL
                        CHECK (categoria IN (
                            'ENTRADA_FACIL',
                            'CRESCIMENTO_PROMISSOR',
                            'EXPANSAO_LIMITADA',
                            'MERCADO_DESAFIADOR',
                            'MERCADO_BLOQUEADO'
                        )),

                    descricao TEXT NOT NULL DEFAULT ''
                )
                """;


                String sqlRecurso = """
                CREATE TABLE IF NOT EXISTS recurso (
                    id_recurso INTEGER PRIMARY KEY,
        
                    nome TEXT NOT NULL UNIQUE,
        
                    descricao TEXT NOT NULL,
        
                    valor_inicial INTEGER NOT NULL
                        CHECK (valor_inicial >= 0)
                )
                """;


        try (Connection connection = ConnectionFactory.getConnection()) {

            connection.setAutoCommit(false);

            try {

                try (Statement statement = connection.createStatement()) {

                    // Cria a tabela de países
                    statement.execute(sql);

                    // Cria a tabela de recursos
                    statement.execute(sqlRecurso);
                }


                adicionarColunaSeAusente(
                        connection,
                        "populacao_total",
                        "INTEGER NOT NULL DEFAULT 0 CHECK (populacao_total >= 0)"
                );

                adicionarColunaSeAusente(
                        connection,
                        "descricao",
                        "TEXT NOT NULL DEFAULT ''"
                );

                // Cadastra os países iniciais
                PaisSeeder.popular(connection);

                // Cadastra os recursos iniciais
                RecursoSeeder.popular(connection);

                connection.commit();

                System.out.println("Banco de dados inicializado com sucesso!");

            } catch (SQLException | RuntimeException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackErro) {
                    e.addSuppressed(rollbackErro);
                }

                throw e;
            }
        }
    }

    private static void adicionarColunaSeAusente(
            Connection connection,
            String nomeColuna,
            String definicao) throws SQLException {

        boolean existe = false;

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("PRAGMA table_info(pais)")) {

            while (rs.next()) {
                if (nomeColuna.equalsIgnoreCase(rs.getString("name"))) {
                    existe = true;
                    break;
                }
            }
        }

        if (!existe) {
            String sql = "ALTER TABLE pais ADD COLUMN "
                    + nomeColuna + " " + definicao;

            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        }
    }
}
