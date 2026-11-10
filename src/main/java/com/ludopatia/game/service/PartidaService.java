
package com.ludopatia.game.service;

import com.ludopatia.game.infrastructure.database.ConnectionFactory;

import com.ludopatia.game.model.dao.PaisDao;
import com.ludopatia.game.model.dao.RecursoDao;
import com.ludopatia.game.model.dao.PartidaDao;

import com.ludopatia.game.model.dao.impl.PaisDaoJDBC;
import com.ludopatia.game.model.dao.impl.RecursoDaoJDBC;
import com.ludopatia.game.model.dao.impl.PartidaDaoJDBC;

import com.ludopatia.game.model.entities.Partida;
import com.ludopatia.game.model.entities.Recurso;
import com.ludopatia.game.model.entities.StatusPartida;

import java.sql.Connection;
import java.sql.SQLException;

public class PartidaService {

    public long criarPartida(
            String nomeEmpresa,
            long idPaisInicial) throws SQLException {

        try (Connection connection =
                     ConnectionFactory.getConnection()) {

            connection.setAutoCommit(false);

            try {
                // Verifica se o país escolhido existe.
                PaisDao paisDao = new PaisDaoJDBC(connection);

                if (paisDao.buscarPorId(idPaisInicial).isEmpty()) {
                    throw new IllegalArgumentException(
                            "O país selecionado não existe."
                    );
                }

                // Recupera o valor inicial de Influência.
                RecursoDao recursoDao =
                        new RecursoDaoJDBC(connection);

                Recurso influencia = recursoDao
                        .buscarPorNome("Influência")
                        .orElseThrow(() -> new SQLException(
                                "O recurso Influência não está cadastrado."
                        ));

                // Cria a partida no ciclo zero.
                Partida partida = new Partida(
                        0L,
                        idPaisInicial,
                        nomeEmpresa,
                        StatusPartida.EM_ANDAMENTO,
                        0,
                        influencia.getValorInicial(),
                        null
                );

                PartidaDao partidaDao =
                        new PartidaDaoJDBC(connection);

                long idPartida = partidaDao.criar(partida);

                // O país inicial já foi escolhido.
                // Portanto, a partida começa com 1 país.
                MarcoInfluenciaService marcoService =
                        new MarcoInfluenciaService(connection);

                marcoService.avaliarEConceder(
                        idPartida,
                        0,
                        1
                );

                // Partida e recompensa são salvas juntas.
                connection.commit();

                return idPartida;

            } catch (SQLException | RuntimeException e) {

                try {
                    connection.rollback();
                } catch (SQLException erroRollback) {
                    e.addSuppressed(erroRollback);
                }

                throw e;
            }
        }
    }
}
