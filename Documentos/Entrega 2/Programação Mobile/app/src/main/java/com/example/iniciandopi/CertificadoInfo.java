package com.example.iniciandopi;

import android.content.Intent;
import android.database.Cursor;
import java.text.Normalizer;
import java.util.Locale;

public class CertificadoInfo {

    public static final String EXTRA_ID = "cert_id";
    public static final String EXTRA_TITULO = "cert_titulo";
    public static final String EXTRA_DATA = "cert_data";
    public static final String EXTRA_HORAS = "cert_horas";

    public final int id;
    public final String titulo;
    public final String data;
    public final String horas;

    public CertificadoInfo(int id, String titulo, String data, String horas) {
        this.id = id;
        this.titulo = vazio(titulo) ? "Lógica de Programação" : titulo.trim();
        this.data = vazio(data) ? "15 Out 2026" : data.trim();
        this.horas = vazio(horas) ? "40h" : horas.trim();
    }

    private static boolean vazio(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static CertificadoInfo doCursor(Cursor c) {
        int iId = c.getColumnIndex("id");
        int iTitulo = c.getColumnIndex("titulo");
        int iData = c.getColumnIndex("data");
        int iHoras = c.getColumnIndex("horas");
        return new CertificadoInfo(
                iId != -1 ? c.getInt(iId) : 1,
                iTitulo != -1 ? c.getString(iTitulo) : null,
                iData != -1 ? c.getString(iData) : null,
                iHoras != -1 ? c.getString(iHoras) : null);
    }

    public static CertificadoInfo doIntent(Intent intent) {
        if (intent == null) return new CertificadoInfo(1, null, null, null);
        return new CertificadoInfo(
                intent.getIntExtra(EXTRA_ID, 1),
                intent.getStringExtra(EXTRA_TITULO),
                intent.getStringExtra(EXTRA_DATA),
                intent.getStringExtra(EXTRA_HORAS));
    }

    public void colocarNoIntent(Intent intent) {
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_TITULO, titulo);
        intent.putExtra(EXTRA_DATA, data);
        intent.putExtra(EXTRA_HORAS, horas);
    }

    public String getHorasPorExtenso() {
        String numeros = horas.replaceAll("[^0-9]", "");
        return numeros.isEmpty() ? horas : numeros + " horas";
    }

    public int getAno() {
        String[] partes = data.split("\\s+");
        String ultimo = partes[partes.length - 1];
        if (ultimo.matches("\\d{4}")) return Integer.parseInt(ultimo);
        return 2026;
    }

    public int getMes() {
        String[] partes = data.split("\\s+");
        if (partes.length >= 2 && partes[1].length() >= 3) {
            String meses = "janfevmarabrmaijunjulagosetoutnovdez";
            int pos = meses.indexOf(partes[1].substring(0, 3).toLowerCase(Locale.ROOT));
            if (pos >= 0 && pos % 3 == 0) return pos / 3 + 1;
        }
        return 10;
    }

    public String getCodigo() {
        return String.format(Locale.ROOT, "PE-%d-%04d", getAno(), id);
    }

    public String getNomeArquivo() {
        String semAcento = Normalizer.normalize(titulo, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String slug = semAcento.replaceAll("[^A-Za-z0-9]+", "_").replaceAll("^_+|_+$", "");
        if (slug.length() > 40) slug = slug.substring(0, 40);
        if (slug.isEmpty()) slug = "Certificado";
        return "Certificado_" + slug + ".pdf";
    }
}