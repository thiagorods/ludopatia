
package com.ludopatia.game;

import com.ludopatia.game.infrastructure.database.ConnectionFactory;
import com.ludopatia.game.infrastructure.database.DatabaseInitializer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TesteInicializacaoBanco {

    public static void main(String[] args) {

        try {
            DatabaseInitializer.inicializar();

            try (Connection connection = ConnectionFactory.getConnection();
                 Statement statement = connection.createStatement()) {

                System.out.println("\nCaminho do banco:");

                try (ResultSet rs = statement.executeQuery(
                        "PRAGMA database_list")) {

                    while (rs.next()) {
                        System.out.println(rs.getString("file"));
                    }
                }

                System.out.println("\nTabelas encontradas:");

                try (ResultSet rs = statement.executeQuery(
                        "SELECT name FROM sqlite_master WHERE type = 'table'")) {

                    while (rs.next()) {
                        System.out.println(rs.getString("name"));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
