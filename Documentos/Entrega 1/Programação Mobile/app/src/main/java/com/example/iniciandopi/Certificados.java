package com.example.iniciandopi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Certificados extends AppCompatActivity {

    public Button btnInicio, btnCursos, btnAgenda, btnCertificados, btnPerfil;
    public Button btnVisualizar1, btnPdf1, btnVisualizar2, btnPdf2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_certificados);

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

        btnVisualizar1 = findViewById(R.id.btnVisualizar1);
        btnPdf1 = findViewById(R.id.btnPdf1);
        btnVisualizar2 = findViewById(R.id.btnVisualizar2);
        btnPdf2 = findViewById(R.id.btnPdf2);

        if (btnInicio != null) {
            btnInicio.setOnClickListener(v -> {
                Intent intent = new Intent(Certificados.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnCursos != null) {
            btnCursos.setOnClickListener(v -> {
                Intent intent = new Intent(Certificados.this, CursosActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnAgenda != null) {
            btnAgenda.setOnClickListener(v -> {
                Intent intent = new Intent(Certificados.this, Agenda.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnPerfil != null) {
            btnPerfil.setOnClickListener(v -> {
                Intent intent = new Intent(Certificados.this, Perfil.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnVisualizar1 != null) {
            btnVisualizar1.setOnClickListener(v -> {
                Intent intent = new Intent(Certificados.this, Visualizar.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        if (btnPdf1 != null) {
            btnPdf1.setOnClickListener(v -> {
                Intent intent = new Intent(Certificados.this, BaixarPDF.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        if (btnVisualizar2 != null) {
            btnVisualizar2.setOnClickListener(v -> {
                Intent intent = new Intent(Certificados.this, Visualizar.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        if (btnPdf2 != null) {
            btnPdf2.setOnClickListener(v -> {
                Intent intent = new Intent(Certificados.this, BaixarPDF.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }
    }
}
