package com.example.iniciandopi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CursosActivity extends AppCompatActivity {

    public Button btnInicio, btnCursos, btnAgenda, btnPerfil;
    public Button btnFiltroTodos, btnFiltroInscritos, btnInscreverCurso4;
    public View cardCurso1, cardCurso2, cardCurso3, cardCurso4, cardCurso5, cardCurso6;
    public EditText edtBuscarCursos;
    private DatabaseHelper dbHelper;
    private boolean inscritoCurso4 = false;

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

        dbHelper = new DatabaseHelper(this);

        btnInicio = findViewById(R.id.btnInicio);
        btnCursos = findViewById(R.id.btnCursos);
        btnAgenda = findViewById(R.id.btnAgenda);
        btnPerfil = findViewById(R.id.btnPerfil);

        btnFiltroTodos = findViewById(R.id.btnFiltroTodos);
        btnFiltroInscritos = findViewById(R.id.btnFiltroInscritos);
        btnInscreverCurso4 = findViewById(R.id.btnInscreverCurso4);

        cardCurso1 = findViewById(R.id.cardCurso1);
        cardCurso2 = findViewById(R.id.cardCurso2);
        cardCurso3 = findViewById(R.id.cardCurso3);
        cardCurso4 = findViewById(R.id.cardCurso4);
        cardCurso5 = findViewById(R.id.cardCurso5);
        cardCurso6 = findViewById(R.id.cardCurso6);

        edtBuscarCursos = findViewById(R.id.edtBuscarCursos);

        if (btnFiltroTodos != null) {
            btnFiltroTodos.setOnClickListener(v -> filtrarCursos(false));
        }

        if (btnFiltroInscritos != null) {
            btnFiltroInscritos.setOnClickListener(v -> filtrarCursos(true));
        }

        if (btnInscreverCurso4 != null) {
            btnInscreverCurso4.setOnClickListener(v -> realizarInscricaoCurso4());
        }

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
    }

    private void realizarInscricaoCurso4() {
        inscritoCurso4 = true;

        if (btnInscreverCurso4 != null) {
            btnInscreverCurso4.setText("Inscrito • Aulas iniciam em breve");
            btnInscreverCurso4.setBackgroundResource(R.drawable.bg_banner_yellow);
            btnInscreverCurso4.setTextColor(0xFFD97706);
            btnInscreverCurso4.setEnabled(false);
        }

        Toast.makeText(this, "Inscrição realizada com sucesso! Módulo 4 ativado na sua trilha.", Toast.LENGTH_LONG).show();
    }

    private void filtrarCursos(boolean apenasInscritos) {
        if (btnFiltroTodos != null) {
            btnFiltroTodos.setBackgroundResource(apenasInscritos ? R.drawable.bg_option_unselected : R.drawable.bg_btn_green);
            btnFiltroTodos.setTextColor(apenasInscritos ? 0xFF334155 : 0xFFFFFFFF);
        }

        if (btnFiltroInscritos != null) {
            btnFiltroInscritos.setBackgroundResource(apenasInscritos ? R.drawable.bg_btn_green : R.drawable.bg_option_unselected);
            btnFiltroInscritos.setTextColor(apenasInscritos ? 0xFFFFFFFF : 0xFF334155);
        }

        if (apenasInscritos) {
            if (cardCurso1 != null) cardCurso1.setVisibility(View.VISIBLE);
            if (cardCurso2 != null) cardCurso2.setVisibility(View.VISIBLE);
            if (cardCurso3 != null) cardCurso3.setVisibility(View.VISIBLE);
            if (cardCurso4 != null) cardCurso4.setVisibility(inscritoCurso4 ? View.VISIBLE : View.GONE);
            if (cardCurso5 != null) cardCurso5.setVisibility(View.GONE);
            if (cardCurso6 != null) cardCurso6.setVisibility(View.GONE);
        } else {
            if (cardCurso1 != null) cardCurso1.setVisibility(View.VISIBLE);
            if (cardCurso2 != null) cardCurso2.setVisibility(View.VISIBLE);
            if (cardCurso3 != null) cardCurso3.setVisibility(View.VISIBLE);
            if (cardCurso4 != null) cardCurso4.setVisibility(View.VISIBLE);
            if (cardCurso5 != null) cardCurso5.setVisibility(View.VISIBLE);
            if (cardCurso6 != null) cardCurso6.setVisibility(View.VISIBLE);
        }
    }
}
