#!/usr/bin/env python3
"""Gera `src/main/resources/com/ludopatia/game/assets/map/mapa.json` para o jogo.

Uso:
    python tools/gerar_mapa.py                       # usa as formas simplificadas embutidas
    python tools/gerar_mapa.py --geojson ne_110m_admin_0_countries.geojson
    python tools/gerar_mapa.py --preview mapa.png    # (opcional, requer matplotlib)

Saída (mapa.json):
    {"largura": 1000, "altura": ..., "fundo": "M..Z",
     "paises": [{"codigo": "BRA", "paisId": 1, "nome": "Brasil", "bandeira": "brazil-flag.png",
                 "caminho": "M..Z", "ref": [x, y], "conexoes": ["PER", "USA", ...]}, ...]}

`paisId` é o id da tabela `pais` (PaisSeeder). `conexoes` usa os códigos (ADM0_A3) dos países.

Os caminhos usam só os comandos absolutos M, L e Z, dentro de uma caixa
`largura x altura`. O jogo (SVGPath do JavaFX) os desenha sem conversões.
Projeção: Miller (cilíndrica, parecida com a de mapas-múndi escolares).
"""
import argparse
import json
import math
import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent))
import formas_simplificadas as built_in  # noqa: E402

IDS_JOGO = list(built_in.PAISES.keys())          # ISO alfa-3 (= ADM0_A3 do Natural Earth)

# codigo -> (paisId na tabela `pais`, nome, arquivo da bandeira, conexões). Mantenha alinhado ao PaisSeeder.
META = {
    "BRA": (1, "Brasil", "brazil-flag.png", ["PER", "USA", "ITA", "ZAF", "RUS", "CHN"]),
    "PER": (2, "Peru", "peru-flag.png", ["BRA", "USA"]),
    "USA": (3, "Estados Unidos", "united-states-flag.png", ["PER", "BRA", "GBR", "ITA", "AUS"]),
    "GBR": (4, "Reino Unido", "united-kingdom-flag.png", ["USA", "ITA", "NGA", "AUS", "CHN"]),
    "ITA": (5, "Itália", "italy-flag.png", ["BRA", "GBR", "USA"]),
    "RUS": (6, "Rússia", "russia-flag.png", ["BRA", "CHN"]),
    "CHN": (7, "China", "china-flag.png", ["RUS", "GBR", "BRA", "ZAF"]),
    "NGA": (8, "Nigéria", "nigeria-flag.png", ["ZAF", "GBR", "AUS"]),
    "ZAF": (9, "África do Sul", "south-africa-flag.png", ["BRA", "CHN", "NGA"]),
    "AUS": (10, "Austrália", "australia-flag.png", ["GBR", "USA", "NGA"]),
}
LARGURA = 1000.0
LAT_MAX, LAT_MIN = 84.0, -58.0                   # corta o polo sul (Antártida)


def miller_y(lat):
    phi = math.radians(max(min(lat, LAT_MAX), LAT_MIN))
    return 1.25 * math.log(math.tan(math.pi / 4 + 0.4 * phi))


ESCALA = LARGURA / (2 * math.pi)
Y_TOPO = miller_y(LAT_MAX)
ALTURA = round((Y_TOPO - miller_y(LAT_MIN)) * ESCALA, 1)


def projetar(lon, lat):
    x = (lon + 180.0) / 360.0 * LARGURA
    y = (Y_TOPO - miller_y(lat)) * ESCALA
    return x, y


def area(anel):
    s = 0.0
    for (x1, y1), (x2, y2) in zip(anel, anel[1:] + anel[:1]):
        s += x1 * y2 - x2 * y1
    return abs(s) / 2


def douglas_peucker(pts, tol):
    if len(pts) < 3:
        return pts
    (x1, y1), (x2, y2) = pts[0], pts[-1]
    dx, dy = x2 - x1, y2 - y1
    norma = math.hypot(dx, dy)
    melhor, idx = -1.0, 0
    for i in range(1, len(pts) - 1):
        x, y = pts[i]
        d = (math.hypot(x - x1, y - y1) if norma == 0
             else abs(dy * x - dx * y + x2 * y1 - y2 * x1) / norma)
        if d > melhor:
            melhor, idx = d, i
    if melhor > tol:
        return douglas_peucker(pts[:idx + 1], tol)[:-1] + douglas_peucker(pts[idx:], tol)
    return [pts[0], pts[-1]]


def anel_para_pixels(anel_lonlat, tol=0.0):
    pts = [projetar(lon, lat) for lon, lat in anel_lonlat]
    if len(pts) > 1 and pts[0] == pts[-1]:
        pts = pts[:-1]
    if tol > 0 and len(pts) > 8:
        pts = douglas_peucker(pts + [pts[0]], tol)[:-1]
    return pts


def para_caminho(aneis_px):
    partes = []
    for anel in aneis_px:
        if len(anel) < 3:
            continue
        partes.append("M%.1f %.1f" % anel[0])
        partes.extend("L%.1f %.1f" % p for p in anel[1:])
        partes.append("Z")
    return " ".join(partes)


def carregar_formas_embutidas():
    paises = {i: [anel for anel in aneis] for i, aneis in built_in.PAISES.items()}
    fundo = list(built_in.FUNDO)
    return paises, fundo


def carregar_geojson(caminho):
    dados = json.loads(Path(caminho).read_text(encoding="utf-8"))
    paises = {i: [] for i in IDS_JOGO}
    fundo = []
    for feat in dados["features"]:
        props = feat.get("properties", {})
        fid = props.get("ADM0_A3") or props.get("ISO_A3") or props.get("iso_a3") or feat.get("id")
        if fid == "ATA":
            continue
        geom = feat["geometry"]
        poligonos = geom["coordinates"] if geom["type"] == "MultiPolygon" else [geom["coordinates"]]
        aneis = [[(p[0], p[1]) for p in pol[0]] for pol in poligonos]   # só o anel externo
        if fid in paises:
            paises[fid].extend(aneis)
        else:
            fundo.extend(aneis)
    faltando = [i for i, a in paises.items() if not a]
    if faltando:
        sys.exit("Países não encontrados no GeoJSON: %s" % ", ".join(faltando))
    return paises, fundo


def gerar(paises, fundo, tol, area_min):
    saida_paises = []
    for pid in IDS_JOGO:
        aneis = [anel_para_pixels(a, tol) for a in paises[pid]]
        aneis = [a for a in aneis if len(a) >= 3 and area(a) >= area_min]
        if not aneis:
            sys.exit("Sem geometria para %s" % pid)
        lon, lat = built_in.REF[pid]
        x, y = projetar(lon, lat)
        pais_id, nome, bandeira, conexoes = META[pid]
        saida_paises.append({"codigo": pid, "paisId": pais_id, "nome": nome, "bandeira": bandeira,
                             "caminho": para_caminho(aneis), "ref": [round(x, 1), round(y, 1)],
                             "conexoes": conexoes})
    aneis_fundo = [anel_para_pixels(a, tol) for a in fundo]
    aneis_fundo = [a for a in aneis_fundo if len(a) >= 3 and area(a) >= area_min]
    return {"largura": LARGURA, "altura": ALTURA, "fundo": para_caminho(aneis_fundo), "paises": saida_paises}


def desenhar_previa(mapa, destino):
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt
    from matplotlib.patches import Polygon

    def aneis(d):
        for bloco in re.findall(r"M[^Z]*Z", d):
            pts = [tuple(map(float, p)) for p in re.findall(r"(-?\d+\.?\d*) (-?\d+\.?\d*)", bloco)]
            yield pts

    fig, ax = plt.subplots(figsize=(14, 14 * mapa["altura"] / mapa["largura"]))
    fig.patch.set_facecolor("#0b1018")
    ax.set_facecolor("#0b1018")
    for pts in aneis(mapa["fundo"]):
        ax.add_patch(Polygon(pts, closed=True, fc="#232832", ec="#2f3644", lw=0.6))
    cores = ["#c0392b", "#27ae60", "#2980b9", "#8e44ad", "#d35400", "#16a085", "#f1c40f", "#7f8c8d", "#e84393", "#fd79a8"]
    for p, cor in zip(mapa["paises"], cores):
        for pts in aneis(p["caminho"]):
            ax.add_patch(Polygon(pts, closed=True, fc=cor, ec="white", lw=0.8, alpha=0.9))
        ax.plot(*p["ref"], "wo", ms=4)
        ax.annotate(p["codigo"], p["ref"], color="white", fontsize=9, xytext=(5, 5), textcoords="offset points")
    ax.set_xlim(0, mapa["largura"])
    ax.set_ylim(mapa["altura"], 0)
    ax.axis("off")
    fig.savefig(destino, dpi=110, bbox_inches="tight", facecolor=fig.get_facecolor())
    plt.close(fig)


def main():
    raiz = Path(__file__).resolve().parent.parent
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--geojson", help="GeoJSON do Natural Earth (admin_0_countries)")
    ap.add_argument("--saida", default=str(raiz / "src/main/resources/com/ludopatia/game/assets/map/mapa.json"))
    ap.add_argument("--tolerancia", type=float, default=None, help="simplificação em pixels (padrão: 0.6 com GeoJSON)")
    ap.add_argument("--area-minima", type=float, default=None, help="descarta ilhas menores que N px² (padrão: 6 com GeoJSON)")
    ap.add_argument("--preview", help="grava uma prévia PNG (requer matplotlib)")
    a = ap.parse_args()

    if a.geojson:
        paises, fundo = carregar_geojson(a.geojson)
        tol = 0.6 if a.tolerancia is None else a.tolerancia
        area_min = 6.0 if a.area_minima is None else a.area_minima
    else:
        paises, fundo = carregar_formas_embutidas()
        tol = 0.0 if a.tolerancia is None else a.tolerancia
        area_min = 0.0 if a.area_minima is None else a.area_minima

    mapa = gerar(paises, fundo, tol, area_min)
    saida = Path(a.saida)
    saida.parent.mkdir(parents=True, exist_ok=True)
    saida.write_text(json.dumps(mapa, ensure_ascii=False, separators=(",", ":")), encoding="utf-8")
    print("Gravado %s (%.1f KB, %dx%d)" % (saida, saida.stat().st_size / 1024, mapa["largura"], mapa["altura"]))
    if a.preview:
        desenhar_previa(mapa, a.preview)
        print("Prévia: %s" % a.preview)


if __name__ == "__main__":
    main()
