package com.example.iniciandopi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import android.content.Context;
import android.database.Cursor;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class BancoIntegracaoTest {

    private Context contexto;
    private DatabaseHelper db;

    @Before
    public void preparar() {
        contexto = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // apaga o banco anterior para comecar sempre limpo
        contexto.deleteDatabase("ProximaEtapa.db");
        db = new DatabaseHelper(contexto);
    }

    @After
    public void limpar() {
        db.close();
        contexto.deleteDatabase("ProximaEtapa.db");
    }

    // ---------- Integracao 1: cadastro + login ----------

    @Test
    public void cadastroELogin_comMesmasCredenciais_encontraOUsuario() {
        boolean cadastrou = db.cadastrarUsuario("ana@teste.com", "1234", "Ana Souza", "2026.01.0001", "E.E. Teste");
        assertTrue("Cadastro deveria funcionar", cadastrou);

        Cursor c = db.buscarUsuarioPorEmailESenha("ana@teste.com", "1234");
        assertTrue("Login deveria encontrar o usuario", c.moveToFirst());
        assertEquals("Ana Souza", c.getString(c.getColumnIndex("nome")));
        c.close();
    }

    @Test
    public void login_comSenhaErrada_naoEncontraUsuario() {
        db.cadastrarUsuario("ana@teste.com", "1234", "Ana Souza", "2026.01.0001", "E.E. Teste");

        Cursor c = db.buscarUsuarioPorEmailESenha("ana@teste.com", "senhaErrada");
        assertFalse("Senha errada nao pode logar", c.moveToFirst());
        c.close();
    }

    @Test
    public void cadastro_comEmailRepetido_ehBloqueado() {
        assertTrue(db.cadastrarUsuario("ana@teste.com", "1234", "Ana", "1", "E.E. Teste"));
        assertTrue(db.emailJaCadastrado("ana@teste.com"));
        assertFalse("E-mail duplicado deve ser recusado",
                db.cadastrarUsuario("ana@teste.com", "9999", "Outra Ana", "2", "E.E. Teste"));
    }

    // ---------- Integracao 2: presenca + bloqueio de duplicidade ----------

    @Test
    public void presenca_salvaEDepoisExiste() {
        assertFalse(db.existePresenca("Aula 04", "08/11/2026"));

        boolean salvou = db.salvarPresencaLocal("Aula 04", "08/11/2026 às 14:00");
        assertTrue(salvou);

        assertTrue("Presenca salva deve ser encontrada", db.existePresenca("Aula 04", "08/11/2026"));
    }

    @Test
    public void presenca_deOutraAulaOuOutroDia_naoContaComoDuplicada() {
        db.salvarPresencaLocal("Aula 04", "08/11/2026 às 14:00");

        assertFalse(db.existePresenca("Aula 05", "08/11/2026"));
        assertFalse(db.existePresenca("Aula 04", "09/11/2026"));
    }
}