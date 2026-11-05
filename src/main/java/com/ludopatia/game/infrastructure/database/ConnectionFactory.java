package com.ludopatia.game.infrastructure.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    // Caminho onde o banco será armazenado
    private static final String DIRETORIO =
            System.getProperty("user.home") + "/ludopatia";

    private static final String NOME_BANCO = "ludopatia.db";

    public static Connection getConnection() throws SQLException {

        try {
            Path diretorio = Path.of(DIRETORIO);

            Files.createDirectories(diretorio);

            Path arquivoBanco = diretorio.resolve(NOME_BANCO);

            String url = "jdbc:sqlite:" + arquivoBanco.toAbsolutePath();

            Connection connection = DriverManager.getConnection(url);

            try (var statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");
            }

            return connection;

        } catch (IOException e) {
            throw new SQLException(
                    "Erro ao criar diretório do banco.", e
            );
        }
    }
}