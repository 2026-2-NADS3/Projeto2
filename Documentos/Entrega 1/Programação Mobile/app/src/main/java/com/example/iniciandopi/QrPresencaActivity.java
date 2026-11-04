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

public class QrPresencaActivity extends AppCompatActivity {

    public Button btnVoltarQr, btnConfirmarPresenca;
    public View frameScanner;
    public TextView txtNomeAulaDetectada, txtProfAulaDetectada;
    private DatabaseHelper dbHelper;

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
            btnConfirmarPresenca.setOnClickListener(v -> {
                String aula = txtNomeAulaDetectada != null ? txtNomeAulaDetectada.getText().toString() : "Aula Geral";
                if (dbHelper != null) {
                    dbHelper.salvarPresencaLocal(aula, "01/11/2026 às 20:00");
                }
                Toast.makeText(QrPresencaActivity.this, "Presença confirmada e salva no SQLite local!", Toast.LENGTH_LONG).show();
                finish();
                overridePendingTransition(0, 0);
            });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String conteudoLido = result.getContents();
                if (txtNomeAulaDetectada != null) {
                    txtNomeAulaDetectada.setText(conteudoLido);
                }
                if (txtProfAulaDetectada != null) {
                    txtProfAulaDetectada.setText("Código verificado • Presença pendente de confirmação");
                }
                Toast.makeText(this, "QR Code Lido: " + conteudoLido, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Leitura cancelada", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
