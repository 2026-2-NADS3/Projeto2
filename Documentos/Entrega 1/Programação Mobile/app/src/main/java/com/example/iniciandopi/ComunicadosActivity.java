package com.example.iniciandopi;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.Set;

public class ComunicadosActivity extends AppCompatActivity {

    public Button btnInicio, btnCursos, btnAgenda, btnPerfil, btnVoltarComunicados;
    public TextView txtMarcarTodos;
    public LinearLayout containerAvisos;

    /** A Home usa esta função para mostrar o número no sino. */
    public static int contarNaoLidos(Context ctx) {
        return PrazosRepositorio.contarNaoLidos(ctx);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comunicados);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnInicio = findViewById(R.id.btnInicio);
        btnCursos = findViewById(R.id.btnCursos);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnPerfil = findViewById(R.id.btnPerfil);
        btnVoltarComunicados = findViewById(R.id.btnVoltarComunicados);
        txtMarcarTodos = findViewById(R.id.txtMarcarTodos);
        containerAvisos = findViewById(R.id.containerAvisos);

        if (btnVoltarComunicados != null) {
            btnVoltarComunicados.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        if (btnInicio != null) btnInicio.setOnClickListener(v -> irParaHome());
        if (btnCursos != null) btnCursos.setOnClickListener(v -> abrirTela(CursosActivity.class));
        if (btnAgenda != null) btnAgenda.setOnClickListener(v -> abrirTela(Agenda.class));
        if (btnPerfil != null) btnPerfil.setOnClickListener(v -> abrirTela(Perfil.class));

        if (txtMarcarTodos != null) {
            txtMarcarTodos.setOnClickListener(v -> {
                PrazosRepositorio.marcarTodosComoLidos(this);
                recarregarAvisos();
            });
        }

        carregarAvisos();
    }

    // ---------- Lista de avisos ----------

    private void recarregarAvisos() {
        if (containerAvisos == null) return;
        containerAvisos.removeAllViews();
        carregarAvisos();
    }

    private void carregarAvisos() {
        if (containerAvisos == null) return;

        Set<String> lidos = PrazosRepositorio.obterLidos(this);
        for (PrazosRepositorio.Prazo prazo : PrazosRepositorio.listar()) {
            boolean novo = !lidos.contains(prazo.id) && !prazo.encerrado();
            containerAvisos.addView(criarCard(prazo, novo));
        }
    }

    private LinearLayout criarCard(PrazosRepositorio.Prazo prazo, boolean novo) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_card_white);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));
        LinearLayout.LayoutParams paramsCard = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsCard.setMargins(0, 0, 0, dp(16));
        card.setLayoutParams(paramsCard);
        card.setClickable(true);
        card.setFocusable(true);
        if (prazo.encerrado()) card.setAlpha(0.55f);

        // Linha de cima: categoria, "● NOVO" e prazo
        LinearLayout topo = new LinearLayout(this);
        topo.setOrientation(LinearLayout.HORIZONTAL);
        topo.setGravity(Gravity.CENTER_VERTICAL);

        TextView tag = new TextView(this);
        tag.setText(prazo.categoria);
        tag.setTextColor(prazo.cor);
        tag.setTextSize(11);
        tag.setTypeface(null, Typeface.BOLD);
        topo.addView(tag, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        if (novo) {
            TextView txtNovo = new TextView(this);
            txtNovo.setText("● NOVO");
            txtNovo.setTextColor(0xFFEF4444);
            txtNovo.setTextSize(10);
            txtNovo.setTypeface(null, Typeface.BOLD);
            txtNovo.setPadding(0, 0, dp(10), 0);
            topo.addView(txtNovo);
        }

        TextView rotulo = new TextView(this);
        rotulo.setText(prazo.rotuloPrazo());
        rotulo.setTextColor(prazo.corRotulo());
        rotulo.setTextSize(11);
        rotulo.setTypeface(null, Typeface.BOLD);
        topo.addView(rotulo);

        TextView titulo = new TextView(this);
        titulo.setText(prazo.titulo);
        titulo.setTextColor(0xFF0F172A);
        titulo.setTextSize(15);
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setPadding(0, dp(8), 0, 0);

        TextView descricao = new TextView(this);
        descricao.setText(prazo.descricao);
        descricao.setTextColor(0xFF64748B);
        descricao.setTextSize(12);
        descricao.setPadding(0, dp(4), 0, 0);

        card.addView(topo);
        card.addView(titulo);
        card.addView(descricao);

        if (prazo.demo) {
            TextView demo = new TextView(this);
            demo.setText("Data de demonstração");
            demo.setTextColor(0xFF94A3B8);
            demo.setTextSize(10);
            demo.setTypeface(null, Typeface.ITALIC);
            demo.setPadding(0, dp(6), 0, 0);
            card.addView(demo);
        }

        TextView link = new TextView(this);
        link.setText("Ver no site oficial ›");
        link.setTextColor(0xFF8B5CF6);
        link.setTextSize(12);
        link.setTypeface(null, Typeface.BOLD);
        link.setPadding(0, dp(10), 0, 0);
        card.addView(link);

        // Tocar no card: marca como lido
        card.setOnClickListener(v -> {
            PrazosRepositorio.marcarComoLido(this, prazo.id);
            recarregarAvisos();
        });

        // Tocar no link: marca como lido e abre o site
        link.setOnClickListener(v -> {
            PrazosRepositorio.marcarComoLido(this, prazo.id);
            abrirSite(prazo.link);
            recarregarAvisos();
        });

        return card;
    }

    private void abrirSite(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Não foi possível abrir o navegador.", Toast.LENGTH_LONG).show();
        }
    }

    // ---------- Navegação ----------

    private void irParaHome() {
        Intent intent = new Intent(ComunicadosActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void abrirTela(Class<?> destino) {
        Intent intent = new Intent(ComunicadosActivity.this, destino);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private int dp(int valor) {
        return Math.round(valor * getResources().getDisplayMetrics().density);
    }
}