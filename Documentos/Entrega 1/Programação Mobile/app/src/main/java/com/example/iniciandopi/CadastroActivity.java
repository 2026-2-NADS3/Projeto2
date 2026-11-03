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

public class CadastroActivity extends AppCompatActivity {

    public EditText edtNomeCadastro, edtEmailCpfCadastro, edtRaCadastro, edtSenhaCadastro;
    public Button btnCadastrar, btnVoltarCadastro;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        btnVoltarCadastro = findViewById(R.id.btnVoltarCadastro);
        edtNomeCadastro = findViewById(R.id.edtNomeCadastro);
        edtEmailCpfCadastro = findViewById(R.id.edtEmailCpfCadastro);
        edtRaCadastro = findViewById(R.id.edtRaCadastro);
        edtSenhaCadastro = findViewById(R.id.edtSenhaCadastro);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        if (btnVoltarCadastro != null) {
            btnVoltarCadastro.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        if (btnCadastrar != null) {
            btnCadastrar.setOnClickListener(v -> cadastrarConta());
        }
    }

    public void cadastrarConta() {
        String nome = edtNomeCadastro != null ? edtNomeCadastro.getText().toString().trim() : "";
        String email = edtEmailCpfCadastro != null ? edtEmailCpfCadastro.getText().toString().trim() : "";
        String ra = edtRaCadastro != null ? edtRaCadastro.getText().toString().trim() : "";
        String senha = edtSenhaCadastro != null ? edtSenhaCadastro.getText().toString().trim() : "";

        if (nome.isEmpty()) {
            Toast.makeText(this, "Por favor, informe seu nome completo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor, informe seu e-mail ou CPF", Toast.LENGTH_SHORT).show();
            return;
        }

        if (senha.isEmpty()) {
            Toast.makeText(this, "Por favor, crie uma senha", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean salvou = dbHelper.cadastrarUsuario(email, senha, nome, ra.isEmpty() ? "2026.03.8841" : ra);

        if (salvou) {
            Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CadastroActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        } else {
            Toast.makeText(this, "Erro ao criar conta. Tente novamente.", Toast.LENGTH_SHORT).show();
        }
    }
}
