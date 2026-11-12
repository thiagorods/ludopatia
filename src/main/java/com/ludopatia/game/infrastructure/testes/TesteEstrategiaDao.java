package com.ludopatia.game.infrastructure.testes;

import com.ludopatia.game.infrastructure.database.ConnectionFactory;
import com.ludopatia.game.infrastructure.database.DatabaseInitializer;

import com.ludopatia.game.infrastructure.model.dao.EstrategiaDao;
import com.ludopatia.game.infrastructure.model.dao.TipoEstrategiaDao;

import com.ludopatia.game.infrastructure.model.dao.impl.EstrategiaDaoJDBC;
import com.ludopatia.game.infrastructure.model.dao.impl.TipoEstrategiaDaoJDBC;

import com.ludopatia.game.infrastructure.model.entities.Estrategia;
import com.ludopatia.game.infrastructure.model.entities.TipoEstrategia;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TesteEstrategiaDao {

    public static void main(String[] args) {

        try {
            DatabaseInitializer.inicializar();

            try (Connection connection = ConnectionFactory.getConnection()) {

                TipoEstrategiaDao tipoDao =
                        new TipoEstrategiaDaoJDBC(connection);

                EstrategiaDao estrategiaDao =
                        new EstrategiaDaoJDBC(connection);

                List<TipoEstrategia> tipos = tipoDao.listarTodos();

                List<Estrategia> todas = estrategiaDao.listarTodas();

                System.out.println("Total de tipos: " + tipos.size());
                System.out.println("Total de estratégias: " + todas.size());

                for (TipoEstrategia tipo : tipos) {

                    List<Estrategia> estrategias =
                            estrategiaDao.listarPorTipo(tipo.getId());

                    int custoTotal = estrategias.stream()
                            .mapToInt(Estrategia::getCustoInfluencia)
                            .sum();

                    System.out.println("\nCategoria: " + tipo.getNome());
                    System.out.println("Estratégias: " + estrategias.size());
                    System.out.println("Custo total: " + custoTotal);

                    for (Estrategia estrategia : estrategias) {
                        System.out.println(
                                estrategia.getId() + " - " +
                                        estrategia.getTitulo() +
                                        " (custo: " +
                                        estrategia.getCustoInfluencia() + ")"
                        );
                    }
                }


                for (TipoEstrategia tipo : tipos) {

                    System.out.println("----------------------------");
                    System.out.println("ID: " + tipo.getId());
                    System.out.println("Nome: " + tipo.getNome());
                    System.out.println("Descrição: " + tipo.getDescricao());
                }


                System.out.println("\nTeste de busca por ID:");

                estrategiaDao.buscarPorId(24L).ifPresent(estrategia -> {
                    System.out.println("Encontrada: " + estrategia.getTitulo());
                    System.out.println(
                            "Agravamento: " + estrategia.getValorAgravamento()
                    );
                    System.out.println(
                            "Recuperação: " + estrategia.getValorRecuperacao()
                    );
                });
            }

        } catch (SQLException e) {
            System.err.println("Erro ao testar estratégias:");
            e.printStackTrace();
        }
    }
}
