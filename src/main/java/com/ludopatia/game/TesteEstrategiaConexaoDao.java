package com.ludopatia.game;

import com.ludopatia.game.infrastructure.database.ConnectionFactory;
import com.ludopatia.game.infrastructure.database.DatabaseInitializer;

import com.ludopatia.game.model.dao.EstrategiaConexaoDao;
import com.ludopatia.game.model.dao.impl.EstrategiaConexaoDaoJDBC;
import com.ludopatia.game.model.entities.EstrategiaConexao;

import java.sql.Connection;
import java.sql.SQLException;

public class TesteEstrategiaConexaoDao {

    public static void main(String[] args) {

        try {
            DatabaseInitializer.inicializar();

            try (Connection connection = ConnectionFactory.getConnection()) {

                connection.setAutoCommit(false);

                try {
                    EstrategiaConexaoDao dao =
                            new EstrategiaConexaoDaoJDBC(connection);

                    System.out.println(
                            "Conexões iniciais: " + dao.listarTodas().size()
                    );

                    // Conexão temporária somente para testar o DAO
                    EstrategiaConexao conexao =
                            new EstrategiaConexao(1L, 2L);

                    dao.adicionar(conexao);

                    System.out.println(
                            "Conexão adicionada: " + dao.existe(1L, 2L)
                    );

                    System.out.println(
                            "Conexões saindo da estratégia 1: " +
                            dao.listarPorOrigem(1L).size()
                    );

                    System.out.println(
                            "Conexões chegando à estratégia 2: " +
                            dao.listarPorDestino(2L).size()
                    );

                    boolean removida = dao.remover(1L, 2L);

                    System.out.println("Conexão removida: " + removida);

                    System.out.println(
                            "Conexão ainda existe: " + dao.existe(1L, 2L)
                    );

                } finally {
                    // Nenhuma alteração de teste é salva definitivamente.
                    connection.rollback();
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro no teste de conexões:");
            e.printStackTrace();
        }
    }
}
