
package com.ludopatia.game;

import com.ludopatia.game.infrastructure.database.ConnectionFactory;
import com.ludopatia.game.infrastructure.database.DatabaseInitializer;

import com.ludopatia.game.model.dao.MarcoInfluenciaDao;
import com.ludopatia.game.model.dao.PartidaDao;
import com.ludopatia.game.model.dao.PartidaEstrategiaDao;
import com.ludopatia.game.model.dao.PartidaMarcoDao;

import com.ludopatia.game.model.dao.impl.MarcoInfluenciaDaoJDBC;
import com.ludopatia.game.model.dao.impl.PartidaDaoJDBC;
import com.ludopatia.game.model.dao.impl.PartidaEstrategiaDaoJDBC;
import com.ludopatia.game.model.dao.impl.PartidaMarcoDaoJDBC;

import com.ludopatia.game.model.entities.MarcoInfluencia;
import com.ludopatia.game.model.entities.Partida;
import com.ludopatia.game.model.entities.PartidaEstrategia;
import com.ludopatia.game.model.entities.StatusPartida;

import com.ludopatia.game.service.MarcoInfluenciaService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TesteMarcoInfluencia {

    public static void main(String[] args) {

        try {
            DatabaseInitializer.inicializar();

            try (Connection connection =
                         ConnectionFactory.getConnection()) {

                connection.setAutoCommit(false);

                try {
                    MarcoInfluenciaDao marcoDao =
                            new MarcoInfluenciaDaoJDBC(connection);

                    PartidaDao partidaDao =
                            new PartidaDaoJDBC(connection);

                    PartidaEstrategiaDao estrategiaDao =
                            new PartidaEstrategiaDaoJDBC(connection);

                    PartidaMarcoDao partidaMarcoDao =
                            new PartidaMarcoDaoJDBC(connection);

                    MarcoInfluenciaService marcoService =
                            new MarcoInfluenciaService(connection);

                    // Confere o catálogo.
                    int totalMarcos = marcoDao.listarTodos().size();

                    System.out.println(
                            "Marcos cadastrados: " + totalMarcos
                    );

                    if (totalMarcos != 12) {
                        throw new IllegalStateException(
                                "O catálogo deveria possuir 12 marcos."
                        );
                    }

                    // Cria uma partida temporária com zero
                    // pontos de Influência.
                    Partida partida = new Partida(
                            0L,
                            1L,
                            "Empresa Teste Marcos",
                            StatusPartida.EM_ANDAMENTO,
                            0,
                            0,
                            null
                    );

                    long idPartida = partidaDao.criar(partida);

                    // TESTE 1: País inicial.
                    List<MarcoInfluencia> inicio =
                            marcoService.avaliarEConceder(
                                    idPartida, 0, 1
                            );

                    System.out.println(
                            "Marcos ao iniciar: " + inicio.size()
                    );

                    // TESTE 2: Primeira estratégia.
                    estrategiaDao.adicionar(
                            new PartidaEstrategia(idPartida, 1L)
                    );

                    List<MarcoInfluencia> primeira =
                            marcoService.avaliarEConceder(
                                    idPartida, 1, 1
                            );

                    System.out.println(
                            "Marcos após primeira estratégia: "
                                    + primeira.size()
                    );

                    // TESTE 3: Três estratégias diferentes.
                    estrategiaDao.adicionar(
                            new PartidaEstrategia(idPartida, 2L)
                    );

                    estrategiaDao.adicionar(
                            new PartidaEstrategia(idPartida, 3L)
                    );

                    List<MarcoInfluencia> portfolio =
                            marcoService.avaliarEConceder(
                                    idPartida, 2, 1
                            );

                    System.out.println(
                            "Marcos após três estratégias: "
                                    + portfolio.size()
                    );

                    // TESTE 4: Expansão para dois países.
                    List<MarcoInfluencia> expansao =
                            marcoService.avaliarEConceder(
                                    idPartida, 3, 2
                            );

                    System.out.println(
                            "Marcos após primeira expansão: "
                                    + expansao.size()
                    );

                    // TESTE 5: Repetição da avaliação.
                    List<MarcoInfluencia> repetidos =
                            marcoService.avaliarEConceder(
                                    idPartida, 3, 2
                            );

                    System.out.println(
                            "Marcos recebidos novamente: "
                                    + repetidos.size()
                    );

                    // Recupera o saldo após as recompensas.
                    Partida resultado = partidaDao
                            .buscarPorId(idPartida)
                            .orElseThrow();

                    int historico = partidaMarcoDao
                            .listarPorPartida(idPartida)
                            .size();

                    System.out.println(
                            "Influência final: "
                                    + resultado.getValorInfluencia()
                    );

                    System.out.println(
                            "Marcos concluídos: " + historico
                    );

                    // 5 + 2 + 3 + 2 = 12
                    if (resultado.getValorInfluencia() != 12 ||
                            historico != 4 ||
                            inicio.size() != 1 ||
                            inicio.get(0).getId() != 12L ||
                            primeira.size() != 1 ||
                            portfolio.size() != 1 ||
                            expansao.size() != 1 ||
                            !repetidos.isEmpty()) {

                        throw new IllegalStateException(
                                "Falha na validação dos marcos."
                        );
                    }

                    System.out.println(
                            "Teste concluído com sucesso!"
                    );

                } finally {
                    // Desfaz os dados temporários.
                    connection.rollback();
                }
            }

        } catch (SQLException | RuntimeException e) {
            System.err.println("Erro no teste de marcos:");
            e.printStackTrace();
        }
    }
}
