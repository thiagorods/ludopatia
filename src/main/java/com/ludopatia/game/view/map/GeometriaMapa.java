package com.ludopatia.game.view.map;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Geometria do mapa (contornos dos países em caminhos SVG) lida de {@code assets/map/mapa.json}.
 *
 * O arquivo é gerado por {@code tools/gerar_mapa.py}. Aqui há apenas dados de apresentação do mapa;
 * os atributos de jogo de cada país continuam vindo do banco (tabela {@code pais}), ligados por {@code paisId}.
 */
public final class GeometriaMapa {

    private static final String RECURSO = "/com/ludopatia/game/assets/map/mapa.json";

    /** Um país desenhado no mapa. */
    public static final class FormaPais {
        public String codigo;                       // ISO alfa-3, ex.: "BRA"
        public int paisId;                         // id na tabela `pais`
        public String nome;                         // usado só na dica (tooltip) do mapa
        public String bandeira;                     // arquivo em assets/images/main/
        public String caminho;                      // atributo "d" do SVG (só M, L e Z)
        public double[] ref;                        // ponto de referência [x, y]
        public List<String> conexoes = new ArrayList<>();   // códigos dos países conectados
    }

    public double largura;
    public double altura;
    public String fundo;
    public List<FormaPais> paises = new ArrayList<>();

    private GeometriaMapa() {
    }

    public static GeometriaMapa carregar() {
        try (InputStream in = GeometriaMapa.class.getResourceAsStream(RECURSO)) {
            if (in == null) {
                throw new IllegalStateException("Recurso não encontrado: " + RECURSO);
            }
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                GeometriaMapa geometria = new Gson().fromJson(reader, GeometriaMapa.class);
                geometria.validar();
                return geometria;
            }
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler " + RECURSO, e);
        }
    }

    /** Falha cedo, com mensagem clara, se o JSON estiver inconsistente. */
    private void validar() {
        if (largura <= 0 || altura <= 0 || fundo == null || paises == null || paises.isEmpty()) {
            throw new IllegalStateException("mapa.json incompleto.");
        }
        Set<String> codigos = new HashSet<>();
        Set<Integer> ids = new HashSet<>();
        for (FormaPais p : paises) {
            if (p.codigo == null || p.caminho == null || p.ref == null || p.ref.length != 2) {
                throw new IllegalStateException("País inválido em mapa.json: " + p.codigo);
            }
            if (!codigos.add(p.codigo) || !ids.add(p.paisId)) {
                throw new IllegalStateException("Código ou paisId repetido em mapa.json: " + p.codigo);
            }
        }
        for (FormaPais p : paises) {
            for (String vizinho : p.conexoes) {
                if (!codigos.contains(vizinho)) {
                    throw new IllegalStateException(p.codigo + " conecta com país desconhecido: " + vizinho);
                }
            }
        }
    }
}
