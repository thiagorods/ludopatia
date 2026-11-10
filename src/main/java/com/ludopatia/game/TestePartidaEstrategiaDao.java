package com.ludopatia.game;

import com.ludopatia.game.infrastructure.database.ConnectionFactory;
import com.ludopatia.game.infrastructure.database.DatabaseInitializer;

import com.ludopatia.game.model.dao.PartidaDao;
import com.ludopatia.game.model.dao.PartidaEstrategiaDao;

import com.ludopatia.game.model.dao.impl.PartidaDaoJDBC;
import com.ludopatia.game.model.dao.impl.PartidaEstrategiaDaoJDBC;

import com.ludopatia.game.model.entities.Partida;
import com.ludopatia.game.model.entities.PartidaEstrategia;
import com.ludopatia.game.model.entities.StatusPartida;

import java.sql.Connection;
import java.sql.SQLException;

public class TestePartidaEstrategiaDao {

    public static void main(String[] args) {

        try {
            DatabaseInitializer.inicializar();

            try (Connection connection = ConnectionFactory.getConnection()) {

                connection.setAutoCommit(false);

                try {
                    PartidaDao partidaDao =
                            new PartidaDaoJDBC(connection);

                    PartidaEstrategiaDao estrategiaDao =
                            new PartidaEstrategiaDaoJDBC(connection);

                    // Cria uma partida temporária.
                    Partida partida = new Partida(
                            0L,
                            1L,
                            "Empresa Teste",
                            StatusPartida.EM_ANDAMENTO,
                            0,
                            10,
                            null
                    );

                    long idPartida = partidaDao.criar(partida);

                    // IDs 1 e 2 são estratégias do catálogo.
                    estrategiaDao.adicionar(
                            new PartidaEstrategia(idPartida, 1L)
                    );

                    estrategiaDao.adicionar(
                            new PartidaEstrategia(idPartida, 2L)
                    );

                    System.out.println(
                            "Estratégias registradas: " +
                                    estrategiaDao.listarPorPartida(idPartida).size()
                    );

                    System.out.println(
                            "Estratégia 1 existe: " +
                                    estrategiaDao.existe(idPartida, 1L)
                    );

                    boolean removida =
                            estrategiaDao.remover(idPartida, 2L);

                    System.out.println(
                            "Estratégia 2 removida: " + removida
                    );

                    System.out.println(
                            "Estratégias restantes: " +
                                    estrategiaDao.listarPorPartida(idPartida).size()
                    );

                } finally {
                    // Desfaz os dados temporários do teste.
                    connection.rollback();
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro no teste:");
            e.printStackTrace();
        }
    }
}
