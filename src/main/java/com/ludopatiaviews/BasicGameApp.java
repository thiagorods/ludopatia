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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.getGameScene;

/**
 * Ludopatia - protótipo da tela de escolha do país inicial.
 *
 * Este protótipo demonstra apenas a interface de seleção. As posições e
 * conexões do mapa são ilustrativas e devem ser ajustadas ao mapa definitivo.
 */
public class BasicGameApp extends GameApplication {

    private static final int WINDOW_WIDTH = 1360;
    private static final int WINDOW_HEIGHT = 820;
    private static final double MAP_WIDTH = 890;
    private static final double MAP_HEIGHT = 570;
    private static final double NODE_WIDTH = 132;
    private static final double NODE_HEIGHT = 44;

    private final Map<String, Country> countries = new LinkedHashMap<>();
    private final List<Route> routes = new ArrayList<>();
    private final Map<String, Button> countryButtons = new LinkedHashMap<>();
    private final List<RouteLine> routeLines = new ArrayList<>();

    private Country selectedCountry;
    private boolean countryConfirmed;
    private Label selectedCountryName;
    private Label selectedCountryDescription;
    private Label selectedCountryConnections;
    private Label statusLabel;
    private Button confirmButton;
    private Pane mapPane;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(WINDOW_WIDTH);
        settings.setHeight(WINDOW_HEIGHT);
        settings.setTitle("LUDOPATIA | Escolha do país inicial");
        settings.setVersion("0.1 - Protótipo");
    }

    @Override
    protected void initUI() {
        createCountries();
        createRoutes();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #050B10;");
        root.setTop(createHeader());
        root.setCenter(createMainContent());
        root.setBottom(createFooter());

        getGameScene().addUINode(root);
        refreshCountryButtons();
        updateDetails();
    }

    private void createCountries() {
        // Coordenadas relativas ao painel de mapa, não coordenadas geográficas reais.
        addCountry("Estados Unidos", 150, 165,
                "Mercado global consolidado, com importantes conexões. Possui grande potencial de crescimento, mas dificuldade considerável.");
        addCountry("Peru", 245, 290,
                "Mercado regional acessível, com boas condições para crescimento interno e conexões estratégicas, mas com influência internacional limitada.");
        addCountry("Brasil", 345, 365,
                "Mercado muito receptivo e favorável à expansão global, mas com forte regulamentação institucional.");
        addCountry("Reino Unido", 420, 145,
                "Mercado de referência global. Difícil de conquistar, mas com grande potencial de influência sobre outros países conectados.");
        addCountry("Itália", 480, 235,
                "Mercado tradicional com grande público e conexões potenciais, mas possui dificuldade relativa de entrada e expansão.");
        addCountry("Nigéria", 485, 330,
                "Mercado emergente com condições favoráveis para uma expansão inicial. Possui facilidade de entrada, mas retorno estratégico internacional limitado.");
        addCountry("África do Sul", 500, 445,
                "Mercado com grande potencial de crescimento e retorno. Possui dificuldade considerável e limites de expansão.");
        addCountry("Rússia", 640, 145,
                "Mercado intercontinental com barreiras de entrada. Oferece potencial de crescimento interno, mas retorno estratégico limitado sobre países conectados.");
        addCountry("China", 725, 245,
                "Mercado de domínio extremamente difícil, mas com retorno estratégico muito valioso.");
        addCountry("Austrália", 755, 425,
                "Mercado com barreiras relevantes à expansão e potencial de crescimento interno. Sua principal vantagem estratégica está em suas conexões.");
    }

    private void addCountry(String name, double centerX, double centerY, String description) {
        countries.put(name, new Country(name, centerX, centerY, description));
    }

    private void createRoutes() {
        // Conexões provisórias para demonstrar visualmente a seleção e o destaque dos vizinhos.
        addRoute("Estados Unidos", "Reino Unido");
        addRoute("Estados Unidos", "Peru");
        addRoute("Estados Unidos", "Brasil");
        addRoute("Estados Unidos", "Itália");
        addRoute("Peru", "Brasil");
        addRoute("Brasil", "Reino Unido");
        addRoute("Brasil", "Itália");
        addRoute("Brasil", "Nigéria");
        addRoute("Brasil", "África do Sul");
        addRoute("Reino Unido", "Itália");
        addRoute("Reino Unido", "Rússia");
        addRoute("Reino Unido", "Austrália");
        addRoute("Itália", "Rússia");
        addRoute("Itália", "China");
        addRoute("Rússia", "China");
        addRoute("Rússia", "África do Sul");
        addRoute("Rússia", "Austrália");
        addRoute("Nigéria", "China");
        addRoute("África do Sul", "Austrália");
        addRoute("China", "Austrália");
    }

    private void addRoute(String from, String to) {
        routes.add(new Route(from, to));
    }

    private VBox createHeader() {
        VBox header = new VBox(4);
        header.setPadding(new Insets(16, 22, 15, 22));
        header.setStyle("-fx-background-color: #080F16; -fx-border-color: #203440; -fx-border-width: 0 0 1 0;");

        Label logo = makeLabel("♠ LUDOPATIA", 26, "#F2F5F7", true);
        Label subtitle = makeLabel("A EPIDEMIA DAS APOSTAS", 10, "#8395A2", true);
        VBox brand = new VBox(0, logo, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label step = makeLabel("NOVA PARTIDA  /  ESCOLHA O PAÍS INICIAL", 13, "#E9B94E", true);
        Label hint = makeLabel("Selecione um dos 10 países para consultar seu perfil.", 11, "#8CA0AD", false);
        VBox instruction = new VBox(5, step, hint);
        instruction.setAlignment(Pos.CENTER_RIGHT);

        HBox row = new HBox(18, brand, spacer, instruction);
        row.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().add(row);
        return header;
    }

    private HBox createMainContent() {
        HBox main = new HBox(16);
        main.setPadding(new Insets(16));
        main.setAlignment(Pos.TOP_CENTER);

        VBox mapColumn = new VBox(9);
        Label mapTitle = makeLabel("MAPA DE EXPANSÃO", 15, "#F2F5F7", true);
        Label mapSubtitle = makeLabel("Clique em um país. As conexões diretas serão realçadas.", 11, "#8CA0AD", false);
        mapColumn.getChildren().addAll(mapTitle, mapSubtitle, createMapPane());
        HBox.setHgrow(mapColumn, Priority.ALWAYS);

        VBox detailsPanel = createDetailsPanel();
        detailsPanel.setPrefWidth(350);
        detailsPanel.setMinWidth(330);
        detailsPanel.setMaxWidth(350);
        detailsPanel.setPrefHeight(MAP_HEIGHT);

        main.getChildren().addAll(mapColumn, detailsPanel);
        return main;
    }

    private Pane createMapPane() {
        mapPane = new Pane();
        mapPane.setPrefSize(MAP_WIDTH, MAP_HEIGHT);
        mapPane.setMinSize(MAP_WIDTH, MAP_HEIGHT);
        mapPane.setMaxSize(MAP_WIDTH, MAP_HEIGHT);
        mapPane.setStyle("-fx-background-color: #07131B; -fx-border-color: #203440; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");

        drawGrid();
        drawStylizedLand();
        drawRoutes();
        addCountryNodes();

        Label note = makeLabel("MAPA ESQUEMÁTICO • LIGAÇÕES ILUSTRATIVAS", 9, "#526875", true);
        note.setLayoutX(18);
        note.setLayoutY(MAP_HEIGHT - 25);
        mapPane.getChildren().add(note);
        return mapPane;
    }

    private void drawGrid() {
        for (int x = 18; x < MAP_WIDTH; x += 32) {
            Line line = new Line(x, 0, x, MAP_HEIGHT);
            line.setStroke(Color.web("#10232D"));
            line.setStrokeWidth(0.6);
            line.setMouseTransparent(true);
            mapPane.getChildren().add(line);
        }
        for (int y = 18; y < MAP_HEIGHT; y += 32) {
            Line line = new Line(0, y, MAP_WIDTH, y);
            line.setStroke(Color.web("#10232D"));
            line.setStrokeWidth(0.6);
            line.setMouseTransparent(true);
            mapPane.getChildren().add(line);
        }
    }

    private void drawStylizedLand() {
        // Formas geométricas decorativas. Não são uma representação cartográfica precisa.
        addLand(new double[]{55, 95, 88, 75, 125, 63, 164, 75, 202, 105, 205, 142, 177, 174, 148, 198, 121, 183, 94, 151, 73, 124});
        addLand(new double[]{208, 250, 231, 272, 253, 302, 277, 335, 302, 374, 291, 411, 273, 447, 261, 411, 246, 370, 232, 327, 217, 291});
        addLand(new double[]{364, 109, 400, 87, 442, 93, 467, 112, 473, 135, 450, 157, 431, 180, 411, 169, 390, 145});
        addLand(new double[]{435, 187, 458, 180, 477, 198, 480, 225, 494, 252, 481, 282, 464, 316, 456, 351, 438, 329, 431, 293, 439, 255, 424, 223});
        addLand(new double[]{490, 101, 541, 76, 601, 73, 651, 91, 699, 84, 754, 112, 818, 146, 811, 179, 777, 212, 741, 201, 701, 175, 663, 184, 625, 161, 590, 175, 552, 153, 516, 161, 490, 139});
        addLand(new double[]{693, 365, 730, 348, 775, 358, 813, 386, 796, 417, 757, 444, 719, 426, 699, 395});
    }

    private void addLand(double[] points) {
        Polygon land = new Polygon(points);
        land.setFill(Color.web("#0B252A"));
        land.setStroke(Color.web("#15383A"));
        land.setStrokeWidth(1.2);
        land.setMouseTransparent(true);
        mapPane.getChildren().add(land);
    }

    private void drawRoutes() {
        routeLines.clear();
        for (Route route : routes) {
            Country from = countries.get(route.from);
            Country to = countries.get(route.to);
            if (from == null || to == null) continue;

            Line line = new Line(from.x, from.y, to.x, to.y);
            line.setStroke(Color.web("#31505E"));
            line.setStrokeWidth(1.3);
            line.setOpacity(0.72);
            line.setMouseTransparent(true);
            routeLines.add(new RouteLine(route, line));
            mapPane.getChildren().add(line);
        }
    }

    private void addCountryNodes() {
        countryButtons.clear();
        for (Country country : countries.values()) {
            Button button = new Button(country.name);
            button.setPrefSize(NODE_WIDTH, NODE_HEIGHT);
            button.setMinSize(NODE_WIDTH, NODE_HEIGHT);
            button.setMaxSize(NODE_WIDTH, NODE_HEIGHT);
            button.setWrapText(true);
            button.setFocusTraversable(false);
            button.setLayoutX(country.x - NODE_WIDTH / 2);
            button.setLayoutY(country.y - NODE_HEIGHT / 2);
            button.setOnAction(event -> selectCountry(country));
            countryButtons.put(country.name, button);
            mapPane.getChildren().add(button);
        }
    }

    private VBox createDetailsPanel() {
        VBox panel = new VBox(12);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #08131B; -fx-border-color: #203440; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label title = makeLabel("DETALHES DO PAÍS", 15, "#E9B94E", true);
        Label section = makeLabel("PAÍS INICIAL", 10, "#718692", true);
        selectedCountryName = makeLabel("Nenhum país selecionado", 21, "#F2F5F7", true);
        selectedCountryName.setWrapText(true);

        selectedCountryDescription = makeLabel(
                "Clique em um país no mapa para consultar seu perfil e decidir se deseja começar por ele.",
                12, "#C4D0D8", false);
        selectedCountryDescription.setWrapText(true);

        Label connectionsTitle = makeLabel("PAÍSES CONECTADOS", 10, "#718692", true);
        selectedCountryConnections = makeLabel("—", 12, "#91D6E7", false);
        selectedCountryConnections.setWrapText(true);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        statusLabel = makeLabel("Escolha um país para habilitar a confirmação.", 11, "#8CA0AD", false);
        statusLabel.setWrapText(true);
        confirmButton = new Button("CONFIRMAR PAÍS INICIAL");
        confirmButton.setMaxWidth(Double.MAX_VALUE);
        confirmButton.setPrefHeight(46);
        confirmButton.setDisable(true);
        confirmButton.setStyle(buttonStyle(false));
        confirmButton.setOnAction(event -> confirmCountry());

        Label note = makeLabel("Protótipo • Posições e conexões demonstrativas", 9, "#526875", false);
        panel.getChildren().addAll(title, separator(), section, selectedCountryName,
                selectedCountryDescription, separator(), connectionsTitle,
                selectedCountryConnections, spacer, statusLabel, confirmButton, note);
        return panel;
    }

    private HBox createFooter() {
        HBox footer = new HBox(18);
        footer.setPadding(new Insets(0, 22, 12, 22));
        footer.setAlignment(Pos.CENTER_LEFT);
        Label hint = makeLabel("DICA: selecione países diferentes para comparar suas descrições e conexões.", 10, "#657B87", false);
        footer.getChildren().add(hint);
        return footer;
    }

    private Region separator() {
        Region rule = new Region();
        rule.setPrefHeight(1);
        rule.setMinHeight(1);
        rule.setMaxHeight(1);
        rule.setStyle("-fx-background-color: #203440;");
        return rule;
    }

    private void selectCountry(Country country) {
        if (countryConfirmed) return;
        selectedCountry = country;
        updateDetails();
        refreshCountryButtons();
    }

    private void updateDetails() {
        if (selectedCountry == null) {
            selectedCountryName.setText("Nenhum país selecionado");
            selectedCountryDescription.setText("Clique em um país no mapa para consultar seu perfil e decidir se deseja começar por ele.");
            selectedCountryConnections.setText("—");
            statusLabel.setText("Escolha um país para habilitar a confirmação.");
            confirmButton.setDisable(true);
            confirmButton.setText("CONFIRMAR PAÍS INICIAL");
            return;
        }

        selectedCountryName.setText(selectedCountry.name);
        selectedCountryDescription.setText(selectedCountry.description);
        selectedCountryConnections.setText(getConnectedCountryNames(selectedCountry.name));
        statusLabel.setText(countryConfirmed
                ? "País inicial confirmado: " + selectedCountry.name + "."
                : "País selecionado. Confirme para definir o início da partida.");
        confirmButton.setDisable(countryConfirmed);
        confirmButton.setText(countryConfirmed
                ? "PAÍS INICIAL CONFIRMADO"
                : "INICIAR POR " + selectedCountry.name.toUpperCase());
    }

    private String getConnectedCountryNames(String name) {
        List<String> connected = new ArrayList<>();
        for (Route route : routes) {
            if (route.from.equals(name)) connected.add(route.to);
            else if (route.to.equals(name)) connected.add(route.from);
        }
        return connected.isEmpty() ? "Nenhuma conexão definida" : String.join("  •  ", connected);
    }

    private void refreshCountryButtons() {
        for (Country country : countries.values()) {
            Button button = countryButtons.get(country.name);
            if (button == null) continue;

            boolean selected = selectedCountry != null && selectedCountry.name.equals(country.name);
            boolean connected = selectedCountry != null && areConnected(selectedCountry.name, country.name);
            String border = selected ? "#F04A52" : connected ? "#63B9CA" : "#35515D";
            String background = selected ? "#4B171D" : connected ? "#102933" : "#0B1A23";
            String text = selected ? "#FFFFFF" : "#DDE6EB";

            button.setStyle("-fx-background-color: " + background + ";"
                    + "-fx-text-fill: " + text + ";"
                    + "-fx-border-color: " + border + ";"
                    + "-fx-border-width: " + (selected ? "2" : "1") + ";"
                    + "-fx-border-radius: 8; -fx-background-radius: 8;"
                    + "-fx-font-family: 'Segoe UI'; -fx-font-size: 11px; -fx-font-weight: bold;"
                    + "-fx-padding: 4 6 4 6; -fx-cursor: hand;");
            button.setDisable(countryConfirmed);
        }

        for (RouteLine routeLine : routeLines) {
            boolean highlight = selectedCountry != null &&
                    (routeLine.route.from.equals(selectedCountry.name) || routeLine.route.to.equals(selectedCountry.name));
            routeLine.line.setStroke(Color.web(highlight ? "#65C9DF" : "#31505E"));
            routeLine.line.setStrokeWidth(highlight ? 2.6 : 1.2);
            routeLine.line.setOpacity(selectedCountry == null ? 0.72 : (highlight ? 1.0 : 0.28));
        }
    }

    private boolean areConnected(String countryA, String countryB) {
        if (countryA.equals(countryB)) return false;
        return routes.stream().anyMatch(route ->
                (route.from.equals(countryA) && route.to.equals(countryB)) ||
                        (route.from.equals(countryB) && route.to.equals(countryA)));
    }

    private void confirmCountry() {
        if (selectedCountry == null || countryConfirmed) return;
        countryConfirmed = true;
        statusLabel.setText("País inicial definido: " + selectedCountry.name + ". A simulação ainda não começa nesta versão.");
        updateDetails();
        refreshCountryButtons();
    }

    private Label makeLabel(String text, int size, String color, boolean bold) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", bold ? FontWeight.BOLD : FontWeight.NORMAL, size));
        label.setTextFill(Color.web(color));
        return label;
    }

    private String buttonStyle(boolean disabled) {
        return "-fx-background-color: #9F242B; -fx-text-fill: white;"
                + "-fx-border-color: #E84B51; -fx-border-width: 1;"
                + "-fx-border-radius: 7; -fx-background-radius: 7;"
                + "-fx-font-family: 'Segoe UI'; -fx-font-size: 11px; -fx-font-weight: bold;"
                + "-fx-opacity: " + (disabled ? "0.7" : "1.0") + "; -fx-cursor: hand;";
    }

    private static class Country {
        private final String name;
        private final double x;
        private final double y;
        private final String description;

        private Country(String name, double x, double y, String description) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.description = description;
        }
    }

    private static class Route {
        private final String from;
        private final String to;

        private Route(String from, String to) {
            this.from = from;
            this.to = to;
        }
    }

    private static class RouteLine {
        private final Route route;
        private final Line line;

        private RouteLine(Route route, Line line) {
            this.route = route;
            this.line = line;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
