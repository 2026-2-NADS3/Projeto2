package com.example.iniciandopi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class QrValidadorTest {

    @Test
    public void testaCasosDoArquivoTxt() throws Exception {
        InputStream in = getClass().getClassLoader().getResourceAsStream("casos_qr.txt");
        assertTrue("Arquivo casos_qr.txt nao encontrado", in != null);

        BufferedReader leitor = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8));

        List<String> linhasRelatorio = new ArrayList<>();
        int passou = 0;
        int falhou = 0;
        int numero = 0;
        String linha;

        while ((linha = leitor.readLine()) != null) {
            if (linha.trim().isEmpty()) continue;
            numero++;

            // separa pelo ULTIMO ';' (o texto do QR vem antes, o esperado depois)
            int pos = linha.lastIndexOf(';');
            String entrada = linha.substring(0, pos);
            String esperado = linha.substring(pos + 1).trim();

            boolean valido = QrValidador.ehValido(entrada);
            String obtido = valido ? "valido" : "invalido";

            if (obtido.equals(esperado)) {
                passou++;
                linhasRelatorio.add("Caso " + numero + " | PASSOU | entrada=[" + entrada
                        + "] esperado=" + esperado + " obtido=" + obtido);
            } else {
                falhou++;
                linhasRelatorio.add("Caso " + numero + " | FALHOU | entrada=[" + entrada
                        + "] esperado=" + esperado + " obtido=" + obtido);
            }
        }
        leitor.close();

        escreverRelatorio(linhasRelatorio, passou, falhou);

        assertEquals("Casos que falharam (veja relatorio_qr.txt)", 0, falhou);
    }

    private void escreverRelatorio(List<String> linhas, int passou, int falhou) throws Exception {
        File pasta = new File("build/relatorios");
        pasta.mkdirs();
        File arquivo = new File(pasta, "relatorio_qr.txt");

        try (PrintWriter out = new PrintWriter(new FileWriter(arquivo))) {
            out.println("RELATORIO DE TESTE - QrValidador");
            out.println("Total: " + (passou + falhou)
                    + " | Passou: " + passou + " | Falhou: " + falhou);
            out.println("----------------------------------------");
            for (String l : linhas) out.println(l);
        }
    }
}