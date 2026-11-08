package com.example.iniciandopi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CertificadoInfoTest {

    @Test
    public void horasPorExtenso_converte40hPara40Horas() {
        CertificadoInfo info = new CertificadoInfo(1, "Orientacao", "15 Out 2026", "40h");
        assertEquals("40 horas", info.getHorasPorExtenso());
    }

    @Test
    public void codigo_temFormatoPE_ano_numeroComQuatroDigitos() {
        CertificadoInfo info = new CertificadoInfo(1, "Orientacao", "15 Out 2026", "40h");
        assertEquals("PE-2026-0001", info.getCodigo());
    }

    @Test
    public void nomeArquivo_removeAcentosEPoeUnderline() {
        CertificadoInfo info = new CertificadoInfo(1, "Orientação Vocacional & Carreira", "15 Out 2026", "40h");
        assertEquals("Certificado_Orientacao_Vocacional_Carreira.pdf", info.getNomeArquivo());
    }

    @Test
    public void mes_outubroRetorna10() {
        CertificadoInfo info = new CertificadoInfo(1, "X", "15 Out 2026", "40h");
        assertEquals(10, info.getMes());
    }

    @Test
    public void camposVazios_usamValoresPadrao() {
        CertificadoInfo info = new CertificadoInfo(1, "", null, "  ");
        assertEquals("Lógica de Programação", info.titulo);
        assertEquals("15 Out 2026", info.data);
        assertEquals("40h", info.horas);
    }
}