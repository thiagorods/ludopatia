package com.ludopatia.game.controller;

import javafx.fxml.FXML;


public class MainMenuViewController {

    private MainMenuActions actions;

    public void setActions(MainMenuActions actions) {
        this.actions = actions;
    }

    @FXML
    protected void onIniciarButtonClicked() {
        actions.onStartGame();
    }

    @FXML
    protected void onConfigButtonClicked() {
        actions.onOpenSettings();
    }

    @FXML
    protected void onSairButtonClicked() {
        actions.onExitGame();
    }
    }
