package com.example.iniciandopi;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Perfil extends AppCompatActivity {

    public Button btnInicio, btnCursos, btnAgenda, btnCertificados, btnPerfil;
    public Button btnMenuCarteira, btnMenuFrequencia, btnMenuCertificados, btnMenuTeste, btnMenuEnem, btnMenuComunicados, btnMenuChat, btnMenuSair;
    public TextView txtNomePerfil, txtSubPerfil, txtInicialPerfil;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

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

        btnMenuCarteira = findViewById(R.id.btnMenuCarteira);
        btnMenuFrequencia = findViewById(R.id.btnMenuFrequencia);
        btnMenuCertificados = findViewById(R.id.btnMenuCertificados);
        btnMenuTeste = findViewById(R.id.btnMenuTeste);
        btnMenuEnem = findViewById(R.id.btnMenuEnem);
        btnMenuComunicados = findViewById(R.id.btnMenuComunicados);
        btnMenuChat = findViewById(R.id.btnMenuChat);
        btnMenuSair = findViewById(R.id.btnMenuSair);

        txtNomePerfil = findViewById(R.id.txtNomePerfil);
        txtSubPerfil = findViewById(R.id.txtSubPerfil);
        txtInicialPerfil = findViewById(R.id.txtInicialPerfil);

        carregarDadosPerfil();

        if (btnInicio != null) {
            btnInicio.setOnClickListener(v -> {
                Intent intent = new Intent(Perfil.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnCursos != null) {
            btnCursos.setOnClickListener(v -> {
                Intent intent = new Intent(Perfil.this, CursosActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnAgenda != null) {
            btnAgenda.setOnClickListener(v -> {
                Intent intent = new Intent(Perfil.this, Agenda.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnMenuCarteira != null) {
            btnMenuCarteira.setOnClickListener(v -> {
                Intent intent = new Intent(Perfil.this, CarteiraActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        if (btnMenuFrequencia != null) {
            btnMenuFrequencia.setOnClickListener(v -> {
                Intent intent = new Intent(Perfil.this, FrequenciaActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        if (btnMenuCertificados != null) {
            btnMenuCertificados.setOnClickListener(v -> {
                Intent intent = new Intent(Perfil.this, Certificados.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        if (btnMenuTeste != null) {
            btnMenuTeste.setOnClickListener(v -> {
                Intent intent = new Intent(Perfil.this, TestePerfilActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        if (btnMenuEnem != null) {
            btnMenuEnem.setOnClickListener(v -> {
                Intent intent = new Intent(Perfil.this, EnemActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        if (btnMenuComunicados != null) {
            btnMenuComunicados.setOnClickListener(v -> {
                Intent intent = new Intent(Perfil.this, ComunicadosActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        if (btnMenuChat != null) {
            btnMenuChat.setOnClickListener(v -> {
                Intent intent = new Intent(Perfil.this, ChatActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        if (btnMenuSair != null) {
            btnMenuSair.setOnClickListener(v -> {
                if (dbHelper != null) {
                    dbHelper.limparSessao();
                }
                Intent intent = new Intent(Perfil.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }
    }

    private void carregarDadosPerfil() {
        if (dbHelper == null) return;

        Cursor cursor = dbHelper.obterAlunoLogado();
        if (cursor != null && cursor.moveToFirst()) {
            int indexNome = cursor.getColumnIndex("nome");
            int indexRa = cursor.getColumnIndex("ra");
            int indexEscola = cursor.getColumnIndex("escola");

            if (indexNome != -1) {
                String nome = cursor.getString(indexNome);
                if (nome != null && !nome.isEmpty()) {
                    if (txtNomePerfil != null) {
                        txtNomePerfil.setText(nome);
                    }

                    if (txtInicialPerfil != null) {
                        String[] partes = nome.split(" ");
                        String i1 = partes.length > 0 && !partes[0].isEmpty() ? partes[0].substring(0, 1).toUpperCase() : "A";
                        String i2 = partes.length > 1 && !partes[1].isEmpty() ? partes[1].substring(0, 1).toUpperCase() : "";
                        txtInicialPerfil.setText(i1 + i2);
                    }
                }
            }

            String ra = indexRa != -1 ? cursor.getString(indexRa) : "2026.03.8841";
            String escola = indexEscola != -1 ? cursor.getString(indexEscola) : "E.E. Maria José";
            if (escola == null || escola.isEmpty()) escola = "E.E. Maria José";

            if (txtSubPerfil != null) {
                txtSubPerfil.setText("RA: " + ra + " • " + escola);
            }

            cursor.close();
        }
    }
}
