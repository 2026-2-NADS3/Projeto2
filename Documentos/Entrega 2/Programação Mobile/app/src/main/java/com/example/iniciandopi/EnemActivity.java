package com.example.iniciandopi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.text.InputType;
import android.app.AlertDialog;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.Locale;

public class EnemActivity extends AppCompatActivity {

    public Button btnVoltarEnem;
    public View cardEnemCalculadora, cardEnemRedacao, cardEnemSimulado;
    public TextView txtEnemProximaProva, txtEnemPrazo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enem);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int margem = (int) (24 * getResources().getDisplayMetrics().density);
            v.setPadding(
                    systemBars.left + margem,
                    systemBars.top + margem,
                    systemBars.right + margem,
                    systemBars.bottom + margem);
            return insets;
        });

        btnVoltarEnem = findViewById(R.id.btnVoltarEnem);
        cardEnemCalculadora = findViewById(R.id.cardEnemCalculadora);
        cardEnemRedacao = findViewById(R.id.cardEnemRedacao);
        cardEnemSimulado = findViewById(R.id.cardEnemSimulado);
        txtEnemProximaProva = findViewById(R.id.txtEnemProximaProva);
        txtEnemPrazo = findViewById(R.id.txtEnemPrazo);

        carregarProximaProva();

        if (btnVoltarEnem != null) {
            btnVoltarEnem.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        if (cardEnemCalculadora != null) {
            cardEnemCalculadora.setOnClickListener(v -> abrirCalculadoraNotaDeCorte());
        }

        if (cardEnemRedacao != null) {
            cardEnemRedacao.setOnClickListener(v -> abrirDicasRedacao());
        }

        if (cardEnemSimulado != null) {
            cardEnemSimulado.setOnClickListener(v -> avisarEmBreve("Simulado ENEM"));
        }
    }


    private void abrirCalculadoraNotaDeCorte() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("📊 Calculadora de Nota de Corte");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int p = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(p, p, p, p);

        final android.widget.EditText edtRedacao = new android.widget.EditText(this);
        edtRedacao.setHint("Nota da Redação (0-1000)");
        edtRedacao.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(edtRedacao);

        final android.widget.EditText edtMatematica = new android.widget.EditText(this);
        edtMatematica.setHint("Nota de Matemática (0-1000)");
        edtMatematica.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(edtMatematica);

        final android.widget.EditText edtLinguagens = new android.widget.EditText(this);
        edtLinguagens.setHint("Nota de Linguagens (0-1000)");
        edtLinguagens.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(edtLinguagens);

        final android.widget.EditText edtHumanas = new android.widget.EditText(this);
        edtHumanas.setHint("Nota de Ciências Humanas (0-1000)");
        edtHumanas.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(edtHumanas);

        final android.widget.EditText edtNatureza = new android.widget.EditText(this);
        edtNatureza.setHint("Nota de Ciências da Natureza (0-1000)");
        edtNatureza.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(edtNatureza);

        builder.setView(layout);

        builder.setPositiveButton("Calcular Média", (dialog, which) -> {
            try {
                double red = edtRedacao.getText().toString().isEmpty() ? 0 : Double.parseDouble(edtRedacao.getText().toString());
                double mat = edtMatematica.getText().toString().isEmpty() ? 0 : Double.parseDouble(edtMatematica.getText().toString());
                double ling = edtLinguagens.getText().toString().isEmpty() ? 0 : Double.parseDouble(edtLinguagens.getText().toString());
                double hum = edtHumanas.getText().toString().isEmpty() ? 0 : Double.parseDouble(edtHumanas.getText().toString());
                double nat = edtNatureza.getText().toString().isEmpty() ? 0 : Double.parseDouble(edtNatureza.getText().toString());

                double mediaSimples = (red + mat + ling + hum + nat) / 5.0;

                String resultado = String.format(Locale.ROOT, "Média Simples: %.1f\n\nEstimativa de Cursos:\n• Medicina: %s (>800)\n• Engenharia / Direito: %s (>700)\n• Administração / Outros: %s (>600)",
                        mediaSimples,
                        mediaSimples >= 800 ? "Compatível ✅" : "Abaixo da média ❌",
                        mediaSimples >= 700 ? "Compatível ✅" : "Abaixo da média ❌",
                        mediaSimples >= 600 ? "Compatível ✅" : "Abaixo da média ❌");

                new AlertDialog.Builder(this)
                        .setTitle("Resultado do Cálculo")
                        .setMessage(resultado)
                        .setPositiveButton("OK", null)
                        .show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Por favor, insira números válidos.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void abrirDicasRedacao() {
        String dicas = "📝 GUIA DE REDAÇÃO NOTA 1000\n\n"
                + "1️⃣ A 5ª Competência (Proposta de Intervenção):\n"
                + "Deve conter obrigatoriamente 5 elementos:\n"
                + "• Agente (Quem fará?)\n"
                + "• Ação (O que será feito?)\n"
                + "• Meio/Modo (Como será feito?)\n"
                + "• Efeito (Para que fim?)\n"
                + "• Detalhamento (Explicitação de um dos elementos).\n\n"
                + "2️⃣ Estrutura Textual:\n"
                + "• Introdução: Contextualização (Repertório) + Tese + 2 Argumentos.\n"
                + "• D1 e D2: Tópico frasal + Repertório + Argumentação + Conclusão do parágrafo.\n"
                + "• Conclusão: Retomada da tese + 5 elementos da intervenção.\n\n"
                + "3️⃣ Repertório Sociocultural:\n"
                + "Use filósofos, dados estatísticos, leis (Constituição), literatura ou filmes que sejam legitimados e produtivos.";

        new AlertDialog.Builder(this)
                .setTitle("💡 Dicas de Redação Nota 1000")
                .setMessage(dicas)
                .setPositiveButton("Entendido!", null)
                .show();
    }


    private void carregarProximaProva() {
        if (txtEnemProximaProva == null || txtEnemPrazo == null) return;

        for (PrazosRepositorio.Prazo prazo : PrazosRepositorio.listar()) {
            if (prazo.id.startsWith("enem") && !prazo.encerrado()) {
                txtEnemProximaProva.setText(prazo.titulo);
                txtEnemPrazo.setText(prazo.rotuloPrazo());
                txtEnemPrazo.setTextColor(prazo.corRotulo());
                return;
            }
        }

        txtEnemProximaProva.setText("As provas do Enem 2026 já foram aplicadas");
        txtEnemPrazo.setText("Acompanhe os resultados no site do Inep");
    }

    private void avisarEmBreve(String nomeTela) {
        Toast.makeText(this, nomeTela + " em breve!", Toast.LENGTH_SHORT).show();
    }
}