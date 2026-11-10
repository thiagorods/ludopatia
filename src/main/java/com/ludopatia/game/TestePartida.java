package com.ludopatia.game;

import com.ludopatia.game.infrastructure.database.ConnectionFactory;
import com.ludopatia.game.infrastructure.database.DatabaseInitializer;

import com.ludopatia.game.model.dao.PartidaDao;
import com.ludopatia.game.model.dao.impl.PartidaDaoJDBC;
import com.ludopatia.game.model.entities.Partida;

import com.ludopatia.game.service.PartidaService;

import java.sql.Connection;
import java.sql.SQLException;

public class TestePartida {

    public static void main(String[] args) {

        try {
            DatabaseInitializer.inicializar();

            PartidaService service = new PartidaService();

            // Brasil possui ID 1 no catálogo.
            long idPartida = service.criarPartida(
                    "Empresa Teste Ludopatia",
                    1L
            );

            System.out.println("Partida criada! ID: " + idPartida);

            try (Connection connection = ConnectionFactory.getConnection()) {

                PartidaDao partidaDao = new PartidaDaoJDBC(connection);

                Partida partida = partidaDao.buscarPorId(idPartida)
                        .orElseThrow(() -> new SQLException(
                                "A partida criada não foi encontrada."
                        ));

                System.out.println("-----------------------------");
                System.out.println("Empresa: " + partida.getNomeEmpresa());
                System.out.println("País inicial ID: " + partida.getIdPaisInicial());
                System.out.println("Status: " + partida.getStatus());
                System.out.println("Ciclo: " + partida.getCiclo());
                System.out.println("Influência: " + partida.getValorInfluencia());
                System.out.println("Capital: " + partida.getValorCapital());

                System.out.println("\nTotal de partidas salvas: "
                        + partidaDao.listarTodas().size());
            }

        } catch (SQLException e) {
            System.err.println("Erro ao testar a partida:");
            e.printStackTrace();
        }
    }
}
