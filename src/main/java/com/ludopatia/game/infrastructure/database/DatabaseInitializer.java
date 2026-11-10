package com.ludopatia.game.infrastructure.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    private static void garantirDescricaoTipo(Connection connection)
            throws SQLException {

        boolean existe = false;

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(
                     "PRAGMA table_info(tipo_estrategia)")) {

            while (rs.next()) {

                if ("descricao".equalsIgnoreCase(rs.getString("name"))) {
                    existe = true;
                    break;
                }
            }
        }

        if (!existe) {

            String sql = """
                ALTER TABLE tipo_estrategia
                ADD COLUMN descricao TEXT NOT NULL DEFAULT ''
                """;

            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        }
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



                String sqlTipoEstrategia = """
                CREATE TABLE IF NOT EXISTS tipo_estrategia (
                    id_tipo INTEGER PRIMARY KEY,
                    nome TEXT NOT NULL UNIQUE,
                    descricao TEXT NOT NULL
                )
                """;


        String sqlEstrategia = """
                CREATE TABLE IF NOT EXISTS estrategia (
                    id_estrategia INTEGER PRIMARY KEY,
                    id_tipo INTEGER NOT NULL,
        
                    titulo TEXT NOT NULL,
                    descricao TEXT NOT NULL,
                    icone TEXT,
        
                    custo_influencia INTEGER
                        CHECK (custo_influencia >= 0),
        
                    custo_capital INTEGER
                        CHECK (custo_capital >= 0),
        
                    valor_exposicao REAL
                        CHECK (valor_exposicao BETWEEN 0 AND 1),
        
                    valor_conversao REAL,
                    valor_retencao REAL,
                    valor_agravamento REAL,
                    valor_recuperacao REAL,
                    valor_conscientizacao REAL,
                    valor_expansao REAL,
        
                    FOREIGN KEY (id_tipo)
                        REFERENCES tipo_estrategia(id_tipo)
                )
                """;


                String sqlEstrategiaConexao = """
                CREATE TABLE IF NOT EXISTS estrategia_conexao (
                    id_estrategia_origem INTEGER NOT NULL,
                    id_estrategia_destino INTEGER NOT NULL,
        
                    PRIMARY KEY (
                        id_estrategia_origem,
                        id_estrategia_destino
                    ),
        
                    FOREIGN KEY (id_estrategia_origem)
                        REFERENCES estrategia(id_estrategia),
        
                    FOREIGN KEY (id_estrategia_destino)
                        REFERENCES estrategia(id_estrategia)
                )
                """;


                String sqlPartida = """
                CREATE TABLE IF NOT EXISTS partida (
                    id_partida INTEGER PRIMARY KEY,
        
                    id_pais_inicial INTEGER NOT NULL,
        
                    nome_empresa TEXT NOT NULL
                        CHECK (LENGTH(TRIM(nome_empresa)) > 0),
        
                    status TEXT NOT NULL DEFAULT 'EM_ANDAMENTO'
                        CHECK (status IN (
                            'EM_ANDAMENTO',
                            'PAUSADO',
                            'VITORIA',
                            'DERROTA'
                        )),
        
                    ciclo INTEGER NOT NULL DEFAULT 0
                        CHECK (ciclo >= 0),
        
                    valor_influencia INTEGER NOT NULL DEFAULT 0
                        CHECK (valor_influencia >= 0),
        
                    valor_capital INTEGER
                        CHECK (valor_capital >= 0),
        
                    FOREIGN KEY (id_pais_inicial)
                        REFERENCES pais(id)
                )
                """;


                String sqlNoticia = """
                CREATE TABLE IF NOT EXISTS noticia (
                    id_noticia INTEGER PRIMARY KEY,
        
                    titulo TEXT NOT NULL
                        CHECK (LENGTH(TRIM(titulo)) > 0),
        
                    descricao TEXT NOT NULL
                        CHECK (LENGTH(TRIM(descricao)) > 0),
        
                    tipo TEXT NOT NULL
                        CHECK (LENGTH(TRIM(tipo)) > 0)
                )
                """;


                String sqlPartidaNoticias = """
                CREATE TABLE IF NOT EXISTS partida_noticias (
                    id_partida INTEGER NOT NULL,
                    id_noticia INTEGER NOT NULL,
        
                    ciclo_gerado INTEGER NOT NULL
                        CHECK (ciclo_gerado >= 0),
        
                    PRIMARY KEY (
                        id_partida,
                        id_noticia,
                        ciclo_gerado
                    ),
        
                    FOREIGN KEY (id_partida)
                        REFERENCES partida(id_partida),
        
                    FOREIGN KEY (id_noticia)
                        REFERENCES noticia(id_noticia)
                )
                """;


                String sqlPartidaEstrategia = """
                CREATE TABLE IF NOT EXISTS partida_estrategia (
                    id_partida INTEGER NOT NULL,
                    id_estrategia INTEGER NOT NULL,
        
                    PRIMARY KEY (
                        id_partida,
                        id_estrategia
                    ),
        
                    FOREIGN KEY (id_partida)
                        REFERENCES partida(id_partida),
        
                    FOREIGN KEY (id_estrategia)
                        REFERENCES estrategia(id_estrategia)
                )
                """;



                String sqlPartidaPais = """
                CREATE TABLE IF NOT EXISTS partida_pais (
                    id_partida INTEGER NOT NULL,
                    id_pais INTEGER NOT NULL,
        
                    num_populacao INTEGER NOT NULL DEFAULT 0
                        CHECK (num_populacao >= 0),
        
                    num_expostos INTEGER NOT NULL DEFAULT 0
                        CHECK (num_expostos >= 0),
        
                    num_jogadores INTEGER NOT NULL DEFAULT 0
                        CHECK (num_jogadores >= 0),
        
                    num_jogadores_recorrentes INTEGER NOT NULL DEFAULT 0
                        CHECK (num_jogadores_recorrentes >= 0),
        
                    num_jogadores_em_risco INTEGER NOT NULL DEFAULT 0
                        CHECK (num_jogadores_em_risco >= 0),
        
                    num_recuperados INTEGER NOT NULL DEFAULT 0
                        CHECK (num_recuperados >= 0),
        
                    valor_reacao_pais REAL,
        
                    PRIMARY KEY (
                        id_partida,
                        id_pais
                    ),
        
                    FOREIGN KEY (id_partida)
                        REFERENCES partida(id_partida),
        
                    FOREIGN KEY (id_pais)
                        REFERENCES pais(id)
                )
                """;



                String sqlMarcoInfluencia = """
                CREATE TABLE IF NOT EXISTS marco_influencia (
                    id_marco INTEGER PRIMARY KEY,
        
                    nome TEXT NOT NULL UNIQUE
                        CHECK (LENGTH(TRIM(nome)) > 0),
        
                    descricao TEXT NOT NULL,
        
                    tipo_condicao TEXT NOT NULL
                        CHECK (tipo_condicao IN (
                            'ESTRATEGIAS_DISTINTAS',
                            'CATEGORIAS_DISTINTAS',
                            'ESTRATEGIAS_MESMA_CATEGORIA',
                            'PAISES_CONTROLADOS'
                        )),
        
                    valor_condicao INTEGER NOT NULL
                        CHECK (valor_condicao > 0),
        
                    valor_recompensa INTEGER NOT NULL
                        CHECK (valor_recompensa >= 0)
                )
                """;


                String sqlPartidaMarco = """
                CREATE TABLE IF NOT EXISTS partida_marco (
                    id_partida INTEGER NOT NULL,
                    id_marco INTEGER NOT NULL,
        
                    ciclo_concluido INTEGER NOT NULL
                        CHECK (ciclo_concluido >= 0),
        
                    PRIMARY KEY (id_partida, id_marco),
        
                    FOREIGN KEY (id_partida)
                        REFERENCES partida(id_partida),
        
                    FOREIGN KEY (id_marco)
                        REFERENCES marco_influencia(id_marco)
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

                    // Cria a tabela de tipo de estratégias
                    statement.execute(sqlTipoEstrategia);

                    // Cria a tabela de estratégias
                    statement.execute(sqlEstrategia);

                    // Cria a tabela de conexões das estratégias
                    statement.execute(sqlEstrategiaConexao);

                    // Cria a tabela de partidas
                    statement.execute(sqlPartida);

                    // Cria a tabela de notícias
                    statement.execute(sqlNoticia);

                    // Cria a tabela de notícias das partidas
                    statement.execute(sqlPartidaNoticias);

                    // Cria a tabela de partida estrategia
                    statement.execute(sqlPartidaEstrategia);

                    // Cria a tabela de partida país
                    statement.execute(sqlPartidaPais);

                    // Cria a tabela de marco de influencia
                    statement.execute(sqlMarcoInfluencia);

                    // Cria a tabela de partida marco
                    statement.execute(sqlPartidaMarco);
                }


                
                // Atualiza as colunas da tabela pais
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

                // Garante a coluna descricao em tipo_estrategia
                garantirDescricaoTipo(connection);

                // Cadastra os países iniciais
                PaisSeeder.popular(connection);

                // Cadastra os recursos iniciais
                RecursoSeeder.popular(connection);

                // Cadastra os tipos de estratégias
                TipoEstrategiaSeeder.popular(connection);

                // Cadastra as estratégias
                EstrategiaSeeder.popular(connection);

                // Cadastra os marcos de influencia
                MarcoInfluenciaSeeder.popular(connection);

                // Confirma todas as operações
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
