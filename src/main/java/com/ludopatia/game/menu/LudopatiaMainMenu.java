package com.ludopatia.game.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;

import com.ludopatia.game.controller.MainMenuViewController;
import com.ludopatia.game.controller.NameSelectionController;
import com.ludopatia.game.session.PartidaConfig;
import com.ludopatia.game.session.SessaoPartida;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Region;

import java.awt.*;

public class LudopatiaMainMenu extends FXGLMenu implements MainMenuActions, NameSelectionMenuActions {

    private static int screenWidth;
    private static int screenHeight;

    public LudopatiaMainMenu() {
        super(MenuType.MAIN_MENU);

        mostrarMenuInicial();
    }

    private void mostrarMenuInicial() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/ludopatia/game/view/fxml/main-menu-view.fxml"
                    )
            );

            Parent root = loader.load();

            MainMenuViewController controller = loader.getController();
            controller.setActions(this);

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

    private void mostrarMenuSelecaoNome() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/ludopatia/game/view/fxml/name-selection-view.fxml"
                    )
            );

            Parent root = loader.load();

            NameSelectionController controller = loader.getController();
            controller.setActions(this);

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
                    "Erro ao carregar o menu de seleção de nome",
                    e
            );
        }
    }

    /* Métodos Menu Principal */
    @Override
    public void onStartGame() {
        mostrarMenuSelecaoNome();
    }

    @Override
    public void onOpenSettings() {
        //Implementar configurações no futuro
    }

    @Override
    public void onExitGame() {
        fireExit();
    }

    /* Métodos Menu Seleção de Nome */
    @Override
    public void onAdvanceGame(String nomeEmpresa) {
        SessaoPartida.definir(new PartidaConfig(nomeEmpresa, -1));
        fireNewGame();
        mostrarMenuInicial(); //coloca o menu inicial na frente para caso o jogador retorne
    }

    @Override
    public void onCancelGame() {
        mostrarMenuInicial();
    }
}
