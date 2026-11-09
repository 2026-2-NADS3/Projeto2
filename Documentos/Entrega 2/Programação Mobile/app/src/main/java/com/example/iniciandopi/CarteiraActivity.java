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

public class CarteiraActivity extends AppCompatActivity {

    public Button btnVoltarCarteira, btnInicio, btnCursos, btnAgenda, btnPerfil;
    public TextView txtNomeAlunoCarteira, txtRaAlunoCarteira, txtInicialCarteira, valInst;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_carteira);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        btnVoltarCarteira = findViewById(R.id.btnVoltarCarteira);
        btnInicio = findViewById(R.id.btnInicio);
        btnCursos = findViewById(R.id.btnCursos);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnPerfil = findViewById(R.id.btnPerfil);

        txtNomeAlunoCarteira = findViewById(R.id.txtNomeAlunoCarteira);
        txtRaAlunoCarteira = findViewById(R.id.txtRaAlunoCarteira);
        txtInicialCarteira = findViewById(R.id.txtInicialCarteira);
        valInst = findViewById(R.id.valInst);

        carregarDadosCarteira();

        if (btnVoltarCarteira != null) {
            btnVoltarCarteira.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        if (btnInicio != null) {
            btnInicio.setOnClickListener(v -> {
                Intent intent = new Intent(CarteiraActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnCursos != null) {
            btnCursos.setOnClickListener(v -> {
                Intent intent = new Intent(CarteiraActivity.this, CursosActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnAgenda != null) {
            btnAgenda.setOnClickListener(v -> {
                Intent intent = new Intent(CarteiraActivity.this, Agenda.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnPerfil != null) {
            btnPerfil.setOnClickListener(v -> {
                Intent intent = new Intent(CarteiraActivity.this, Perfil.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }
    }

    private void carregarDadosCarteira() {
        if (dbHelper == null) return;

        Cursor cursor = dbHelper.obterAlunoLogado();
        if (cursor != null && cursor.moveToFirst()) {
            int indexNome = cursor.getColumnIndex("nome");
            int indexRa = cursor.getColumnIndex("ra");
            int indexEscola = cursor.getColumnIndex("escola");

            if (indexNome != -1) {
                String nome = cursor.getString(indexNome);
                if (nome != null && !nome.isEmpty()) {
                    if (txtNomeAlunoCarteira != null) {
                        txtNomeAlunoCarteira.setText(nome.toUpperCase());
                    }

                    if (txtInicialCarteira != null) {
                        String[] partes = nome.split(" ");
                        String i1 = partes.length > 0 && !partes[0].isEmpty() ? partes[0].substring(0, 1).toUpperCase() : "A";
                        String i2 = partes.length > 1 && !partes[1].isEmpty() ? partes[1].substring(0, 1).toUpperCase() : "";
                        txtInicialCarteira.setText(i1 + i2);
                    }
                }
            }

            if (indexRa != -1) {
                String ra = cursor.getString(indexRa);
                if (ra != null && !ra.isEmpty() && txtRaAlunoCarteira != null) {
                    txtRaAlunoCarteira.setText("RA: " + ra);
                }
            }

            if (indexEscola != -1) {
                String escola = cursor.getString(indexEscola);
                if (escola != null && !escola.isEmpty() && valInst != null) {
                    valInst.setText(escola);
                }
            }
            cursor.close();
        }
    }
}
