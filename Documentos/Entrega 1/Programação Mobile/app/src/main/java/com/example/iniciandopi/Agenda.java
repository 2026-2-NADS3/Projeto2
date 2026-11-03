package com.example.iniciandopi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Agenda extends AppCompatActivity {

    private Button btnInicio, btnCursos, btnAgenda, btnCertificados, btnPerfil;
    private TextView txtLabelHoje;
    private TextView txtSiglaDia1, txtNumDia1, txtSiglaDia2, txtNumDia2, txtSiglaDia3, txtNumDia3, txtSiglaDia4, txtNumDia4, txtSiglaDia5, txtNumDia5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agenda);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnInicio = findViewById(R.id.btnInicio);
        btnCursos = findViewById(R.id.btnCursos);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnCertificados = findViewById(R.id.btnCertificados);
        btnPerfil = findViewById(R.id.btnPerfil);

        txtLabelHoje = findViewById(R.id.txtLabelHoje);
        txtSiglaDia1 = findViewById(R.id.txtSiglaDia1);
        txtNumDia1 = findViewById(R.id.txtNumDia1);
        txtSiglaDia2 = findViewById(R.id.txtSiglaDia2);
        txtNumDia2 = findViewById(R.id.txtNumDia2);
        txtSiglaDia3 = findViewById(R.id.txtSiglaDia3);
        txtNumDia3 = findViewById(R.id.txtNumDia3);
        txtSiglaDia4 = findViewById(R.id.txtSiglaDia4);
        txtNumDia4 = findViewById(R.id.txtNumDia4);
        txtSiglaDia5 = findViewById(R.id.txtSiglaDia5);
        txtNumDia5 = findViewById(R.id.txtNumDia5);

        atualizarDatasDinamicas();

        if (btnInicio != null) {
            btnInicio.setOnClickListener(v -> {
                Intent intent = new Intent(Agenda.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnCursos != null) {
            btnCursos.setOnClickListener(v -> {
                Intent intent = new Intent(Agenda.this, CursosActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnCertificados != null) {
            btnCertificados.setOnClickListener(v -> {
                Intent intent = new Intent(Agenda.this, Certificados.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnPerfil != null) {
            btnPerfil.setOnClickListener(v -> {
                Intent intent = new Intent(Agenda.this, Perfil.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }
    }

    private void atualizarDatasDinamicas() {
        Calendar cal = Calendar.getInstance();
        Locale ptBr = new Locale("pt", "BR");

        SimpleDateFormat dfSigla = new SimpleDateFormat("EEE", ptBr);
        SimpleDateFormat dfDiaNum = new SimpleDateFormat("dd", ptBr);
        SimpleDateFormat dfLabel = new SimpleDateFormat("'HOJE ('EEE, dd 'DE' MMMM')'", ptBr);

        if (txtLabelHoje != null) {
            txtLabelHoje.setText(dfLabel.format(cal.getTime()).toUpperCase());
        }

        TextView[] siglas = {txtSiglaDia1, txtSiglaDia2, txtSiglaDia3, txtSiglaDia4, txtSiglaDia5};
        TextView[] numeros = {txtNumDia1, txtNumDia2, txtNumDia3, txtNumDia4, txtNumDia5};

        for (int i = 0; i < 5; i++) {
            if (siglas[i] != null) {
                String sigla = dfSigla.format(cal.getTime()).toUpperCase().replace(".", "");
                if (sigla.length() > 3) sigla = sigla.substring(0, 3);
                siglas[i].setText(sigla);
            }
            if (numeros[i] != null) {
                numeros[i].setText(dfDiaNum.format(cal.getTime()));
            }
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
    }
}
