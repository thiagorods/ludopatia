package com.ludopatia.game;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.ludopatia.game.controller.MainViewController;
import com.ludopatia.game.infrastructure.database.DatabaseInitializer;
import com.ludopatia.game.menu.LudopatiaSceneFactory;
import com.ludopatia.game.service.PaisService;
import com.ludopatia.game.session.PartidaConfig;
import com.ludopatia.game.session.SessaoPartida;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Region;

import java.awt.*;
import java.sql.SQLException;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;

public class LudopatiaApp extends GameApplication {
    private static int screenWidth;
    private static int screenHeight;
    private static MainViewController controller;

    @Override
    protected void initSettings(GameSettings settings) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = (int) screenSize.getWidth();
        screenHeight = (int) screenSize.getHeight();

        settings.setWidth(screenWidth);
        settings.setHeight(screenHeight);

        settings.setTitle("Ludopatia");

        settings.setFullScreenAllowed(true);
        settings.setFullScreenFromStart(true);

        settings.setMainMenuEnabled(true);
        //settings.setGameMenuEnabled(true);

        settings.setSceneFactory(new LudopatiaSceneFactory());
    }

    @Override
    protected void initUI() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/ludopatia/game/view/fxml/main-view.fxml"
                    )
            );

            Parent root = loader.load();

            controller = loader.getController();

            if (root instanceof Region region) {
                ((Region) root).setPrefWidth(screenWidth);
                ((Region) root).setPrefHeight(screenHeight );
                ((Region) root).setMaxSize(screenWidth, screenHeight);
            }

            getGameScene().addUINode(root);
            controller.aoExibir();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Erro ao carregar o menu principal", e
            );
        }
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        PartidaConfig config = SessaoPartida.obter();
        vars.put("nomeEmpresa", config.nomeCasa());
        vars.put("idPaisInicial", config.idPaisInicial());
        vars.put("valorInfluencia", 0);
        vars.put("valorCiclo", 0);
        vars.put("numJogadores", 0);
    }

    @Override
    protected void initGame() {
        /* Vincula o botão de Escolher País Inicial à variável de estado
        * "idPaisInicial". Quando essa variável é alterada dentro do jogo, o botão
        * é configurado para não aparecer mais.
        * */
        FXGL.getip("idPaisInicial").addListener((obs, antigo, novo) ->
                controller.definirInitialCountryOptionVisivel(false));
    }

    public static void main(String[] args) {
        // Garante tabelas e países cadastrados antes de abrir o jogo (idempotente).
        // A tela principal lê os países do banco para preencher o painel do país.
        try {
            DatabaseInitializer.inicializar();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar o banco de dados", e);
        }

        launch(args);
    }
}