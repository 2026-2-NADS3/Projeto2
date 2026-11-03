package com.example.iniciandopi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ComunicadosActivity extends AppCompatActivity {

    public Button btnInicio, btnCursos, btnAgenda, btnPerfil;

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

        if (btnInicio != null) {
            btnInicio.setOnClickListener(v -> {
                Intent intent = new Intent(ComunicadosActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnCursos != null) {
            btnCursos.setOnClickListener(v -> {
                Intent intent = new Intent(ComunicadosActivity.this, CursosActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnAgenda != null) {
            btnAgenda.setOnClickListener(v -> {
                Intent intent = new Intent(ComunicadosActivity.this, Agenda.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnPerfil != null) {
            btnPerfil.setOnClickListener(v -> {
                Intent intent = new Intent(ComunicadosActivity.this, Perfil.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }
    }
}
