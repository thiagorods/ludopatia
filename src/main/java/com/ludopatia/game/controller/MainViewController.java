package com.ludopatia.game.controller;

import com.almasb.fxgl.dsl.FXGL;
import com.ludopatia.game.infrastructure.model.entities.Pais;
import com.ludopatia.game.infrastructure.model.entities.Partida;
import com.ludopatia.game.service.PartidaService;
import com.ludopatia.game.service.PaisService;
import com.ludopatia.game.session.SessaoPartida;
import com.ludopatia.game.utils.FaixaAtributo;
import com.ludopatia.game.view.map.GeometriaMapa;
import com.ludopatia.game.view.map.MapaView;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Controller da tela principal (main-view.fxml).
 *
 * Nesta etapa ele faz três coisas: monta o mapa interativo dentro de {@code mapContainer}, carrega os
 * países do banco e preenche o painel "PAÍS EM FOCO" quando um país é selecionado (o painel fica
 * oculto enquanto não há seleção).
 *
 * Os atributos dinâmicos (expostos, jogadores etc.) ainda aparecem como "—": dependem do motor da
 * simulação (PaisEstado), que ainda não existe.
 */
public class MainViewController {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final String CAMINHO_BANDEIRAS = "/com/ludopatia/game/assets/images/main/";
    private static final String BANDEIRA_PADRAO = "default-country.png";
    private static final String SEM_DADO = "—";

    // Medidas dos painéis sobrepostos ao mapa. Devem acompanhar main-view.css (.side-menu e .country-panel)
    // e as margens de 15 px do FXML. Servem para o mapa deslizar um país que ficaria escondido atrás de um painel.
    private static final double MARGEM_PAINEIS = 15;
    private static final double LARGURA_MENU_LATERAL = 243;
    private static final double LARGURA_PAINEL_PAIS = 230;
    private static final double FOLGA_ENTRE_PAINEL_E_MAPA = 16;

    @FXML private Label countryReachedNumber;
    @FXML private Label totalPlayersNumber;
    @FXML private Label totalInfluenceNumber;

    @FXML private Pane mapContainer;
    @FXML private VBox countryPanel;
    @FXML private VBox newsPanel;

    @FXML private HBox sideMenuMapOption;
    @FXML private HBox sideMenuStrategiesOption;
    @FXML private HBox sideMenuResourcesOption;

    @FXML private HBox initialCountryOption;

    @FXML private ImageView countryFlag;
    @FXML private Label countryName;
    @FXML private Label countryStatus;
    @FXML private Label countryDescription;
    @FXML private Label countryPopulation;

    @FXML private Label attrAcessibilidade;
    @FXML private Label attrReceptividade;
    @FXML private Label attrVulnerabilidade;
    @FXML private Label attrResistencia;

    @FXML private Label dynNaoExpostos;
    @FXML private Label dynExpostos;
    @FXML private Label dynJogadores;
    @FXML private Label dynRecorrentes;
    @FXML private Label dynEmRisco;
    @FXML private Label dynRecuperados;
    @FXML private Label dynFalencia;

    private final Map<Integer, Pais> paisesPorId = new HashMap<>();
    private final Map<Integer, GeometriaMapa.FormaPais> formasPorId = new HashMap<>();
    private final Map<String, Image> cacheBandeiras = new HashMap<>();
    private final Tooltip dicaDescricao = new Tooltip();

    private MapaView mapa;

    private final String nomeEmpresa = FXGL.gets("nomeEmpresa");

    @FXML
    private void initialize() {
        dicaDescricao.setWrapText(true);
        dicaDescricao.setMaxWidth(320);
        Tooltip.install(countryDescription, dicaDescricao);

        carregarPaises();
        construirMapa();

        /* Vinculando elementos do cabeçalho com variáveis de estado da partida */
        totalInfluenceNumber.textProperty().bind(FXGL.getip("valorInfluencia").asString());
    }

    // ------------------------------------------------------------------ exibição
    public void aoExibir() {
        if (SessaoPartida.obter().idPaisInicial() == -1) {
            System.out.println("Escolha o país onde " + nomeEmpresa + " deve iniciar!");
        }
    }

    // ------------------------------------------------------------------ montagem

    private void carregarPaises() {
        try {
            for (Pais pais : new PaisService().listarPaises()) {
                paisesPorId.put(pais.getId(), pais);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível carregar os países do banco de dados.", e);
        }
    }

    private void construirMapa() {
        GeometriaMapa geometria = GeometriaMapa.carregar();

        for (GeometriaMapa.FormaPais forma : geometria.paises) {
            if (!paisesPorId.containsKey(forma.paisId)) {
                throw new IllegalStateException("O mapa tem o país " + forma.codigo + " (paisId " + forma.paisId
                        + ") que não existe na tabela `pais`.");
            }
            formasPorId.put(forma.paisId, forma);
        }

        mapa = new MapaView(geometria);
        // o Pane não redimensiona filhos sozinho: o mapa acompanha o tamanho do container
        mapa.prefWidthProperty().bind(mapContainer.widthProperty());
        mapa.prefHeightProperty().bind(mapContainer.heightProperty());
        mapa.setAreaSobreposta(new Insets(
                0,
                LARGURA_PAINEL_PAIS + MARGEM_PAINEIS + FOLGA_ENTRE_PAINEL_E_MAPA,
                0,
                LARGURA_MENU_LATERAL + MARGEM_PAINEIS + FOLGA_ENTRE_PAINEL_E_MAPA));
        mapa.setOnPaisSelecionado(this::aoSelecionarPais);

        mapContainer.getChildren().add(mapa);
    }

    // ------------------------------------------------------------------ seleção de país

    /** Chamado pelo mapa com o id do país clicado, ou {@code null} quando a seleção é limpa. */
    private void aoSelecionarPais(Integer paisId) {
        Pais pais = paisId == null ? null : paisesPorId.get(paisId);
        if (pais == null) {
            definirPainelVisivel((Node) countryPanel,false);
            return;
        }

        preencherPainel(pais, formasPorId.get(paisId));
        definirPainelVisivel((Node) countryPanel, true);
        mapa.garantirVisivel(paisId);

        int idPaisInicial = FXGL.geti("idPaisInicial");

        /* Se um país inicial já tiver sido escolhido, esconde o botão de seleção */
        definirInitialCountryOptionVisivel(idPaisInicial == -1);
    }

    public void definirInitialCountryOptionVisivel(boolean visivel) {
        initialCountryOption.setVisible(visivel);
        initialCountryOption.setManaged(visivel);
    }

    private void definirPainelVisivel(Node no, boolean visivel) {
        no.setVisible(visivel);
        no.setManaged(visivel);       // oculto não ocupa espaço no StackPane
    }

    private void preencherPainel(Pais pais, GeometriaMapa.FormaPais forma) {
        countryName.setText(pais.getNome());
        countryStatus.setText(pais.getCategoria().name().replace("_", " "));
        countryDescription.setText(pais.getDescricao());
        dicaDescricao.setText(pais.getDescricao());
        countryPopulation.setText(NumberFormat.getIntegerInstance(PT_BR).format(pais.getPopulacaoTotal()));

        attrAcessibilidade.setText(formatarAtributo(pais.getAcessibilidade()));
        attrReceptividade.setText(formatarAtributo(pais.getReceptividade()));
        attrVulnerabilidade.setText(formatarAtributo(pais.getVulnerabilidade()));
        attrResistencia.setText(formatarAtributo(pais.getResistencia()));

        limparDadosDinamicos();
        countryFlag.setImage(carregarBandeira(forma == null ? null : forma.bandeira));
        countryFlag.getStyleClass().clear();
        countryFlag.setStyle("country-flag");
    }

    private static String formatarAtributo(int valor) {
        return valor + " · " + FaixaAtributo.rotulo(valor);
    }

    /** Ponto de ligação futuro: quando existir PaisEstado, preencher aqui os valores da partida. */
    private void limparDadosDinamicos() {
        for (Label label : new Label[]{dynNaoExpostos, dynExpostos, dynJogadores, dynRecorrentes,
                dynEmRisco, dynRecuperados, dynFalencia}) {
            label.setText(SEM_DADO);
        }
    }

    private Image carregarBandeira(String arquivo) {
        String nome = (arquivo == null || arquivo.isBlank()) ? BANDEIRA_PADRAO : arquivo;
        return cacheBandeiras.computeIfAbsent(nome, n -> {
            URL url = getClass().getResource(CAMINHO_BANDEIRAS + n);
            if (url == null) {
                url = getClass().getResource(CAMINHO_BANDEIRAS + BANDEIRA_PADRAO);
            }
            return new Image(url.toExternalForm());
        });
    }

    @FXML
    protected void onEscolherPaisInicialButtonClicked() {
        // getPaisSelecionado() retorna null quando nenhum país está selecionado.
        Integer idPaisSelecionado = mapa.getPaisSelecionado();
        if (idPaisSelecionado == null) {
            return;
        }

        Pais paisSelecionado = paisesPorId.get(idPaisSelecionado);
        if (paisSelecionado == null) {
            return;
        }

        String textoConfirmacao =
                "Iniciar em " + paisSelecionado.getNome() + "?";

        FXGL.getDialogService().showConfirmationBox(
                textoConfirmacao,
                confirmou -> {
                    if (!confirmou) {
                        return;
                    }

                    try {
                        // Só altera o estado do FXGL depois que a criação no banco
                        // terminar com sucesso. Assim, uma falha permite tentar de novo.
                        Partida partida = new PartidaService().criarPartida(
                                nomeEmpresa,
                                idPaisSelecionado
                        );

                        SessaoPartida.definirPartidaAtual(partida);

                        FXGL.set("idPaisInicial", partida.getIdPaisInicial());
                        FXGL.set("valorCiclo", partida.getCiclo());
                        FXGL.set("valorInfluencia", partida.getValorInfluencia());

                        mapa.definirAtivo(idPaisSelecionado, true);
                        mapa.definirIntensidade(idPaisSelecionado, 0.1);

                        /* Próxima etapa: iniciar o motor de simulação com a partida. */

                    } catch (SQLException e) {
                        System.err.println(
                                "Não foi possível iniciar a partida. Verifique o banco de dados e tente novamente."
                        );
                        e.printStackTrace();
                    }
                }
        );
    }

    // ------------------------------------------------------------------ navegação entre abas
    @FXML
    protected void onMapaButtonClicked() {
        definirPainelVisivel((Node) countryPanel, false);
        definirPainelVisivel((Node) newsPanel, true);

        sideMenuMapOption.getStyleClass().add("menu-option-active");
        sideMenuStrategiesOption.getStyleClass().setAll("menu-option");
        sideMenuResourcesOption.getStyleClass().setAll("menu-option");
    }

    @FXML
    protected void onEstrategiasButtonClicked() {
        definirPainelVisivel((Node) countryPanel, false);
        definirPainelVisivel((Node) newsPanel, false);

        sideMenuMapOption.getStyleClass().setAll("menu-option");
        sideMenuStrategiesOption.getStyleClass().add("menu-option-active");
        sideMenuResourcesOption.getStyleClass().setAll("menu-option");
    }

    @FXML
    protected void onRecursosButtonClicked() {
        definirPainelVisivel((Node) countryPanel, false);
        definirPainelVisivel((Node) newsPanel, false);

        sideMenuMapOption.getStyleClass().setAll("menu-option");
        sideMenuStrategiesOption.getStyleClass().setAll("menu-option");
        sideMenuResourcesOption.getStyleClass().add("menu-option-active");
    }




}
