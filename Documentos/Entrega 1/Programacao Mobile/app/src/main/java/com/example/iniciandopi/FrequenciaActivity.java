package com.example.iniciandopi;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FrequenciaActivity extends AppCompatActivity {

    public Button btnVoltarFrequencia;
    public LinearLayout containerHistoricoPresencas;
    public TextView txtTotalPresencas;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_frequencia);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        btnVoltarFrequencia = findViewById(R.id.btnVoltarFrequencia);
        containerHistoricoPresencas = findViewById(R.id.containerHistoricoPresencas);
        txtTotalPresencas = findViewById(R.id.txtTotalPresencas);

        if (btnVoltarFrequencia != null) {
            btnVoltarFrequencia.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        carregarHistoricoPresencas();
    }

    private void carregarHistoricoPresencas() {
        if (dbHelper == null || containerHistoricoPresencas == null) return;

        Cursor cursor = dbHelper.obterPresencas();
        int total = 0;

        if (cursor != null) {
            total = cursor.getCount();
            while (cursor.moveToNext()) {
                int indexAula = cursor.getColumnIndex("aula");
                int indexData = cursor.getColumnIndex("data");

                String aula = indexAula != -1 ? cursor.getString(indexAula) : "Aula Geral";
                String data = indexData != -1 ? cursor.getString(indexData) : "Data não informada";

                adicionarCardPresenca(aula, data);
            }
            cursor.close();
        }

        if (txtTotalPresencas != null) {
            txtTotalPresencas.setText(total + " Aulas");
        }
    }

    private void adicionarCardPresenca(String aula, String dataHora) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setBackgroundResource(R.drawable.bg_card_white);
        itemLayout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dpToPx(12));
        itemLayout.setLayoutParams(params);

        TextView tvAula = new TextView(this);
        tvAula.setText(aula);
        tvAula.setTextColor(0xFF0F172A);
        tvAula.setTextSize(15);
        tvAula.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvData = new TextView(this);
        tvData.setText(dataHora + " • Confirmado");
        tvData.setTextColor(0xFF15803D);
        tvData.setTextSize(12);
        tvData.setPadding(0, dpToPx(4), 0, 0);

        itemLayout.addView(tvAula);
        itemLayout.addView(tvData);

        containerHistoricoPresencas.addView(itemLayout);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
