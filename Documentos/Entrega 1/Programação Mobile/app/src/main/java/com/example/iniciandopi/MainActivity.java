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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public Button btnInicio, btnCursos, btnAgenda, btnCertificados, btnPerfil, btnNotificacoes;
    public View cardQrPresenca, cardMeuCard;
    public TextView txtOlaUsuario, txtInicialAvatar, txtMesBadge, txtDiaBadge, txtBadgeNotificacoes;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

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
        btnNotificacoes = findViewById(R.id.btnNotificacoes);

        cardQrPresenca = findViewById(R.id.cardQrPresenca);
        cardMeuCard = findViewById(R.id.cardMeuCard);

        txtOlaUsuario = findViewById(R.id.txtOlaUsuario);
        txtInicialAvatar = findViewById(R.id.txtInicialAvatar);
        txtMesBadge = findViewById(R.id.txtMesBadge);
        txtDiaBadge = findViewById(R.id.txtDiaBadge);
        txtBadgeNotificacoes = findViewById(R.id.txtBadgeNotificacoes);

        carregarDadosAluno();
        carregarDataAgendaDestaque();

        if (btnInicio != null) {
            btnInicio.setOnClickListener(v -> irParaInicio());
        }

        if (btnCursos != null) {
            btnCursos.setOnClickListener(v -> irParaCursos());
        }

        if (btnAgenda != null) {
            btnAgenda.setOnClickListener(v -> irParaAgenda());
        }

        if (btnCertificados != null) {
            btnCertificados.setOnClickListener(v -> irParaCertificados());
        }

        if (btnPerfil != null) {
            btnPerfil.setOnClickListener(v -> irParaPerfil());
        }

        if (btnNotificacoes != null) {
            btnNotificacoes.setOnClickListener(v -> irParaComunicados());
        }

        if (cardQrPresenca != null) {
            cardQrPresenca.setOnClickListener(v -> irParaQrPresenca());
        }

        if (cardMeuCard != null) {
            cardMeuCard.setOnClickListener(v -> irParaMeuCard());
        }
    }

    // Toda vez que a Home volta para a frente, recalcula o número do sino
    @Override
    protected void onResume() {
        super.onResume();
        atualizarBadgeNotificacoes();
    }

    private void atualizarBadgeNotificacoes() {
        if (txtBadgeNotificacoes == null) return;

        int naoLidos = ComunicadosActivity.contarNaoLidos(this);
        if (naoLidos > 0) {
            txtBadgeNotificacoes.setText(naoLidos > 9 ? "9+" : String.valueOf(naoLidos));
            txtBadgeNotificacoes.setVisibility(View.VISIBLE);
        } else {
            txtBadgeNotificacoes.setVisibility(View.GONE);
        }
    }

    private void carregarDadosAluno() {
        if (dbHelper == null) return;

        Cursor cursor = dbHelper.obterAlunoLogado();
        if (cursor != null && cursor.moveToFirst()) {
            int indexNome = cursor.getColumnIndex("nome");
            if (indexNome != -1) {
                String nome = cursor.getString(indexNome);
                if (nome != null && !nome.isEmpty()) {
                    if (txtOlaUsuario != null) {
                        txtOlaUsuario.setText("Olá, " + nome);
                    }

                    if (txtInicialAvatar != null) {
                        String[] partes = nome.split(" ");
                        String i1 = partes.length > 0 && !partes[0].isEmpty() ? partes[0].substring(0, 1).toUpperCase() : "A";
                        String i2 = partes.length > 1 && !partes[1].isEmpty() ? partes[1].substring(0, 1).toUpperCase() : "";
                        txtInicialAvatar.setText(i1 + i2);
                    }
                }
            }
            cursor.close();
        }
    }

    private void carregarDataAgendaDestaque() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 2); // Exibe evento nos próximos dias
        Locale ptBr = new Locale("pt", "BR");

        SimpleDateFormat dfMes = new SimpleDateFormat("MMM", ptBr);
        SimpleDateFormat dfDia = new SimpleDateFormat("dd", ptBr);

        if (txtMesBadge != null) {
            String mes = dfMes.format(cal.getTime()).toUpperCase().replace(".", "");
            if (mes.length() > 3) mes = mes.substring(0, 3);
            txtMesBadge.setText(mes);
        }

        if (txtDiaBadge != null) {
            txtDiaBadge.setText(dfDia.format(cal.getTime()));
        }
    }

    public void irParaInicio() {

    }

    public void irParaCursos() {
        Intent cursos = new Intent(MainActivity.this, CursosActivity.class);
        startActivity(cursos);
        overridePendingTransition(0, 0);
    }

    public void irParaAgenda() {
        Intent agenda = new Intent(MainActivity.this, Agenda.class);
        startActivity(agenda);
        overridePendingTransition(0, 0);
    }

    public void irParaCertificados() {
        Intent certificados = new Intent(MainActivity.this, Certificados.class);
        startActivity(certificados);
        overridePendingTransition(0, 0);
    }

    public void irParaPerfil() {
        Intent perfil = new Intent(MainActivity.this, Perfil.class);
        startActivity(perfil);
        overridePendingTransition(0, 0);
    }

    public void irParaComunicados() {
        Intent comunicados = new Intent(MainActivity.this, ComunicadosActivity.class);
        startActivity(comunicados);
        overridePendingTransition(0, 0);
    }

    public void irParaQrPresenca() {
        Intent qr = new Intent(MainActivity.this, QrPresencaActivity.class);
        startActivity(qr);
        overridePendingTransition(0, 0);
    }

    public void irParaMeuCard() {
        Intent card = new Intent(MainActivity.this, CarteiraActivity.class);
        startActivity(card);
        overridePendingTransition(0, 0);
    }
}