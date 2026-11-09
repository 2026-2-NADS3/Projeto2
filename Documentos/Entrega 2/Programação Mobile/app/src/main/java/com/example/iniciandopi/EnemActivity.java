package com.example.iniciandopi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
            cardEnemCalculadora.setOnClickListener(v -> avisarEmBreve("Calculadora de Nota de Corte"));
        }

        if (cardEnemRedacao != null) {
            cardEnemRedacao.setOnClickListener(v -> avisarEmBreve("Dicas de Redação"));
        }

        if (cardEnemSimulado != null) {
            cardEnemSimulado.setOnClickListener(v -> avisarEmBreve("Simulado ENEM"));
        }
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