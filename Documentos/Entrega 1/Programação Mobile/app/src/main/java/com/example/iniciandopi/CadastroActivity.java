package com.example.iniciandopi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.Locale;

public class CadastroActivity extends AppCompatActivity {

    public EditText edtNomeCadastro, edtEmailCpfCadastro, edtRaCadastro, edtSenhaCadastro;
    public Button btnCadastrar, btnVoltarCadastro;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);

        int margem = (int) (24 * getResources().getDisplayMetrics().density); // 24dp em pixels
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    systemBars.left + margem,
                    systemBars.top + margem,
                    systemBars.right + margem,
                    systemBars.bottom + margem);
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
        // toLowerCase: mesmo padrão do Login, para os dois "falarem a mesma língua".
        String email = edtEmailCpfCadastro != null ? edtEmailCpfCadastro.getText().toString().trim().toLowerCase(Locale.ROOT) : "";
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

        // Não deixa criar uma conta com e-mail que já existe
        if (dbHelper.emailJaCadastrado(email)) {
            Toast.makeText(this, "Este e-mail já está cadastrado. Volte e faça login.", Toast.LENGTH_LONG).show();
            return;
        }

        boolean salvou = dbHelper.cadastrarUsuario(email, senha, nome, ra.isEmpty() ? "2026.03.8841" : ra);
        Log.d("CADASTRO", "Conta salva? " + salvou + " | e-mail: " + email);

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