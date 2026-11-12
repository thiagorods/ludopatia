package com.ludopatia.game.infrastructure.testes;

import com.ludopatia.game.infrastructure.database.ConnectionFactory;
import com.ludopatia.game.infrastructure.database.DatabaseInitializer;

import com.ludopatia.game.infrastructure.model.dao.RecursoDao;
import com.ludopatia.game.infrastructure.model.dao.impl.RecursoDaoJDBC;
import com.ludopatia.game.infrastructure.model.entities.Recurso;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TesteRecursoDao {

    public static void main(String[] args) {

        try {
            // Prepara o banco e cadastra os dados iniciais
            DatabaseInitializer.inicializar();

            try (Connection connection = ConnectionFactory.getConnection()) {

                RecursoDao recursoDao = new RecursoDaoJDBC(connection);

                // Testa a listagem
                List<Recurso> recursos = recursoDao.listarTodos();

                System.out.println("Total de recursos: " + recursos.size());

                for (Recurso recurso : recursos) {
                    System.out.println("--------------------");
                    System.out.println("ID: " + recurso.getId());
                    System.out.println("Nome: " + recurso.getNome());
                    System.out.println("Descrição: " + recurso.getDescricao());
                    System.out.println("Valor inicial: " + recurso.getValorInicial());
                }

                // Testa a busca por ID
                System.out.println("\nBusca por ID:");

                recursoDao.buscarPorId(1L).ifPresent(recurso ->
                        System.out.println("Encontrado: " + recurso.getNome())
                );

                // Testa a busca por nome
                System.out.println("\nBusca por nome:");

                recursoDao.buscarPorNome("Influência").ifPresent(recurso ->
                        System.out.println("Encontrado: " + recurso.getNome())
                );
            }

        } catch (SQLException e) {
            System.err.println("Erro ao consultar recursos:");
            e.printStackTrace();
        }
    }
}
