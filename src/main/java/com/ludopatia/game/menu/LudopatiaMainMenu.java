package com.ludopatia.game.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;

import com.ludopatia.game.controller.MainMenuActions;
import com.ludopatia.game.controller.MainMenuViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Region;

import java.awt.*;

public class LudopatiaMainMenu extends FXGLMenu {

    private static int screenWidth;
    private static int screenHeight;

    public LudopatiaMainMenu() {
        super(MenuType.MAIN_MENU);

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/ludopatia/game/view/fxml/main-menu-view.fxml"
                    )
            );

            Parent root = loader.load();

            MainMenuViewController controller = loader.getController();

            controller.setActions(new MainMenuActions() {

                @Override
                public void onStartGame() {
                    fireNewGame();
                }

                @Override
                public void onOpenSettings() {
                    //Settings
                }

                @Override
                public void onExitGame() {
                    fireExit();
                }
            });

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            screenWidth = (int) screenSize.getWidth();
            screenHeight = (int) screenSize.getHeight();

            if (root instanceof Region) {
                ((Region) root).setPrefWidth(screenWidth);
                ((Region) root).setPrefHeight(screenHeight );
                ((Region) root).setMaxSize(screenWidth, screenHeight);
            }

            // Substitui o conteúdo visual do menu
            getContentRoot().getChildren().setAll(root);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Erro ao carregar o menu principal",
                    e
            );
        }


    }
}
