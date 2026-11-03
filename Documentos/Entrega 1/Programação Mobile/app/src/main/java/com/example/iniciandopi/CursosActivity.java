package com.example.iniciandopi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CursosActivity extends AppCompatActivity {

    public Button btnInicio, btnCursos, btnAgenda, btnPerfil, btnInscrever1;
    public EditText edtBuscarCursos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cursos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnInicio = findViewById(R.id.btnInicio);
        btnCursos = findViewById(R.id.btnCursos);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnPerfil = findViewById(R.id.btnPerfil);
        btnInscrever1 = findViewById(R.id.btnInscrever1);
        edtBuscarCursos = findViewById(R.id.edtBuscarCursos);

        if (btnInicio != null) {
            btnInicio.setOnClickListener(v -> {
                Intent intent = new Intent(CursosActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnAgenda != null) {
            btnAgenda.setOnClickListener(v -> {
                Intent intent = new Intent(CursosActivity.this, Agenda.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnPerfil != null) {
            btnPerfil.setOnClickListener(v -> {
                Intent intent = new Intent(CursosActivity.this, Perfil.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnInscrever1 != null) {
            btnInscrever1.setOnClickListener(v -> {
                Toast.makeText(CursosActivity.this, "Inscrição realizada com sucesso!", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
