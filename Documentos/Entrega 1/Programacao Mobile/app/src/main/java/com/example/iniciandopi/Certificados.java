package com.example.iniciandopi;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import java.util.List;

public class Certificados extends AppCompatActivity {

    public Button btnInicio, btnCursos, btnAgenda, btnCertificados, btnPerfil;
    public Button btnVisualizar1, btnPdf1, btnVisualizar2, btnPdf2;
    private DatabaseHelper dbHelper;

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

        dbHelper = new DatabaseHelper(this);

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

        carregarCertificados();
    }

    private void carregarCertificados() {
        List<CertificadoInfo> lista = new ArrayList<>();
        Cursor cursor = dbHelper.obterCertificadosLocais();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                lista.add(CertificadoInfo.doCursor(cursor));
            }
            cursor.close();
        }

        preencherCard(lista.size() > 0 ? lista.get(0) : null,
                findViewById(R.id.cardCertificado1),
                (TextView) findViewById(R.id.txtCurso1),
                (TextView) findViewById(R.id.txtDetCertificado1),
                btnVisualizar1, btnPdf1);

        preencherCard(lista.size() > 1 ? lista.get(1) : null,
                findViewById(R.id.cardCertificado2),
                (TextView) findViewById(R.id.txtCurso2),
                (TextView) findViewById(R.id.txtDetCertificado2),
                btnVisualizar2, btnPdf2);
    }

    private void preencherCard(CertificadoInfo info, View card, TextView txtTitulo,
                               TextView txtDetalhe, Button btnVisualizar, Button btnPdf) {
        if (card == null) return;

        if (info == null) {
            card.setVisibility(View.GONE);
            return;
        }

        card.setVisibility(View.VISIBLE);
        if (txtTitulo != null) txtTitulo.setText(info.titulo);
        if (txtDetalhe != null) txtDetalhe.setText("Concluído em " + info.data + " • " + info.horas);

        if (btnVisualizar != null) {
            btnVisualizar.setOnClickListener(v -> abrirTela(Visualizar.class, info));
        }
        if (btnPdf != null) {
            btnPdf.setOnClickListener(v -> abrirTela(BaixarPDF.class, info));
        }
    }

    private void abrirTela(Class<?> destino, CertificadoInfo info) {
        Intent intent = new Intent(Certificados.this, destino);
        info.colocarNoIntent(intent);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}