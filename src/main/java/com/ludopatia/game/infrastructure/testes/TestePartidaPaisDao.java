package com.ludopatia.game.infrastructure.testes;

import com.ludopatia.game.infrastructure.database.ConnectionFactory;
import com.ludopatia.game.infrastructure.database.DatabaseInitializer;

import com.ludopatia.game.infrastructure.model.dao.PaisDao;
import com.ludopatia.game.infrastructure.model.dao.PartidaDao;
import com.ludopatia.game.infrastructure.model.dao.PartidaPaisDao;

import com.ludopatia.game.infrastructure.model.dao.impl.PaisDaoJDBC;
import com.ludopatia.game.infrastructure.model.dao.impl.PartidaDaoJDBC;
import com.ludopatia.game.infrastructure.model.dao.impl.PartidaPaisDaoJDBC;

import com.ludopatia.game.infrastructure.model.entities.Pais;
import com.ludopatia.game.infrastructure.model.entities.Partida;
import com.ludopatia.game.infrastructure.model.entities.PartidaPais;
import com.ludopatia.game.infrastructure.model.entities.StatusPartida;

import java.sql.Connection;
import java.sql.SQLException;

public class TestePartidaPaisDao {

    public static void main(String[] args) {

        try {
            DatabaseInitializer.inicializar();

            try (Connection connection = ConnectionFactory.getConnection()) {

                connection.setAutoCommit(false);

                try {
                    PaisDao paisDao = new PaisDaoJDBC(connection);
                    PartidaDao partidaDao = new PartidaDaoJDBC(connection);
                    PartidaPaisDao partidaPaisDao =
                            new PartidaPaisDaoJDBC(connection);

                    int idPais = 1;

                    Pais pais = paisDao.buscarPorId(idPais)
                            .orElseThrow(() -> new SQLException(
                                    "País não encontrado."
                            ));

                    long populacaoTotal = pais.getPopulacaoTotal();

                    // Cria uma partida temporária.
                    Partida partida = new Partida(
                            0L,
                            idPais,
                            "Empresa Teste",
                            StatusPartida.EM_ANDAMENTO,
                            0,
                            0,
                            null
                    );

                    long idPartida = partidaDao.criar(partida);

                    // Inicializa toda a população no estado neutro.
                    PartidaPais estadoInicial = new PartidaPais(
                            idPartida,
                            idPais,
                            populacaoTotal,
                            0,
                            0,
                            0,
                            0,
                            0,
                            null
                    );

                    partidaPaisDao.adicionar(estadoInicial);

                    PartidaPais registro =
                            partidaPaisDao.buscarPorPartidaEPais(
                                    idPartida, idPais
                            ).orElseThrow();

                    System.out.println("País: " + pais.getNome());
                    System.out.println(
                            "População neutra inicial: "
                                    + registro.getNumPopulacao()
                    );

                    // Simula uma atualização populacional.
                    PartidaPais estadoAtualizado = new PartidaPais(
                            idPartida,
                            idPais,
                            populacaoTotal - 1000,
                            1000,
                            0,
                            0,
                            0,
                            0,
                            null
                    );

                    boolean atualizado =
                            partidaPaisDao.atualizar(estadoAtualizado);

                    PartidaPais resultado =
                            partidaPaisDao.buscarPorPartidaEPais(
                                    idPartida, idPais
                            ).orElseThrow();

                    if (!atualizado ||
                            resultado.getNumExpostos() != 1000 ||
                            resultado.getNumPopulacao() !=
                                    populacaoTotal - 1000) {

                        throw new SQLException(
                                "Falha ao atualizar a população."
                        );
                    }

                    System.out.println(
                            "Atualização realizada: " + atualizado
                    );

                    System.out.println(
                            "População neutra atual: "
                                    + resultado.getNumPopulacao()
                    );

                    System.out.println(
                            "Expostos: " + resultado.getNumExpostos()
                    );

                    System.out.println(
                            "Países na partida: "
                                    + partidaPaisDao.listarPorPartida(
                                    idPartida
                            ).size()
                    );

                } finally {
                    // Desfaz todos os dados fictícios.
                    connection.rollback();
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro no teste de PartidaPais:");
            e.printStackTrace();
        }
    }
}
