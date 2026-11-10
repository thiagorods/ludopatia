package com.ludopatia.game;

import com.ludopatia.game.infrastructure.database.ConnectionFactory;
import com.ludopatia.game.infrastructure.database.DatabaseInitializer;

import com.ludopatia.game.model.dao.NoticiaDao;
import com.ludopatia.game.model.dao.PartidaNoticiaDao;

import com.ludopatia.game.model.dao.impl.NoticiaDaoJDBC;
import com.ludopatia.game.model.dao.impl.PartidaNoticiaDaoJDBC;

import com.ludopatia.game.model.entities.Noticia;
import com.ludopatia.game.model.entities.PartidaNoticia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TesteNoticiaDao {

    public static void main(String[] args) {

        try {
            DatabaseInitializer.inicializar();

            try (Connection connection = ConnectionFactory.getConnection()) {

                connection.setAutoCommit(false);

                try {
                    // Cria uma partida temporária com o Brasil.
                    long idPartida;

                    String sqlPartida = """
                            INSERT INTO partida (
                                id_pais_inicial, nome_empresa
                            )
                            VALUES (?, ?)
                            """;

                    try (PreparedStatement statement =
                                 connection.prepareStatement(sqlPartida)) {

                        statement.setLong(1, 1L);
                        statement.setString(2, "Empresa de Teste");

                        statement.executeUpdate();
                    }

                    try (Statement statement = connection.createStatement();
                         ResultSet rs = statement.executeQuery(
                                 "SELECT last_insert_rowid()")) {

                        if (!rs.next()) {
                            throw new SQLException("Partida não criada.");
                        }

                        idPartida = rs.getLong(1);
                    }

                    NoticiaDao noticiaDao =
                            new NoticiaDaoJDBC(connection);

                    PartidaNoticiaDao partidaNoticiaDao =
                            new PartidaNoticiaDaoJDBC(connection);

                    // Cadastra uma notícia temporária.
                    Noticia noticia = new Noticia(
                            0L,
                            "Campanha educativa sobre apostas",
                            "Uma campanha informa a população sobre os riscos das apostas online.",
                            "EVENTO"
                    );

                    long idNoticia = noticiaDao.criar(noticia);

                    System.out.println("Notícia criada! ID: " + idNoticia);

                    noticiaDao.buscarPorId(idNoticia).ifPresent(n ->
                            System.out.println("Título: " + n.getTitulo())
                    );

                    // Registra a notícia no ciclo 5 da partida.
                    partidaNoticiaDao.registrar(
                            new PartidaNoticia(idPartida, idNoticia, 5)
                    );

                    var historico =
                            partidaNoticiaDao.listarPorPartida(idPartida);

                    System.out.println(
                            "Notícias da partida: " + historico.size()
                    );

                    for (PartidaNoticia registro : historico) {
                        System.out.println(
                                "Notícia ID: " + registro.getIdNoticia()
                                        + " | Ciclo: " + registro.getCicloGerado()
                        );
                    }

                } finally {
                    // Não salva os dados fictícios do teste.
                    connection.rollback();
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro no teste de notícias:");
            e.printStackTrace();
        }
    }
}
