package com.ludopatia.game.view.map;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Mapa interativo do jogo. Cada país é um {@link SVGPath}; a cor muda conforme a intensidade (0 a 1)
 * informada pelo motor da simulação.
 *
 * Interações: passar o mouse (realce e nome), clicar (seleciona e avisa o ouvinte), arrastar (mover),
 * roda do mouse (zoom no cursor) e duplo clique no mar (enquadrar tudo).
 *
 * O componente não conhece o banco nem as regras do jogo: ele só fala em {@code paisId} (o id da
 * tabela {@code pais}) e em cores. Quem decide o que mostrar ao selecionar é o controller da tela.
 */
public class MapaView extends Pane {

    // Paleta alinhada ao main-view.css (fundo #07151E, bordas #263D4B, vermelho #B80D17 / #FF2633).
    private static final Color COR_TERRA_FUNDO = Color.web("#0F2431");
    private static final Color COR_BORDA_FUNDO = Color.web("#17303F");
    private static final Color COR_PAIS = Color.web("#1D3748");
    private static final Color COR_ALTA = Color.web("#B80D17");
    private static final Color COR_BORDA = Color.web("#4A6B7E");
    private static final Color COR_SELECAO = Color.web("#FF2633");
    private static final Color COR_LIGACAO = Color.web("#6FA6C2", 0.30);
    private static final Color COR_LIGACAO_DESTAQUE = Color.web("#FF414B", 0.95);
    private static final Color COR_MARCADOR = Color.web("#6F8796");
    private static final Color COR_MARCADOR_ATIVO = Color.web("#FF414B");
    private static final Color COR_BOLA = Color.web("#FFD34D");

    private static final double ZOOM_PASSO = 1.15;
    private static final double LIMITE_ARRASTE_PX = 4;
    private static final double FOLGA_VISIVEL_PX = 70;

    private final GeometriaMapa geometria;

    private final Group mundo = new Group();                   // recebe zoom e deslocamento
    private final Group camadaPaises = new Group();
    private final Group camadaLigacoes = new Group();
    private final Group camadaMarcadores = new Group();
    private final Group camadaAnimacao = new Group();

    private final Translate deslocamento = new Translate(0, 0);
    private final Scale escala = new Scale(1, 1, 0, 0);

    // chaves internas: código do país (ex.: "BRA")
    private final Map<String, SVGPath> formas = new LinkedHashMap<>();
    private final Map<String, Point2D> refs = new HashMap<>();
    private final Map<String, Circle> marcadores = new HashMap<>();
    private final Map<String, QuadCurve> ligacoes = new LinkedHashMap<>();
    private final Map<String, Double> intensidade = new HashMap<>();
    private final Set<String> ativos = new HashSet<>();
    private final Map<Integer, String> codigoPorPaisId = new HashMap<>();

    private String sobre;
    private String selecionado;
    private Consumer<Integer> aoSelecionar = id -> { };

    private double ancoraX, ancoraY, baseTx, baseTy;
    private boolean arrastou;
    private boolean usuarioMexeu;                               // se true, não re-enquadra ao redimensionar
    private double escalaAjuste = 1;
    private Insets areaSobreposta = Insets.EMPTY;
    private Timeline animacaoDeslocamento;

    public MapaView(GeometriaMapa geometria) {
        this.geometria = Objects.requireNonNull(geometria);
        getStyleClass().add("map-view");
        setPickOnBounds(true);                                  // o "mar" também recebe cliques

        Rectangle recorte = new Rectangle();
        recorte.widthProperty().bind(widthProperty());
        recorte.heightProperty().bind(heightProperty());
        setClip(recorte);

        mundo.getTransforms().addAll(deslocamento, escala);     // escala primeiro, depois desloca
        getChildren().add(mundo);

        construirFundo();
        construirPaises();
        construirLigacoes();
        construirMarcadores();

        camadaLigacoes.setMouseTransparent(true);
        camadaMarcadores.setMouseTransparent(true);
        camadaAnimacao.setMouseTransparent(true);
        mundo.getChildren().addAll(camadaPaises, camadaLigacoes, camadaMarcadores, camadaAnimacao);

        configurarInteracao();
        widthProperty().addListener((o, a, b) -> aoRedimensionar());
        heightProperty().addListener((o, a, b) -> aoRedimensionar());
        repintar();
    }

    // ------------------------------------------------------------------ construção

    private void construirFundo() {
        SVGPath fundo = new SVGPath();
        fundo.setContent(geometria.fundo);
        fundo.setFill(COR_TERRA_FUNDO);
        fundo.setStroke(COR_BORDA_FUNDO);
        fundo.setStrokeWidth(0.6);
        fundo.setMouseTransparent(true);
        mundo.getChildren().add(fundo);
    }

    private void construirPaises() {
        for (GeometriaMapa.FormaPais f : geometria.paises) {
            final String codigo = f.codigo;
            SVGPath s = new SVGPath();
            s.setContent(f.caminho);
            s.setStrokeLineJoin(StrokeLineJoin.ROUND);
            s.setCursor(Cursor.HAND);

            Tooltip dica = new Tooltip(f.nome != null ? f.nome : codigo);
            dica.setShowDelay(Duration.millis(150));
            Tooltip.install(s, dica);

            s.setOnMouseEntered(e -> definirSobre(codigo));
            s.setOnMouseExited(e -> {
                if (codigo.equals(sobre)) definirSobre(null);
            });
            s.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY && !arrastou) selecionarCodigo(codigo);
            });

            formas.put(codigo, s);
            refs.put(codigo, new Point2D(f.ref[0], f.ref[1]));
            codigoPorPaisId.put(f.paisId, codigo);
            camadaPaises.getChildren().add(s);
        }
    }

    private void construirLigacoes() {
        for (GeometriaMapa.FormaPais f : geometria.paises) {
            for (String outro : f.conexoes) {
                String chave = chave(f.codigo, outro);
                if (ligacoes.containsKey(chave)) continue;
                Point2D a = refs.get(f.codigo);
                Point2D b = refs.get(outro);
                Point2D c = controle(f.codigo, outro);
                QuadCurve q = new QuadCurve(a.getX(), a.getY(), c.getX(), c.getY(), b.getX(), b.getY());
                q.setFill(null);                                // sem preenchimento (o padrão seria preto)
                q.setStroke(COR_LIGACAO);
                ligacoes.put(chave, q);
                camadaLigacoes.getChildren().add(q);
            }
        }
    }

    private void construirMarcadores() {
        for (String codigo : formas.keySet()) {
            Point2D r = refs.get(codigo);
            Circle c = new Circle(r.getX(), r.getY(), 3);
            c.setStroke(Color.web("#030B10"));
            marcadores.put(codigo, c);
            camadaMarcadores.getChildren().add(c);
        }
    }

    /** Ponto de controle da curva, calculado na ordem canônica para ficar igual nos dois sentidos. */
    private Point2D controle(String x, String y) {
        String a = x.compareTo(y) < 0 ? x : y;
        String b = x.compareTo(y) < 0 ? y : x;
        Point2D pa = refs.get(a);
        Point2D pb = refs.get(b);
        double dx = pb.getX() - pa.getX();
        double dy = pb.getY() - pa.getY();
        double len = Math.hypot(dx, dy);
        double nx = -dy / len;
        double ny = dx / len;
        if (ny > 0) {                                           // sempre arqueia para cima
            nx = -nx;
            ny = -ny;
        }
        double k = 0.22 * len;
        return new Point2D((pa.getX() + pb.getX()) / 2 + nx * k, (pa.getY() + pb.getY()) / 2 + ny * k);
    }

    private static String chave(String a, String b) {
        return a.compareTo(b) < 0 ? a + "-" + b : b + "-" + a;
    }

    // ------------------------------------------------------------------ interação do pane

    private void configurarInteracao() {
        setOnMousePressed(e -> {
            ancoraX = e.getX();
            ancoraY = e.getY();
            baseTx = deslocamento.getX();
            baseTy = deslocamento.getY();
            arrastou = false;
        });
        setOnMouseDragged(e -> {
            if (!arrastou && Math.hypot(e.getX() - ancoraX, e.getY() - ancoraY) > LIMITE_ARRASTE_PX) {
                arrastou = true;
                usuarioMexeu = true;
                pararAnimacaoDeslocamento();
            }
            if (arrastou) {
                deslocamento.setX(baseTx + (e.getX() - ancoraX));
                deslocamento.setY(baseTy + (e.getY() - ancoraY));
            }
        });
        setOnMouseClicked(e -> {
            // clique no mar (o próprio pane): duplo clique enquadra; clique simples limpa a seleção
            if (e.getTarget() != this || arrastou) return;
            if (e.getClickCount() == 2) ajustarAoTamanho();
            else selecionarCodigo(null);
        });
        setOnScroll(e -> {
            if (e.getDeltaY() == 0) return;
            pararAnimacaoDeslocamento();
            usuarioMexeu = true;
            zoomEm(e.getX(), e.getY(), e.getDeltaY() > 0 ? ZOOM_PASSO : 1 / ZOOM_PASSO);
            e.consume();
        });
    }

    private void zoomEm(double px, double py, double fator) {
        double atual = escala.getX();
        double nova = Math.max(escalaAjuste * 0.8, Math.min(escalaAjuste * 14, atual * fator));
        double razao = nova / atual;
        deslocamento.setX(px - (px - deslocamento.getX()) * razao);
        deslocamento.setY(py - (py - deslocamento.getY()) * razao);
        escala.setX(nova);
        escala.setY(nova);
        repintar();
    }

    private void aoRedimensionar() {
        if (!usuarioMexeu) ajustarAoTamanho();
    }

    // ------------------------------------------------------------------ API para a tela e para o motor

    /** Enquadra o mapa inteiro, centralizado, no tamanho atual do componente. */
    public void ajustarAoTamanho() {
        double w = getWidth();
        double h = getHeight();
        if (w <= 0 || h <= 0) return;
        pararAnimacaoDeslocamento();
        escalaAjuste = Math.min(w / geometria.largura, h / geometria.altura);
        escala.setX(escalaAjuste);
        escala.setY(escalaAjuste);
        deslocamento.setX((w - geometria.largura * escalaAjuste) / 2);
        deslocamento.setY((h - geometria.altura * escalaAjuste) / 2);
        usuarioMexeu = false;
        repintar();
    }

    /** Áreas cobertas por painéis sobrepostos ao mapa (menu lateral, painel do país). Usado por {@link #garantirVisivel}. */
    public void setAreaSobreposta(Insets area) {
        this.areaSobreposta = area == null ? Insets.EMPTY : area;
    }

    /** Ouvinte de seleção: recebe o {@code paisId} do país clicado, ou {@code null} quando a seleção é limpa. */
    public void setOnPaisSelecionado(Consumer<Integer> acao) {
        this.aoSelecionar = acao == null ? id -> { } : acao;
    }

    public Integer getPaisSelecionado() {
        return selecionado == null ? null : paisIdDe(selecionado);
    }

    /** Seleciona por código (a mesma coisa que um clique). {@code null} limpa a seleção. */
    public void selecionarPais(Integer paisId) {
        selecionarCodigo(paisId == null ? null : codigoPorPaisId.get(paisId));
    }

    /** Intensidade de 0 a 1 (por exemplo, % de expostos ou de falência) que define a cor do país. */
    public void definirIntensidade(int paisId, double valor) {
        String codigo = codigoPorPaisId.get(paisId);
        if (codigo == null) return;
        intensidade.put(codigo, Math.max(0, Math.min(1, valor)));
        repintar();
    }

    /** País com presença da casa de apostas (marcador aceso). */
    public void definirAtivo(int paisId, boolean ativo) {
        String codigo = codigoPorPaisId.get(paisId);
        if (codigo == null) return;
        if (ativo) ativos.add(codigo);
        else ativos.remove(codigo);
        repintar();
    }

    public void limparEstado() {
        intensidade.clear();
        ativos.clear();
        repintar();
    }

    /** Se o país estiver escondido atrás de um painel, desliza o mapa o mínimo necessário para mostrá-lo. */
    public void garantirVisivel(long paisId) {
        String codigo = codigoPorPaisId.get(paisId);
        if (codigo == null || getWidth() <= 0) return;

        Point2D r = refs.get(codigo);
        double z = escala.getX();
        double sx = deslocamento.getX() + r.getX() * z;
        double sy = deslocamento.getY() + r.getY() * z;

        double minX = areaSobreposta.getLeft() + FOLGA_VISIVEL_PX;
        double maxX = getWidth() - areaSobreposta.getRight() - FOLGA_VISIVEL_PX;
        double minY = areaSobreposta.getTop() + FOLGA_VISIVEL_PX;
        double maxY = getHeight() - areaSobreposta.getBottom() - FOLGA_VISIVEL_PX;
        if (minX > maxX) minX = maxX = (minX + maxX) / 2;
        if (minY > maxY) minY = maxY = (minY + maxY) / 2;

        double dx = sx < minX ? minX - sx : (sx > maxX ? maxX - sx : 0);
        double dy = sy < minY ? minY - sy : (sy > maxY ? maxY - sy : 0);
        if (dx == 0 && dy == 0) return;

        usuarioMexeu = true;
        pararAnimacaoDeslocamento();
        animacaoDeslocamento = new Timeline(new KeyFrame(Duration.millis(350),
                new KeyValue(deslocamento.xProperty(), deslocamento.getX() + dx, Interpolator.EASE_BOTH),
                new KeyValue(deslocamento.yProperty(), deslocamento.getY() + dy, Interpolator.EASE_BOTH)));
        animacaoDeslocamento.play();
    }

    /** Anima uma bolinha percorrendo a ligação de {@code de} até {@code para}; depois chama {@code aoTerminar}. */
    public void animarExpansao(long de, long para, Runnable aoTerminar) {
        String cDe = codigoPorPaisId.get(de);
        String cPara = codigoPorPaisId.get(para);
        if (cDe == null || cPara == null) {
            if (aoTerminar != null) aoTerminar.run();
            return;
        }
        Point2D a = refs.get(cDe);
        Point2D b = refs.get(cPara);
        Point2D c = controle(cDe, cPara);
        QuadCurve trajeto = new QuadCurve(a.getX(), a.getY(), c.getX(), c.getY(), b.getX(), b.getY());
        trajeto.setFill(null);
        trajeto.setStroke(null);

        Circle bola = new Circle(0, 0, 5 / escala.getX(), COR_BOLA);
        camadaAnimacao.getChildren().add(bola);

        PathTransition pt = new PathTransition(Duration.millis(700), trajeto, bola);
        pt.setOnFinished(e -> {
            camadaAnimacao.getChildren().remove(bola);
            if (aoTerminar != null) aoTerminar.run();
        });
        pt.play();
    }

    // ------------------------------------------------------------------ seleção e aparência

    private void selecionarCodigo(String codigo) {
        if (Objects.equals(codigo, selecionado)) return;
        selecionado = codigo;
        if (codigo != null) formas.get(codigo).toFront();       // borda da seleção por cima dos vizinhos
        repintar();
        aoSelecionar.accept(codigo == null ? null : paisIdDe(codigo));
    }

    private Integer paisIdDe(String codigo) {
        for (GeometriaMapa.FormaPais f : geometria.paises) {
            if (f.codigo.equals(codigo)) return f.paisId;
        }
        return null;
    }

    private void definirSobre(String codigo) {
        sobre = codigo;
        repintar();
    }

    private void pararAnimacaoDeslocamento() {
        if (animacaoDeslocamento != null) animacaoDeslocamento.stop();
    }

    private Color corDe(String codigo) {
        double t = intensidade.getOrDefault(codigo, 0.0);
        Color c = COR_PAIS.interpolate(COR_ALTA, t);
        if (codigo.equals(sobre)) c = c.deriveColor(0, 1, 1.35, 1);
        return c;
    }

    /** Aplica cores, bordas e marcadores. Barato: são só 10 países. Larguras dividem pelo zoom para manter o traço fino. */
    private void repintar() {
        double z = escala.getX();
        for (Map.Entry<String, SVGPath> e : formas.entrySet()) {
            String codigo = e.getKey();
            SVGPath s = e.getValue();
            boolean sel = codigo.equals(selecionado);
            boolean hover = codigo.equals(sobre);
            s.setFill(corDe(codigo));
            s.setStroke(sel ? COR_SELECAO : hover ? Color.WHITE : COR_BORDA);
            s.setStrokeWidth((sel ? 2.2 : hover ? 1.6 : 0.9) / z);
            s.setEffect(sel ? new DropShadow(14 / z, COR_SELECAO) : null);
        }
        for (Map.Entry<String, QuadCurve> e : ligacoes.entrySet()) {
            boolean destaque = selecionado != null && chaveContem(e.getKey(), selecionado);
            QuadCurve q = e.getValue();
            q.setStroke(destaque ? COR_LIGACAO_DESTAQUE : COR_LIGACAO);
            q.setStrokeWidth((destaque ? 2.0 : 1.1) / z);
        }
        for (Map.Entry<String, Circle> e : marcadores.entrySet()) {
            boolean ativo = ativos.contains(e.getKey());
            Circle c = e.getValue();
            c.setRadius((ativo ? 4.2 : 3.0) / z);
            c.setFill(ativo ? COR_MARCADOR_ATIVO : COR_MARCADOR);
            c.setStrokeWidth(1.0 / z);
        }
    }

    private static boolean chaveContem(String chave, String codigo) {
        for (String parte : chave.split("-")) {
            if (parte.equals(codigo)) return true;
        }
        return false;
    }
}
