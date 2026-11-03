package com.example.iniciandopi;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Visualizar extends AppCompatActivity {

    public Button btnVoltar;
    public TextView txtNomeAlunoCertificado;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_visualizar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        btnVoltar = findViewById(R.id.btnVoltar);
        txtNomeAlunoCertificado = findViewById(R.id.txtNomeAlunoCertificado);

        carregarNomeAluno();

        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }
    }

    private void carregarNomeAluno() {
        if (dbHelper == null || txtNomeAlunoCertificado == null) return;

        Cursor cursor = dbHelper.obterAlunoLogado();
        if (cursor != null && cursor.moveToFirst()) {
            int indexNome = cursor.getColumnIndex("nome");
            if (indexNome != -1) {
                String nome = cursor.getString(indexNome);
                if (nome != null && !nome.isEmpty()) {
                    txtNomeAlunoCertificado.setText(nome.toUpperCase());
                }
            }
            cursor.close();
        }
    }
}
