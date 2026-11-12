package com.ludopatia.game.controller;

import com.almasb.fxgl.app.FXGLApplication;
import com.almasb.fxgl.app.GameController;
import com.almasb.fxgl.dsl.FXGL;
import com.ludopatia.game.menu.NameSelectionMenuActions;
import com.ludopatia.game.session.PartidaConfig;
import com.ludopatia.game.utils.validation.ResultadoValidacaoNome;
import com.ludopatia.game.utils.validation.ValidadorNomeCasa;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class NameSelectionController {
    @FXML
    private TextField campoNomeCasa;

    @FXML
    private Label labelStatus;

    private NameSelectionMenuActions actions;

    public void setActions(NameSelectionMenuActions actions) {
        this.actions = actions;
    }

    @FXML
    protected void onCancelarButtonClicked() {
        actions.onCancelGame();
    }

    @FXML
    protected void onIniciarButtonClicked() {
        String nomeEmpresa = campoNomeCasa.getText();

        ResultadoValidacaoNome resultado = ValidadorNomeCasa.validar(nomeEmpresa);

        labelStatus.getStyleClass().removeAll("sucesso", "erro");

        switch (resultado) {

            case VALIDO:
                labelStatus.setText("Nome da casa de apostas válido!");
                labelStatus.getStyleClass().add("sucesso");

                /* repassa o nome da empresa para ser configurado no estado da partida
                * dentro de "initGameVars" na classe principal
                *  */
                actions.onAdvanceGame(nomeEmpresa);
                break;

            case NOME_VAZIO:
                exibirErro("O nome não pode estar vazio.");
                break;

            case NOME_CURTO:
                exibirErro("O nome deve possuir pelo menos 3 caracteres.");
                break;

            case NOME_LONGO:
                exibirErro("O nome deve possuir no máximo 20 caracteres.");
                break;

            case CARACTERES_INVALIDOS:
                exibirErro("O nome contém caracteres inválidos.");
                break;
        }
    }

    private void exibirErro(String mensagem) {
        labelStatus.setText(mensagem);
        labelStatus.getStyleClass().add("erro");
    }
}
