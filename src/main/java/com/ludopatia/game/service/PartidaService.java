package com.ludopatia.game.service;

import com.ludopatia.game.infrastructure.database.ConnectionFactory;
import com.ludopatia.game.infrastructure.model.dao.PaisDao;
import com.ludopatia.game.infrastructure.model.dao.RecursoDao;
import com.ludopatia.game.infrastructure.model.dao.PartidaDao;
import com.ludopatia.game.infrastructure.model.dao.PartidaPaisDao;
import com.ludopatia.game.infrastructure.model.dao.impl.PaisDaoJDBC;
import com.ludopatia.game.infrastructure.model.dao.impl.RecursoDaoJDBC;
import com.ludopatia.game.infrastructure.model.dao.impl.PartidaDaoJDBC;
import com.ludopatia.game.infrastructure.model.dao.impl.PartidaPaisDaoJDBC;
import com.ludopatia.game.infrastructure.model.entities.Pais;
import com.ludopatia.game.infrastructure.model.entities.Partida;
import com.ludopatia.game.infrastructure.model.entities.PartidaPais;
import com.ludopatia.game.infrastructure.model.entities.Recurso;
import com.ludopatia.game.infrastructure.model.entities.StatusPartida;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Coordena os casos de uso relacionados à persistência de uma partida.
 * As operações que alteram mais de uma tabela são executadas na mesma transação.
 */
public class PartidaService {

    /**
     * Cria e persiste uma partida inicializada no país escolhido.
     *
     * @return a representação da partida já persistida, incluindo o ID gerado
     *         e os valores atualizados após a concessão dos marcos iniciais.
     */
    public Partida criarPartida(String nomeEmpresa, int idPaisInicial)
            throws SQLException {

        try (Connection connection = ConnectionFactory.getConnection()) {
            connection.setAutoCommit(false);

            try {
                PaisDao paisDao = new PaisDaoJDBC(connection);
                Pais paisInicial = paisDao.buscarPorId(idPaisInicial)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "O país selecionado não existe."
                        ));

                RecursoDao recursoDao = new RecursoDaoJDBC(connection);
                Recurso influencia = recursoDao.buscarPorNome("Influência")
                        .orElseThrow(() -> new SQLException(
                                "O recurso Influência não está cadastrado."
                        ));

                Partida partidaNova = new Partida(
                        0L,
                        idPaisInicial,
                        nomeEmpresa,
                        StatusPartida.EM_ANDAMENTO,
                        0,
                        influencia.getValorInicial(),
                        null
                );

                PartidaDao partidaDao = new PartidaDaoJDBC(connection);
                long idPartida = partidaDao.criar(partidaNova);

                // Registra também o estado populacional do país inicial.
                // Neste momento, toda a população está no estado "não expostos".
                PartidaPaisDao partidaPaisDao =
                        new PartidaPaisDaoJDBC(connection);

                partidaPaisDao.adicionar(new PartidaPais(
                        idPartida,
                        idPaisInicial,
                        paisInicial.getPopulacaoTotal(),
                        0,
                        0,
                        0,
                        0,
                        0,
                        null
                ));

                // O marco inicial pode conceder Influência e atualiza a tabela
                // partida dentro desta mesma transação.
                MarcoInfluenciaService marcoService =
                        new MarcoInfluenciaService(connection);

                marcoService.avaliarEConceder(idPartida, 0, 1);

                // Recarrega a entidade para devolver os valores efetivamente
                // persistidos, inclusive eventual recompensa do marco inicial.
                Partida partidaSalva = partidaDao.buscarPorId(idPartida)
                        .orElseThrow(() -> new SQLException(
                                "A partida foi criada, mas não pôde ser recuperada."
                        ));

                connection.commit();
                return partidaSalva;

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

    /*
     * Próxima evolução: implementar salvar/atualizar o estado completo da
     * partida em uma única transação (partida, países, estratégias, notícias,
     * marcos etc.). Evite espalhar commits por DAOs ou controllers.
     */
}
