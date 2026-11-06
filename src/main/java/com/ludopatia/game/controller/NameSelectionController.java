package com.ludopatia.game.controller;

import com.ludopatia.game.utils.validation.ResultadoValidacaoNome;
import com.ludopatia.game.utils.validation.ValidadorNomeCasa;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameController;

public class NameSelectionController {
    @FXML
    private TextField campoNomeCasa;

    @FXML
    private Label labelStatus;

    @FXML
    protected void onCancelarButtonClicked() {
        getGameController().gotoMainMenu();
    }

    @FXML
    protected void onIniciarButtonClicked() {
        String nomeCasa = campoNomeCasa.getText();

        ResultadoValidacaoNome resultado = ValidadorNomeCasa.validar(nomeCasa);

        labelStatus.getStyleClass().removeAll("sucesso", "erro");

        switch (resultado) {

            case VALIDO:
                labelStatus.setText("Nome da casa de apostas válido!");
                labelStatus.getStyleClass().add("sucesso");

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
