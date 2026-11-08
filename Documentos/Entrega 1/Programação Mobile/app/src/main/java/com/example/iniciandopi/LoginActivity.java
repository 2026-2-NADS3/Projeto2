package com.example.iniciandopi;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public EditText edtEmailCpf, edtSenha;
    public Button btnEntrar, btnCriarConta;
    public TextView txtEsqueceuSenha;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

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

        edtEmailCpf = findViewById(R.id.edtEmailCpf);
        edtSenha = findViewById(R.id.edtSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        btnCriarConta = findViewById(R.id.btnCriarConta);
        txtEsqueceuSenha = findViewById(R.id.txtEsqueceuSenha);

        if (btnEntrar != null) {
            btnEntrar.setOnClickListener(v -> fazerLogin());
        }

        if (btnCriarConta != null) {
            btnCriarConta.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        if (txtEsqueceuSenha != null) {
            txtEsqueceuSenha.setOnClickListener(v -> Toast.makeText(LoginActivity.this, "Recuperação de senha em breve!", Toast.LENGTH_SHORT).show());
        }
    }

    public void fazerLogin() {
        String email = edtEmailCpf != null ? edtEmailCpf.getText().toString().trim() : "";
        String senha = edtSenha != null ? edtSenha.getText().toString().trim() : "";

        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor, informe seu e-mail ou CPF", Toast.LENGTH_SHORT).show();
            return;
        }

        if (senha.isEmpty()) {
            Toast.makeText(this, "Por favor, informe sua senha", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            LoginRequest request = new LoginRequest(email, senha);

            Call<LoginResponse> call = apiService.fazerLogin(
                    "anon_key_demo",
                    "application/json",
                    request
            );

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String token = response.body().getAccessToken();
                        dbHelper.salvarAluno(email, email, "2026.03.8841", token);
                        Toast.makeText(LoginActivity.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
                        prosseguirParaHome();
                    } else {
                        fazerLoginOffline(email, senha);
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    fazerLoginOffline(email, senha);
                }
            });

        } catch (Exception e) {
            fazerLoginOffline(email, senha);
        }
    }

    private void fazerLoginOffline(String email, String senha) {
        Cursor cursor = dbHelper.buscarUsuarioPorEmailESenha(email, senha);
        if (cursor != null && cursor.moveToFirst()) {
            int indexNome = cursor.getColumnIndex("nome");
            int indexRa = cursor.getColumnIndex("ra");

            String nome = indexNome != -1 ? cursor.getString(indexNome) : "Aluno";
            String ra = indexRa != -1 ? cursor.getString(indexRa) : "2026.03.8841";

            cursor.close();
            dbHelper.salvarAluno(email, nome, ra, "token_local_sqlite");
            Toast.makeText(this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show();
            prosseguirParaHome();
        } else {
            if (cursor != null) {
                cursor.close();
            }
            Toast.makeText(this, "E-mail ou senha incorretos! Crie uma conta no botão abaixo.", Toast.LENGTH_LONG).show();
        }
    }

    private void prosseguirParaHome() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}