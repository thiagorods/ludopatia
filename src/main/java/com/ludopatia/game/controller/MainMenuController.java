package com.ludopatia.game.controller;

import com.almasb.fxgl.app.GameApplication;
import com.ludopatia.game.LudopatiaApp;
import javafx.fxml.FXML;

import java.util.function.Consumer;

import static com.almasb.fxgl.dsl.FXGL.getDialogService;
import static com.almasb.fxgl.dsl.FXGL.getGameController;


public class MainMenuController {
    @FXML
    protected void onIniciarButtonClicked() {
    }

    @FXML
    protected void onSairButtonClicked() {
        getDialogService().showConfirmationBox(
                "Tem certeza que deseja sair?",
                confirmou -> {
                    if (confirmou) {
                        getGameController().exit();
                    }
                }
        );
    }
}
