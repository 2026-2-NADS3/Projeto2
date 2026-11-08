package com.example.iniciandopi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QrPresencaActivity extends AppCompatActivity {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    public Button btnVoltarQr, btnConfirmarPresenca;
    public View frameScanner;
    public TextView txtNomeAulaDetectada, txtProfAulaDetectada;
    private DatabaseHelper dbHelper;

    // Guarda o que foi lido no QR. Fica null enquanto nada foi lido.
    private String aulaLida = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qr_presenca);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        btnVoltarQr = findViewById(R.id.btnVoltarQr);
        btnConfirmarPresenca = findViewById(R.id.btnConfirmarPresenca);
        frameScanner = findViewById(R.id.frameScanner);
        txtNomeAulaDetectada = findViewById(R.id.txtNomeAulaDetectada);
        txtProfAulaDetectada = findViewById(R.id.txtProfAulaDetectada);

        if (btnVoltarQr != null) {
            btnVoltarQr.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        if (frameScanner != null) {
            frameScanner.setOnClickListener(v -> abrirCameraScanner());
        }

        if (btnConfirmarPresenca != null) {
            btnConfirmarPresenca.setOnClickListener(v -> confirmarPresenca());
        }
    }

    public void abrirCameraScanner() {
        IntentIntegrator integrator = new IntentIntegrator(QrPresencaActivity.this);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("Aponte a câmera para o QR Code da aula");
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }

    /** Salva a presença no SQLite, com data real e sem duplicar. */
    private void confirmarPresenca() {
        // 1) Só confirma se um QR já foi lido
        if (aulaLida == null) {
            Toast.makeText(this, "Leia o QR Code da aula antes de confirmar.", Toast.LENGTH_LONG).show();
            return;
        }

        Date agora = new Date();
        String dia = new SimpleDateFormat("dd/MM/yyyy", PT_BR).format(agora);
        String dataHora = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", PT_BR).format(agora);

        // 2) Evita presença duplicada para a mesma aula no mesmo dia
        if (dbHelper.existePresenca(aulaLida, dia)) {
            Toast.makeText(this, "Sua presença nesta aula já foi registrada hoje.", Toast.LENGTH_LONG).show();
            return;
        }

        // 3) Salva com a data e hora reais
        boolean salvou = dbHelper.salvarPresencaLocal(aulaLida, dataHora);
        if (salvou) {
            Toast.makeText(this, "Presença confirmada e salva no SQLite local!", Toast.LENGTH_LONG).show();
            finish();
            overridePendingTransition(0, 0);
        } else {
            Toast.makeText(this, "Erro ao salvar a presença. Tente novamente.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String lido = result.getContents().trim();
                if (!QrValidador.ehValido(lido)) {
                    Toast.makeText(this, "QR Code inválido. Use o QR Code oficial da aula.", Toast.LENGTH_LONG).show();
                    return;
                }
                aulaLida = QrValidador.extrairNomeAula(lido);

                if (txtNomeAulaDetectada != null) {
                    txtNomeAulaDetectada.setText(aulaLida);
                }
                if (txtProfAulaDetectada != null) {
                    txtProfAulaDetectada.setText("Código verificado • Presença pendente de confirmação");
                }
                Toast.makeText(this, "QR Code lido com sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Leitura cancelada", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}