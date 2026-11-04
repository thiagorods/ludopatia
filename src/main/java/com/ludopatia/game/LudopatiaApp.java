package com.ludopatia.game;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Region;

import java.awt.*;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameScene;

public class LudopatiaApp extends GameApplication {
    private static int screenWidth;
    private static int screenHeight;

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
    }

    @Override
    protected void initUI() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/ludopatia/game/view/fxml/main-menu-view.fxml"
                    )
            );

            Parent root = loader.load();

            if (root instanceof Region region) {
                ((Region) root).setPrefWidth(screenWidth);
                ((Region) root).setPrefHeight(screenHeight );
                ((Region) root).setMaxSize(screenWidth, screenHeight);
            }

            getGameScene().addUINode(root);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Erro ao carregar o menu principal", e
            );
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}