package com.example.iniciandopi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class QrExtrairNomeTest {

    @Test
    public void qrValido_devolveSoONomeDaAula() {
        assertEquals("Aula 04 - Comunicacao",
                QrValidador.extrairNomeAula("PE|AULA|Aula 04 - Comunicacao"));
    }

    @Test
    public void qrComEspacosNasPontas_devolveNomeLimpo() {
        assertEquals("Aula 02", QrValidador.extrairNomeAula("  PE|AULA|  Aula 02  "));
    }

    @Test
    public void qrInvalido_devolveNull() {
        assertNull(QrValidador.extrairNomeAula("https://google.com"));
    }

    @Test
    public void qrNulo_devolveNull() {
        assertNull(QrValidador.extrairNomeAula(null));
    }
}