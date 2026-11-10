import argparse
import statistics
import threading
import time
import urllib.error
import urllib.request
from datetime import datetime
from html import escape

BASE_URL = "https://jzaageilkvvucgedlwxr.supabase.co"
ENDPOINT = "/rest/v1/courses"

# (usuarios simultaneos, total de requisicoes)
ETAPAS = [(5, 50), (10, 100), (25, 250)]


def uma_requisicao(url, chave, timeout):
    """Faz um GET e devolve (status HTTP, tempo em ms). Status 0 = falha de rede."""
    req = urllib.request.Request(
        url, headers={"apikey": chave, "Authorization": "Bearer " + chave}
    )
    inicio = time.perf_counter()
    try:
        with urllib.request.urlopen(req, timeout=timeout) as resp:
            resp.read()
            status = resp.status
    except urllib.error.HTTPError as e:
        status = e.code
    except Exception:
        status = 0
    ms = (time.perf_counter() - inicio) * 1000
    return status, ms


def rodar_etapa(url, chave, usuarios, total, timeout):
    resultados = []
    trava = threading.Lock()
    por_usuario = total // usuarios

    def trabalhador():
        for _ in range(por_usuario):
            r = uma_requisicao(url, chave, timeout)
            with trava:
                resultados.append(r)

    inicio = time.perf_counter()
    threads = [threading.Thread(target=trabalhador) for _ in range(usuarios)]
    for t in threads:
        t.start()
    for t in threads:
        t.join()
    duracao = time.perf_counter() - inicio

    tempos = sorted(ms for _, ms in resultados)
    status_cont = {}
    for s, _ in resultados:
        status_cont[s] = status_cont.get(s, 0) + 1

    def pct(p):
        return tempos[min(len(tempos) - 1, int(len(tempos) * p))]

    respondeu = sum(1 for s, _ in resultados if 0 < s < 500)
    return {
        "usuarios": usuarios,
        "requisicoes": len(resultados),
        "duracao": duracao,
        "rps": len(resultados) / duracao if duracao > 0 else 0,
        "media": statistics.mean(tempos),
        "minimo": tempos[0],
        "maximo": tempos[-1],
        "p50": pct(0.50),
        "p95": pct(0.95),
        "status": status_cont,
        "taxa_resposta": 100.0 * respondeu / len(resultados),
    }


def texto_status(status_cont):
    nomes = {0: "falha de rede", 200: "200 OK", 401: "401 nao autorizado",
             403: "403 proibido"}
    return ", ".join(
        f"{nomes.get(s, str(s))}: {n}" for s, n in sorted(status_cont.items())
    )


def gerar_txt(etapas, url, chave_real, quando):
    linhas = [
        "RELATORIO DE TESTE DE CARGA - Proxima Etapa",
        f"Data: {quando}",
        f"Alvo: GET {url}",
        "Chave anon: " + ("real" if chave_real else "demo (servidor deve responder 401)"),
        "",
    ]
    for e in etapas:
        linhas += [
            f"Etapa: {e['usuarios']} usuarios simultaneos, {e['requisicoes']} requisicoes",
            f"  Duracao total: {e['duracao']:.2f} s | Vazao: {e['rps']:.1f} req/s",
            f"  Tempo medio: {e['media']:.0f} ms | minimo: {e['minimo']:.0f} ms | maximo: {e['maximo']:.0f} ms",
            f"  p50: {e['p50']:.0f} ms | p95: {e['p95']:.0f} ms",
            f"  Taxa de resposta do servidor: {e['taxa_resposta']:.1f}%",
            f"  Status: {texto_status(e['status'])}",
            "",
        ]
    return "\n".join(linhas)


def gerar_html(etapas, url, chave_real, quando):
    maior = max(e["p95"] for e in etapas) or 1
    linhas_tabela = ""
    barras = ""
    for e in etapas:
        linhas_tabela += (
            "<tr>"
            f"<td>{e['usuarios']}</td><td>{e['requisicoes']}</td>"
            f"<td>{e['rps']:.1f}</td><td>{e['media']:.0f}</td>"
            f"<td>{e['p50']:.0f}</td><td>{e['p95']:.0f}</td>"
            f"<td>{e['maximo']:.0f}</td><td>{e['taxa_resposta']:.1f}%</td>"
            f"<td>{escape(texto_status(e['status']))}</td>"
            "</tr>"
        )
        largura = 100.0 * e["p95"] / maior
        barras += (
            f"<div class='linha'><span>{e['usuarios']} usuarios</span>"
            f"<div class='barra' style='width:{largura:.0f}%'>{e['p95']:.0f} ms</div></div>"
        )

    aviso = (
        "Chave anon real usada: as respostas refletem o uso normal do servidor."
        if chave_real
        else "Chave anon demo usada: o servidor responde 401 (nao autorizado). "
             "O teste mede tempo e taxa de resposta do servidor, mas nao a leitura de dados reais."
    )

    return f"""<!DOCTYPE html>
<html lang="pt-BR"><head><meta charset="utf-8">
<title>Relatorio de Teste de Carga</title>
<style>
body{{font-family:Arial,sans-serif;margin:32px;color:#0F172A}}
h1{{margin-bottom:4px}} .sub{{color:#64748B;margin-bottom:24px}}
table{{border-collapse:collapse;width:100%;margin:16px 0}}
th,td{{border:1px solid #CBD5E1;padding:8px;text-align:center;font-size:13px}}
th{{background:#8B5CF6;color:#fff}}
.aviso{{background:#FEF3C7;border:1px solid #D97706;padding:10px;border-radius:6px}}
.linha{{display:flex;align-items:center;margin:6px 0}}
.linha span{{width:110px;font-size:13px}}
.barra{{background:#22C55E;color:#fff;padding:4px 8px;border-radius:4px;font-size:12px}}
</style></head><body>
<h1>Relatorio de Teste de Carga do Servidor</h1>
<div class="sub">Proxima Etapa - App do Aluno | {quando}</div>
<p><b>Alvo:</b> GET {escape(url)}</p>
<p class="aviso">{aviso}</p>
<h2>Resultados por etapa</h2>
<table>
<tr><th>Usuarios simultaneos</th><th>Requisicoes</th><th>Req/s</th><th>Media (ms)</th>
<th>p50 (ms)</th><th>p95 (ms)</th><th>Maximo (ms)</th><th>Taxa de resposta</th><th>Status HTTP</th></tr>
{linhas_tabela}
</table>
<h2>Tempo p95 por etapa</h2>
{barras}
</body></html>"""


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--chave", default="anon_key_demo", help="chave anon do Supabase")
    ap.add_argument("--timeout", type=float, default=15.0)
    args = ap.parse_args()

    url = BASE_URL + ENDPOINT
    chave_real = args.chave != "anon_key_demo"
    quando = datetime.now().strftime("%d/%m/%Y %H:%M")

    etapas = []
    for usuarios, total in ETAPAS:
        print(f"Rodando etapa: {usuarios} usuarios, {total} requisicoes...")
        etapas.append(rodar_etapa(url, args.chave, usuarios, total, args.timeout))

    txt = gerar_txt(etapas, url, chave_real, quando)
    with open("relatorio_carga.txt", "w", encoding="utf-8") as f:
        f.write(txt)
    with open("relatorio_carga.html", "w", encoding="utf-8") as f:
        f.write(gerar_html(etapas, url, chave_real, quando))

    print()
    print(txt)
    print("Arquivos gerados: relatorio_carga.txt e relatorio_carga.html")


if __name__ == "__main__":
    main()