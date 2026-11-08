package com.example.iniciandopi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.text.Normalizer;
import java.util.Locale;

public class CursosActivity extends AppCompatActivity {

    private static final String PREFS = "cursos_prefs";
    private static final String KEY_INSCRITO_4 = "inscrito_curso4";

    public Button btnInicio, btnCursos, btnAgenda, btnPerfil;
    public Button btnFiltroTodos, btnFiltroInscritos, btnInscreverCurso4;
    public EditText edtBuscarCursos;

    // Listas paralelas: posição 0 = curso 1, posição 1 = curso 2, ...
    private View[] cards;
    private TextView[] nomes;
    private TextView[] descricoes;

    // Mensagem exibida quando nenhum curso passa pelo filtro
    private TextView txtNenhumCurso;

    // Cursos 1, 2 e 3 já são do aluno. O 4 depende do botão "Inscrever-se".
    private final boolean[] inscrito = {true, true, true, false, false, false};
    private boolean apenasInscritos = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cursos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnInicio = findViewById(R.id.btnInicio);
        btnCursos = findViewById(R.id.btnCursos);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnPerfil = findViewById(R.id.btnPerfil);
        btnFiltroTodos = findViewById(R.id.btnFiltroTodos);
        btnFiltroInscritos = findViewById(R.id.btnFiltroInscritos);
        btnInscreverCurso4 = findViewById(R.id.btnInscreverCurso4);
        edtBuscarCursos = findViewById(R.id.edtBuscarCursos);

        cards = new View[]{
                findViewById(R.id.cardCurso1), findViewById(R.id.cardCurso2),
                findViewById(R.id.cardCurso3), findViewById(R.id.cardCurso4),
                findViewById(R.id.cardCurso5), findViewById(R.id.cardCurso6)
        };
        nomes = new TextView[]{
                findViewById(R.id.txtNomeCurso1), findViewById(R.id.txtNomeCurso2),
                findViewById(R.id.txtNomeCurso3), findViewById(R.id.txtNomeCurso4),
                findViewById(R.id.txtNomeCurso5), findViewById(R.id.txtNomeCurso6)
        };
        descricoes = new TextView[]{
                findViewById(R.id.txtDescCurso1), findViewById(R.id.txtDescCurso2),
                findViewById(R.id.txtDescCurso3), findViewById(R.id.txtDescCurso4),
                findViewById(R.id.txtDescCurso5), findViewById(R.id.txtDescCurso6)
        };

        criarMensagemNenhumCurso();

        // Recupera se o aluno já se inscreveu no módulo 4 antes
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        inscrito[3] = prefs.getBoolean(KEY_INSCRITO_4, false);
        if (inscrito[3]) {
            mostrarBotaoComoInscrito();
        }

        btnFiltroTodos.setOnClickListener(v -> {
            apenasInscritos = false;
            atualizarBotoesFiltro();
            aplicarFiltros();
        });

        btnFiltroInscritos.setOnClickListener(v -> {
            apenasInscritos = true;
            atualizarBotoesFiltro();
            aplicarFiltros();
        });

        btnInscreverCurso4.setOnClickListener(v -> realizarInscricaoCurso4());

        // Busca: toda vez que o texto muda, refaz o filtro
        edtBuscarCursos.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) { }
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { }
            @Override public void afterTextChanged(Editable s) { aplicarFiltros(); }
        });

        configurarMenu();
        atualizarBotoesFiltro();
        aplicarFiltros();
    }

    /** Cria o texto "Nenhum curso encontrado" e coloca no topo da lista de cursos. */
    private void criarMensagemNenhumCurso() {
        txtNenhumCurso = new TextView(this);
        txtNenhumCurso.setText("Nenhum curso encontrado");
        txtNenhumCurso.setTextColor(0xFF64748B);
        txtNenhumCurso.setTextSize(14);
        txtNenhumCurso.setGravity(Gravity.CENTER);
        int espaco = (int) (32 * getResources().getDisplayMetrics().density);
        txtNenhumCurso.setPadding(0, espaco, 0, espaco);
        txtNenhumCurso.setVisibility(View.GONE);

        LinearLayout conteudo = findViewById(R.id.conteudo);
        conteudo.addView(txtNenhumCurso, 0, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    /** Decide quais cards aparecem, combinando busca + filtro Todos/Inscritos. */
    private void aplicarFiltros() {
        String busca = normalizar(edtBuscarCursos.getText().toString());
        int visiveis = 0;

        for (int i = 0; i < cards.length; i++) {
            String textoDoCard = normalizar(nomes[i].getText() + " " + descricoes[i].getText());

            boolean passaBusca = busca.isEmpty() || textoDoCard.contains(busca);
            boolean passaFiltro = !apenasInscritos || inscrito[i];
            boolean mostrar = passaBusca && passaFiltro;

            cards[i].setVisibility(mostrar ? View.VISIBLE : View.GONE);
            if (mostrar) visiveis++;
        }

        // Se nenhum card ficou visível, mostra a mensagem
        txtNenhumCurso.setVisibility(visiveis == 0 ? View.VISIBLE : View.GONE);
    }

    /** Tira acentos e deixa minúsculo: "Liderança" vira "lideranca". */
    private String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }

    private void atualizarBotoesFiltro() {
        btnFiltroTodos.setBackgroundResource(apenasInscritos ? R.drawable.bg_option_unselected : R.drawable.bg_btn_green);
        btnFiltroTodos.setTextColor(apenasInscritos ? 0xFF334155 : 0xFFFFFFFF);
        btnFiltroInscritos.setBackgroundResource(apenasInscritos ? R.drawable.bg_btn_green : R.drawable.bg_option_unselected);
        btnFiltroInscritos.setTextColor(apenasInscritos ? 0xFFFFFFFF : 0xFF334155);
    }

    private void realizarInscricaoCurso4() {
        inscrito[3] = true;
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putBoolean(KEY_INSCRITO_4, true).apply();
        mostrarBotaoComoInscrito();
        aplicarFiltros();
        Toast.makeText(this, "Inscrição realizada com sucesso! Módulo 4 ativado na sua trilha.", Toast.LENGTH_LONG).show();
    }

    private void mostrarBotaoComoInscrito() {
        btnInscreverCurso4.setText("Inscrito • Aulas iniciam em breve");
        btnInscreverCurso4.setBackgroundResource(R.drawable.bg_banner_yellow);
        btnInscreverCurso4.setTextColor(0xFFD97706);
        btnInscreverCurso4.setEnabled(false);
    }

    private void configurarMenu() {
        btnInicio.setOnClickListener(v -> abrirTela(MainActivity.class));
        btnAgenda.setOnClickListener(v -> abrirTela(Agenda.class));
        btnPerfil.setOnClickListener(v -> abrirTela(Perfil.class));
    }

    private void abrirTela(Class<?> destino) {
        startActivity(new Intent(CursosActivity.this, destino));
        overridePendingTransition(0, 0);
        finish();
    }
}