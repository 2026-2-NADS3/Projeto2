package com.example.iniciandopi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.junit.Test;

public class PrazoTest {

    private String dataIsoMais(int dias) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, dias);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(c.getTime());
    }

    private PrazosRepositorio.Prazo prazoEm(int dias) {
        return new PrazosRepositorio.Prazo("t", "CAT", 0, "Titulo", "Desc",
                dataIsoMais(dias), "http://x", false);
    }

    @Test
    public void prazoDeHoje_mostraHoje() {
        assertTrue(prazoEm(0).rotuloPrazo().endsWith("HOJE"));
    }

    @Test
    public void prazoDeAmanha_mostraAmanha() {
        assertTrue(prazoEm(1).rotuloPrazo().endsWith("AMANHÃ"));
    }

    @Test
    public void prazoEm7Dias_mostraEm7Dias() {
        assertTrue(prazoEm(7).rotuloPrazo().endsWith("EM 7 DIAS"));
    }

    @Test
    public void prazoPassado_estaEncerrado() {
        PrazosRepositorio.Prazo p = prazoEm(-3);
        assertTrue(p.encerrado());
        assertTrue(p.rotuloPrazo().endsWith("ENCERRADO"));
    }

    @Test
    public void prazoFuturo_naoEstaEncerrado() {
        assertFalse(prazoEm(5).encerrado());
    }

    @Test
    public void dataCurta_formataDiaBarraMes() {
        PrazosRepositorio.Prazo p = new PrazosRepositorio.Prazo("t", "C", 0, "T", "D",
                "2026-11-08", "http://x", false);
        assertEquals("08/11", p.dataCurta());
    }

    @Test
    public void semData_mostraSemData() {
        PrazosRepositorio.Prazo p = new PrazosRepositorio.Prazo("t", "C", 0, "T", "D",
                null, "http://x", false);
        assertEquals("SEM DATA", p.rotuloPrazo());
    }
}