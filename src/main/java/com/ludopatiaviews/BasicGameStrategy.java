package com.ludopatiaviews;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.getGameScene;

/**
 * Protótipo de interface para testar FXGL com o conceito do Ludopatia.
 * Os números e efeitos são fictícios e servem somente para testar a interface.
 */
public class BasicGameStrategy extends GameApplication {

    private static final int WINDOW_WIDTH = 1260;
    private static final int WINDOW_HEIGHT = 800;

    private final Map<String, Strategy> strategies = new LinkedHashMap<>();

    private int influence = 20;
    private long capital = 5_000_000;
    private int socialAlert = 42;
    private int reactionValue = 18;
    private int cycle = 1;
    private int exposureBonus = 0;
    private int conversionBonus = 0;

    private Strategy selectedStrategy;

    private Label influenceValue;
    private Label capitalValue;
    private Label alertValue;
    private Label reactionValueLabel;
    private Label cycleValue;
    private Label bonusLabel;

    private Label detailName;
    private Label detailCategory;
    private Label detailDescription;
    private Label detailEffects;
    private Label detailPrerequisites;
    private Label detailCost;
    private Label detailStatus;
    private Button activateButton;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(WINDOW_WIDTH);
        settings.setHeight(WINDOW_HEIGHT);
        settings.setTitle("Ludopatia — Protótipo FXGL");
        settings.setVersion("0.1");
    }

    @Override
    protected void initUI() {
        createStrategies();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18));
        root.setStyle("-fx-background-color: #071019;");

        root.setTop(createHeader());
        root.setCenter(createWorkspace());

        getGameScene().addUINode(root);
        selectStrategy(strategies.get("presenca"));
    }

    private void createStrategies() {
        strategies.clear();

        addStrategy(new Strategy(
                "presenca", "Presença Digital",
                "Estabelece a presença inicial da marca nos canais digitais.",
                0, 0, List.of(), 0, 0, 265, 10, true));

        addStrategy(new Strategy(
                "influenciadores", "Influenciadores",
                "Representa uma parceria de divulgação que altera as probabilidades da simulação.",
                3, 500_000, List.of("presenca"), 8, 0, 75, 85, false));

        addStrategy(new Strategy(
                "direcionada", "Publicidade Direcionada",
                "Representa campanhas direcionadas a públicos definidos dentro do modelo do jogo.",
                3, 400_000, List.of("presenca"), 4, 3, 445, 85, false));

        addStrategy(new Strategy(
                "afiliados", "Marketing de Afiliados",
                "Representa uma rede de parceiros de divulgação dentro da simulação.",
                4, 750_000, List.of("influenciadores"), 6, 2, 75, 160, false));

        addStrategy(new Strategy(
                "aquisicao", "Campanhas de Aquisição",
                "Representa uma campanha cujo efeito de teste incide sobre a probabilidade de conversão.",
                4, 850_000, List.of("direcionada"), 0, 7, 445, 160, false));

        addStrategy(new Strategy(
                "midia", "Parcerias de Mídia",
                "Representa parcerias com canais de comunicação no modelo de expansão.",
                7, 1_200_000, List.of("afiliados"), 6, 0, 75, 235, false));

        addStrategy(new Strategy(
                "eventos", "Patrocínio de Eventos",
                "Representa a associação da marca a eventos dentro da simulação.",
                5, 1_000_000, List.of("aquisicao"), 8, 0, 445, 235, false));

        addStrategy(new Strategy(
                "multicanal", "Rede Multicanal",
                "Reúne os dois caminhos da árvore e aumenta os bônus simulados.",
                8, 1_800_000, List.of("midia", "eventos"), 10, 4, 265, 310, false));

        addStrategy(new Strategy(
                "internacional", "Expansão Internacional",
                "Representa a expansão do alcance da empresa para outros países do mapa.",
                8, 2_000_000, List.of("multicanal"), 6, 5, 265, 385, false));

        addStrategy(new Strategy(
                "marca", "Marca Consolidada",
                "Representa o último nível desta árvore de habilidades do protótipo.",
                10, 2_500_000, List.of("internacional"), 10, 8, 265, 460, false));
    }

    private void addStrategy(Strategy strategy) {
        strategies.put(strategy.id, strategy);
    }

    private VBox createHeader() {
        VBox header = new VBox(10);

        Label title = makeLabel("LUDOPATIA", 26, "#F2F5F8");
        Label subtitle = makeLabel("PROTÓTIPO DE ÁRVORE DE HABILIDADES", 11, "#8F9EAA");
        VBox brand = new VBox(2, title, subtitle);
        brand.setMinWidth(255);

        influenceValue = makeLabel("20", 20, "#FFC247");
        capitalValue = makeLabel("R$ 5.000.000", 18, "#79D875");
        alertValue = makeLabel("42%", 18, "#FF5C61");
        reactionValueLabel = makeLabel("18", 18, "#E8EDF3");
        cycleValue = makeLabel("Ciclo 1", 14, "#CDD6DF");

        HBox stats = new HBox(18,
                statBox("INFLUÊNCIA", influenceValue),
                statBox("CAPITAL", capitalValue),
                statBox("ALERTA SOCIAL", alertValue),
                statBox("VALOR DE REAÇÃO", reactionValueLabel),
                statBox("CICLO", cycleValue));
        stats.setAlignment(Pos.CENTER_LEFT);

        Button nextCycleButton = makeButton("AVANÇAR CICLO", "#193746", "#74D7F0");
        nextCycleButton.setOnAction(event -> advanceCycle());

        HBox topLine = new HBox(18, brand, stats, nextCycleButton);
        topLine.setAlignment(Pos.CENTER_LEFT);

        bonusLabel = makeLabel("Bônus das estratégias: Exposição +0 p.p.  |  Conversão +0 p.p.",
                12, "#91A4B4");
        header.getChildren().addAll(topLine, bonusLabel);
        header.setPadding(new Insets(0, 0, 16, 0));
        return header;
    }

    private VBox statBox(String caption, Label value) {
        Label label = makeLabel(caption, 10, "#8F9EAA");
        VBox box = new VBox(3, label, value);
        box.setPadding(new Insets(8, 12, 8, 12));
        box.setStyle("-fx-background-color: #0C1822; -fx-border-color: #213440; -fx-border-radius: 6; -fx-background-radius: 6;");
        return box;
    }

    private HBox createWorkspace() {
        Label treeTitle = makeLabel("ÁRVORE DE PUBLICIDADE", 15, "#FF5D5D");
        Label treeHint = makeLabel("Clique em uma habilidade para ver os detalhes e os pré-requisitos.",
                11, "#91A4B4");
        VBox treeHeading = new VBox(4, treeTitle, treeHint);

        Pane treePane = createTreePane();
        VBox treeColumn = new VBox(10, treeHeading, treePane);
        treeColumn.setMinWidth(710);

        VBox details = createDetailsPanel();
        HBox workspace = new HBox(18, treeColumn, details);
        workspace.setAlignment(Pos.TOP_LEFT);
        return workspace;
    }

    private Pane createTreePane() {
        Pane pane = new Pane();
        pane.setPrefSize(710, 540);
        pane.setMinSize(710, 540);
        pane.setStyle("-fx-background-color: #09141E; -fx-border-color: #213440; -fx-border-radius: 8; -fx-background-radius: 8;");

        // As linhas são adicionadas primeiro para ficarem atrás dos nós.
        addConnection(pane, 360, 64, 165, 85);
        addConnection(pane, 360, 64, 535, 85);
        addConnection(pane, 165, 139, 165, 160);
        addConnection(pane, 535, 139, 535, 160);
        addConnection(pane, 165, 214, 165, 235);
        addConnection(pane, 535, 214, 535, 235);
        addConnection(pane, 165, 289, 360, 310);
        addConnection(pane, 535, 289, 360, 310);
        addConnection(pane, 360, 364, 360, 385);
        addConnection(pane, 360, 439, 360, 460);

        for (Strategy strategy : strategies.values()) {
            Button node = makeNodeButton(strategy);
            strategy.nodeButton = node;
            pane.getChildren().add(node);
        }

        refreshTreeNodes();
        return pane;
    }

    private void addConnection(Pane pane, double startX, double startY, double endX, double endY) {
        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(Color.web("#63323A"));
        line.setStrokeWidth(3);
        pane.getChildren().add(line);
    }

    private Button makeNodeButton(Strategy strategy) {
        Button button = new Button(strategy.name);
        button.setWrapText(true);
        button.setPrefSize(190, 54);
        button.setLayoutX(strategy.x);
        button.setLayoutY(strategy.y);
        button.setFocusTraversable(false);
        button.setOnAction(event -> selectStrategy(strategy));
        return button;
    }

    private VBox createDetailsPanel() {
        detailName = makeLabel("Selecione uma habilidade", 21, "#F2F5F8");
        detailCategory = makeLabel("PUBLICIDADE", 12, "#FF5D5D");
        detailDescription = makeLabel("", 13, "#C7D1DA");
        detailDescription.setWrapText(true);

        Label effectsCaption = makeLabel("EFEITOS DE TESTE", 11, "#91A4B4");
        detailEffects = makeLabel("", 13, "#7EE080");
        detailEffects.setWrapText(true);

        Label prereqCaption = makeLabel("PRÉ-REQUISITOS", 11, "#91A4B4");
        detailPrerequisites = makeLabel("", 13, "#C7D1DA");
        detailPrerequisites.setWrapText(true);

        Label costCaption = makeLabel("CUSTO", 11, "#91A4B4");
        detailCost = makeLabel("", 13, "#FFC247");
        detailCost.setWrapText(true);

        activateButton = makeButton("DESBLOQUEAR HABILIDADE", "#8F1F2A", "#FFFFFF");
        activateButton.setMaxWidth(Double.MAX_VALUE);
        activateButton.setOnAction(event -> activateSelectedStrategy());

        detailStatus = makeLabel("Os valores deste protótipo são fictícios.", 11, "#91A4B4");
        detailStatus.setWrapText(true);

        VBox panel = new VBox(13,
                detailName, detailCategory, detailDescription,
                separator(), effectsCaption, detailEffects,
                separator(), prereqCaption, detailPrerequisites,
                separator(), costCaption, detailCost,
                activateButton, detailStatus);
        panel.setPadding(new Insets(18));
        panel.setPrefSize(430, 540);
        panel.setMinWidth(430);
        panel.setStyle("-fx-background-color: #0B1721; -fx-border-color: #263B48; -fx-border-radius: 8; -fx-background-radius: 8;");
        return panel;
    }

    private javafx.scene.layout.Region separator() {
        javafx.scene.layout.Region line = new javafx.scene.layout.Region();
        line.setPrefHeight(1);
        line.setMaxHeight(1);
        line.setStyle("-fx-background-color: #243641;");
        return line;
    }

    private void selectStrategy(Strategy strategy) {
        selectedStrategy = strategy;
        detailName.setText(strategy.name);
        detailCategory.setText("PUBLICIDADE");
        detailDescription.setText(strategy.description);
        detailEffects.setText(formatEffects(strategy));
        detailPrerequisites.setText(formatPrerequisites(strategy));
        detailCost.setText(strategy.influenceCost + " Influência\n" + formatMoney(strategy.capitalCost));

        if (strategy.unlocked) {
            detailStatus.setText("Esta habilidade já está desbloqueada.");
        } else if (!prerequisitesMet(strategy)) {
            detailStatus.setText("Desbloqueie os pré-requisitos antes de comprar esta habilidade.");
        } else if (influence < strategy.influenceCost || capital < strategy.capitalCost) {
            detailStatus.setText("Você não possui recursos suficientes para esta habilidade.");
        } else {
            detailStatus.setText("Protótipo: desbloqueie para aplicar os bônus de teste.");
        }

        activateButton.setDisable(strategy.unlocked
                || !prerequisitesMet(strategy)
                || influence < strategy.influenceCost
                || capital < strategy.capitalCost);
        activateButton.setText(strategy.unlocked ? "JÁ DESBLOQUEADA" : "DESBLOQUEAR HABILIDADE");
        refreshTreeNodes();
    }

    private String formatEffects(Strategy strategy) {
        List<String> effects = new ArrayList<>();
        if (strategy.exposureBonus != 0) {
            effects.add("Exposição " + signed(strategy.exposureBonus) + " p.p.");
        }
        if (strategy.conversionBonus != 0) {
            effects.add("Conversão " + signed(strategy.conversionBonus) + " p.p.");
        }
        if (effects.isEmpty()) {
            return "Habilidade inicial; sem bônus adicional neste protótipo.";
        }
        return String.join("\n", effects);
    }

    private String formatPrerequisites(Strategy strategy) {
        if (strategy.prerequisites.isEmpty()) {
            return "Nenhum — habilidade inicial.";
        }
        List<String> names = new ArrayList<>();
        for (String prerequisiteId : strategy.prerequisites) {
            names.add(strategies.get(prerequisiteId).name);
        }
        return String.join("\nE\n", names);
    }

    private boolean prerequisitesMet(Strategy strategy) {
        for (String prerequisiteId : strategy.prerequisites) {
            Strategy prerequisite = strategies.get(prerequisiteId);
            if (prerequisite == null || !prerequisite.unlocked) {
                return false;
            }
        }
        return true;
    }

    private void activateSelectedStrategy() {
        if (selectedStrategy == null || selectedStrategy.unlocked) {
            return;
        }

        if (!prerequisitesMet(selectedStrategy)) {
            detailStatus.setText("Pré-requisitos não atendidos.");
            return;
        }
        if (influence < selectedStrategy.influenceCost || capital < selectedStrategy.capitalCost) {
            detailStatus.setText("Recursos insuficientes.");
            return;
        }

        influence -= selectedStrategy.influenceCost;
        capital -= selectedStrategy.capitalCost;
        exposureBonus += selectedStrategy.exposureBonus;
        conversionBonus += selectedStrategy.conversionBonus;
        selectedStrategy.unlocked = true;

        updateHeaderValues();
        selectStrategy(selectedStrategy);
        detailStatus.setText("Habilidade desbloqueada! Bônus aplicados ao protótipo.");
    }

    private void advanceCycle() {
        cycle++;
        socialAlert = Math.min(100, socialAlert + 4);
        reactionValue = Math.min(100, reactionValue + 3);
        capital += 150_000;
        updateHeaderValues();
        cycleValue.setText("Ciclo " + cycle);
        detailStatus.setText("Ciclo avançado. Alerta social e Valor de Reação foram atualizados.");
    }

    private void updateHeaderValues() {
        influenceValue.setText(String.valueOf(influence));
        capitalValue.setText(formatMoney(capital));
        alertValue.setText(socialAlert + "%");
        reactionValueLabel.setText(String.valueOf(reactionValue));
        bonusLabel.setText("Bônus das estratégias: Exposição " + signed(exposureBonus)
                + " p.p.  |  Conversão " + signed(conversionBonus) + " p.p.");
    }

    private void refreshTreeNodes() {
        for (Strategy strategy : strategies.values()) {
            if (strategy.nodeButton == null) {
                continue;
            }
            String background = strategy.unlocked ? "#173A32" : "#20151D";
            String border = strategy == selectedStrategy ? "#F7C84A" : (strategy.unlocked ? "#65C99A" : "#B63843");
            String text = strategy.unlocked ? "#E5FFF1" : "#F0DDE0";
            strategy.nodeButton.setStyle(
                    "-fx-background-color: " + background + ";"
                            + "-fx-border-color: " + border + ";"
                            + "-fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8;"
                            + "-fx-text-fill: " + text + "; -fx-font-size: 12px; -fx-font-weight: bold;"
                            + "-fx-padding: 5 8 5 8; -fx-cursor: hand;");
        }
    }

    private Label makeLabel(String text, int size, String color) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: " + size + "px; -fx-text-fill: " + color + ";");
        return label;
    }

    private Button makeButton(String text, String background, String foreground) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + background + ";"
                + "-fx-text-fill: " + foreground + "; -fx-font-weight: bold;"
                + "-fx-font-size: 12px; -fx-padding: 10 14 10 14;"
                + "-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #3B5968;");
        button.setFocusTraversable(false);
        return button;
    }

    private String formatMoney(long value) {
        return String.format("R$ %,d", value).replace(',', '.');
    }

    private String signed(int value) {
        return (value >= 0 ? "+" : "") + value;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static final class Strategy {
        private final String id;
        private final String name;
        private final String description;
        private final int influenceCost;
        private final long capitalCost;
        private final List<String> prerequisites;
        private final int exposureBonus;
        private final int conversionBonus;
        private final double x;
        private final double y;
        private boolean unlocked;
        private Button nodeButton;

        private Strategy(String id, String name, String description,
                         int influenceCost, long capitalCost,
                         List<String> prerequisites,
                         int exposureBonus, int conversionBonus,
                         double x, double y, boolean unlocked) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.influenceCost = influenceCost;
            this.capitalCost = capitalCost;
            this.prerequisites = prerequisites;
            this.exposureBonus = exposureBonus;
            this.conversionBonus = conversionBonus;
            this.x = x;
            this.y = y;
            this.unlocked = unlocked;
        }
    }
}
