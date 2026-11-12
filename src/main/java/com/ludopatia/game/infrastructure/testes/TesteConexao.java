package com.ludopatia.game.infrastructure.testes;

import com.ludopatia.game.infrastructure.database.ConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class TesteConexao {

    public static void main(String[] args) {

        try (Connection connection =
                     ConnectionFactory.getConnection()) {

            System.out.println("Conexão com SQLite estabelecida!");

        } catch (SQLException e) {
            System.err.println("Erro ao conectar com o banco:");
            e.printStackTrace();
        }
    }
}