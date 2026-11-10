package com.ludopatia.game.infrastructure.database;

import com.ludopatia.game.model.entities.Estrategia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public final class EstrategiaSeeder {

    private EstrategiaSeeder() {
    }

    public static void popular(Connection connection) throws SQLException {

        String sql = """
                INSERT INTO estrategia (
                    id_estrategia, id_tipo, titulo, descricao, icone,
                    custo_influencia, custo_capital,
                    valor_exposicao, valor_conversao, valor_retencao,
                    valor_agravamento, valor_recuperacao,
                    valor_conscientizacao, valor_expansao
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(id_estrategia) DO UPDATE SET
                    id_tipo = excluded.id_tipo,
                    titulo = excluded.titulo,
                    descricao = excluded.descricao,
                    icone = excluded.icone,
                    custo_influencia = excluded.custo_influencia,
                    custo_capital = excluded.custo_capital,
                    valor_exposicao = excluded.valor_exposicao,
                    valor_conversao = excluded.valor_conversao,
                    valor_retencao = excluded.valor_retencao,
                    valor_agravamento = excluded.valor_agravamento,
                    valor_recuperacao = excluded.valor_recuperacao,
                    valor_conscientizacao = excluded.valor_conscientizacao,
                    valor_expansao = excluded.valor_expansao
                """;

        // Ordem dos efeitos em nova():
        // Exposição, Conversão, Retenção, Agravamento,
        // Recuperação e Conscientização.
        //
        // Os números estão em escala decimal:
        // +3 pontos percentuais = 0.03

        List<Estrategia> estrategias = List.of(

                // PUBLICIDADE - Tipo 1

                nova(1, 1, "Presença Digital",
                        "Estabelece a presença da casa de apostas nos canais digitais para ampliar seu reconhecimento.",
                        1, 0.03, 0, 0, 0, 0, 0),

                nova(2, 1, "Influenciadores",
                        "Utiliza parcerias com influenciadores e criadores de conteúdo para ampliar a divulgação da marca.",
                        1, 0.03, 0, 0, 0, 0, 0),

                nova(3, 1, "Publicidade Direcionada",
                        "Direciona campanhas a públicos específicos para aumentar o alcance da divulgação.",
                        2, 0, 0.03, 0, 0, 0, 0),

                nova(4, 1, "Marketing de Afiliados",
                        "Estabelece parcerias com afiliados que promovem a plataforma e atraem novos usuários.",
                        2, 0.02, 0.01, 0, 0, 0, 0),

                nova(5, 1, "Campanhas de Aquisição",
                        "Desenvolve campanhas voltadas à atração de novos usuários para a plataforma.",
                        2, 0, 0.03, 0, 0, 0, 0),

                nova(6, 1, "Parcerias de Mídia",
                        "Estabelece parcerias com canais de comunicação para ampliar a visibilidade da marca.",
                        2, 0.03, 0, 0, 0, 0, 0),

                nova(7, 1, "Patrocínio de Eventos",
                        "Associa a marca a eventos para aumentar sua exposição e reconhecimento público.",
                        3, 0.02, 0.01, 0, 0, 0, 0),

                nova(8, 1, "Rede Multicanal",
                        "Integra diferentes canais de divulgação para ampliar o alcance da casa de apostas.",
                        3, 0.03, 0.01, 0, 0, 0, 0),

                nova(9, 1, "Expansão Internacional",
                        "Amplia a atuação da empresa para novos países e mercados.",
                        3, 0.02, 0.02, 0, 0, 0, 0),

                nova(10, 1, "Marca Consolidada",
                        "Fortalece o reconhecimento da marca e sua posição nos mercados alcançados.",
                        4, 0.02, 0, 0.02, 0, 0, 0),


                // PLATAFORMA - Tipo 2

                nova(11, 2, "Bônus de Boas-vindas",
                        "Oferece benefícios iniciais para incentivar novos usuários a utilizar a plataforma.",
                        1, 0, 0.03, 0, 0, 0, 0),

                nova(12, 2, "Apostas Promocionais",
                        "Disponibiliza promoções específicas relacionadas às atividades da plataforma.",
                        1, 0, 0.02, 0, 0, 0, 0),

                nova(13, 2, "Recompensas por Atividade",
                        "Concede benefícios associados à participação dos usuários na plataforma.",
                        2, 0, 0, 0.03, 0, 0, 0),

                nova(14, 2, "Recompensas Diárias",
                        "Disponibiliza recompensas periódicas para incentivar o retorno dos usuários.",
                        2, 0, 0, 0.03, 0, 0, 0),

                nova(15, 2, "Programa de Indicação",
                        "Incentiva a indicação de novos usuários por meio de benefícios específicos.",
                        2, 0.02, 0.01, 0, 0, 0, 0),

                nova(16, 2, "Programa de Fidelidade",
                        "Oferece benefícios progressivos aos usuários conforme sua participação ao longo do tempo.",
                        2, 0, 0, 0.03, 0, 0, 0),

                nova(17, 2, "Ofertas Personalizadas",
                        "Adapta as ofertas apresentadas de acordo com o perfil e as preferências dos usuários.",
                        3, 0, 0.03, 0, 0, 0, 0),

                nova(18, 2, "Eventos Temporários",
                        "Promove atividades por tempo limitado, com condições e recompensas específicas.",
                        3, 0.01, 0, 0.02, 0, 0, 0),

                nova(19, 2, "Programa VIP",
                        "Oferece benefícios exclusivos aos usuários que alcançam determinada categoria na plataforma.",
                        3, 0, 0.01, 0.03, 0, 0, 0),

                nova(20, 2, "Plataforma Integrada",
                        "Integra funcionalidades e serviços para proporcionar uma experiência mais completa na plataforma.",
                        4, 0, 0.02, 0.03, 0, 0, 0),


                // RESISTÊNCIA - Tipo 3

                nova(21, 3, "Adequação Regulatória",
                        "Adapta as operações da empresa às exigências legais e regulatórias aplicáveis.",
                        1, 0, 0, 0, -0.02, 0, 0),

                nova(22, 3, "Verificação de Identidade",
                        "Confirma a identidade dos usuários para reforçar os controles de acesso à plataforma.",
                        1, 0, 0, 0, -0.02, 0, 0),

                nova(23, 3, "Monitoramento de Risco",
                        "Identifica situações e indicadores que podem representar riscos para a empresa ou para a população.",
                        2, 0, 0, 0, -0.03, 0, 0.01),

                nova(24, 3, "Políticas de Jogo Responsável",
                        "Implementa medidas de prevenção para reduzir os riscos associados às apostas.",
                        2, 0, 0, 0, -0.04, 0.03, 0.02),

                nova(25, 3, "Gestão de Crises",
                        "Coordena respostas a acontecimentos críticos que possam comprometer a operação da empresa.",
                        2, 0, 0, 0, -0.01, 0, -0.02),

                nova(26, 3, "Comunicação Institucional",
                        "Organiza a comunicação oficial da empresa com o público e as instituições.",
                        2, 0, 0, 0, 0, 0, 0.02),

                nova(27, 3, "Assessoria Jurídica",
                        "Oferece suporte jurídico para orientar decisões e lidar com questões legais.",
                        3, 0, 0, 0, -0.02, 0, 0),

                nova(28, 3, "Auditoria Interna",
                        "Avalia os processos internos para identificar falhas e verificar o cumprimento das normas.",
                        3, 0, 0, 0, -0.02, 0.01, 0),

                nova(29, 3, "Gestão de Reputação",
                        "Acompanha e administra a imagem pública da empresa diante de diferentes acontecimentos.",
                        3, 0, 0, 0, 0, 0, -0.02),

                nova(30, 3, "Governança Institucional",
                        "Estabelece diretrizes de gestão, responsabilidades e processos de tomada de decisão.",
                        4, 0, 0, 0, -0.03, 0.02, 0)
        );

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            for (Estrategia estrategia : estrategias) {

                statement.setLong(1, estrategia.getId());
                statement.setLong(2, estrategia.getIdTipo());
                statement.setString(3, estrategia.getTitulo());
                statement.setString(4, estrategia.getDescricao());

                if (estrategia.getIcone() == null) {
                    statement.setNull(5, Types.VARCHAR);
                } else {
                    statement.setString(5, estrategia.getIcone());
                }

                statement.setInt(6, estrategia.getCustoInfluencia());

                if (estrategia.getCustoCapital() == null) {
                    statement.setNull(7, Types.INTEGER);
                } else {
                    statement.setInt(7, estrategia.getCustoCapital());
                }

                statement.setDouble(8, estrategia.getValorExposicao());
                statement.setDouble(9, estrategia.getValorConversao());
                statement.setDouble(10, estrategia.getValorRetencao());
                statement.setDouble(11, estrategia.getValorAgravamento());
                statement.setDouble(12, estrategia.getValorRecuperacao());
                statement.setDouble(13, estrategia.getValorConscientizacao());
                statement.setDouble(14, estrategia.getValorExpansao());

                statement.addBatch();
            }

            statement.executeBatch();
        }
    }

    private static Estrategia nova(
            long id,
            long idTipo,
            String titulo,
            String descricao,
            int custo,
            double exposicao,
            double conversao,
            double retencao,
            double agravamento,
            double recuperacao,
            double conscientizacao) {

        return new Estrategia(
                id,
                idTipo,
                titulo,
                descricao,
                null,     // Ícone ainda não definido
                custo,
                null,     // Capital fora do MVP
                exposicao,
                conversao,
                retencao,
                agravamento,
                recuperacao,
                conscientizacao,
                0.0       // Efeito de expansão ainda não definido
        );
    }
}
