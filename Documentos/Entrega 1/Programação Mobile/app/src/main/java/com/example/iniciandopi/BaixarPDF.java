package com.example.iniciandopi;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BaixarPDF extends AppCompatActivity {

    private static final String NOME_ARQUIVO = "Certificado_Logica_Programacao.pdf";

    public Button btnVoltarTelaBaixarPDF, btnBaixarPdfConfirmar, btnAdicionarLinkedin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_baixar_pdf);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        btnVoltarTelaBaixarPDF = findViewById(R.id.btnVoltarTelaBaixarPDF);
        btnBaixarPdfConfirmar = findViewById(R.id.btnBaixarPdfConfirmar);
        btnAdicionarLinkedin = findViewById(R.id.btnAdicionarLinkedin);

        if (btnVoltarTelaBaixarPDF != null) {
            btnVoltarTelaBaixarPDF.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        if (btnBaixarPdfConfirmar != null) {
            btnBaixarPdfConfirmar.setOnClickListener(v -> baixarCertificado());
        }

        if (btnAdicionarLinkedin != null) {
            btnAdicionarLinkedin.setOnClickListener(v -> adicionarAoLinkedin());
        }
    }

    // ---------- LinkedIn ----------

    /** Abre o formulário "Licenças e certificados" do LinkedIn já preenchido. */
    private void adicionarAoLinkedin() {
        String url = "https://www.linkedin.com/profile/add?startTask=CERTIFICATION_NAME"
                + "&name=" + Uri.encode("Lógica de Programação")
                + "&organizationName=" + Uri.encode("Próxima Etapa")
                + "&issueYear=2026"
                + "&issueMonth=10"
                + "&certId=" + Uri.encode("PE-2026-8841");

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Não foi possível abrir o LinkedIn neste dispositivo.", Toast.LENGTH_LONG).show();
        }
    }

    // ---------- Nome do aluno logado ----------

    private String obterNomeAluno() {
        String nome = "ALUNO";
        Cursor cursor = dbHelper.obterAlunoLogado();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex("nome");
                if (idx != -1) {
                    String lido = cursor.getString(idx);
                    if (lido != null && !lido.trim().isEmpty()) {
                        nome = lido.trim();
                    }
                }
            }
            cursor.close();
        }
        return nome.toUpperCase();
    }

    // ---------- Gera e salva o PDF ----------

    private void baixarCertificado() {
        PdfDocument documento = criarPdf(obterNomeAluno());
        try {
            String onde = salvarPdf(documento);
            Toast.makeText(this, "Certificado salvo em " + onde, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Não foi possível salvar o PDF. Tente novamente.", Toast.LENGTH_LONG).show();
        } finally {
            documento.close();
        }
    }

    /** Desenha o certificado em uma página A4 deitada (842 x 595 pontos). */
    private PdfDocument criarPdf(String nomeAluno) {
        PdfDocument documento = new PdfDocument();
        PdfDocument.PageInfo info = new PdfDocument.PageInfo.Builder(842, 595, 1).create();
        PdfDocument.Page pagina = documento.startPage(info);
        Canvas canvas = pagina.getCanvas();

        int largura = info.getPageWidth();
        float centro = largura / 2f;

        // Borda roxa
        Paint borda = new Paint(Paint.ANTI_ALIAS_FLAG);
        borda.setStyle(Paint.Style.STROKE);
        borda.setStrokeWidth(6);
        borda.setColor(Color.parseColor("#8B5CF6"));
        canvas.drawRect(30, 30, largura - 30, 595 - 30, borda);

        Paint texto = new Paint(Paint.ANTI_ALIAS_FLAG);
        texto.setTextAlign(Paint.Align.CENTER);

        texto.setColor(Color.parseColor("#8B5CF6"));
        texto.setTextSize(18);
        texto.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText("PRÓXIMA ETAPA", centro, 110, texto);

        texto.setColor(Color.parseColor("#0F172A"));
        texto.setTextSize(34);
        canvas.drawText("CERTIFICADO DE CONQUISTA", centro, 170, texto);

        texto.setColor(Color.parseColor("#64748B"));
        texto.setTypeface(Typeface.DEFAULT);
        texto.setTextSize(16);
        canvas.drawText("Certificamos que o(a) estudante", centro, 235, texto);

        texto.setColor(Color.parseColor("#0F172A"));
        texto.setTypeface(Typeface.DEFAULT_BOLD);
        texto.setTextSize(30);
        canvas.drawText(nomeAluno, centro, 290, texto);

        texto.setColor(Color.parseColor("#334155"));
        texto.setTypeface(Typeface.DEFAULT);
        texto.setTextSize(17);
        canvas.drawText("concluiu com êxito a formação em Lógica de Programação", centro, 345, texto);
        canvas.drawText("com carga horária total de 40 horas.", centro, 372, texto);

        texto.setColor(Color.parseColor("#94A3B8"));
        texto.setTextSize(13);
        canvas.drawText("Emitido em 15/10/2026 • Código: PE-2026-8841", centro, 470, texto);

        documento.finishPage(pagina);
        return documento;
    }

    /** Salva o PDF e devolve um texto dizendo onde ficou. */
    private String salvarPdf(PdfDocument documento) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+: vai direto para a pasta Downloads, sem pedir permissão
            ContentValues valores = new ContentValues();
            valores.put(MediaStore.Downloads.DISPLAY_NAME, NOME_ARQUIVO);
            valores.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
            valores.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            ContentResolver resolver = getContentResolver();
            Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, valores);
            if (uri == null) throw new IOException("Não criou o arquivo");

            try (OutputStream saida = resolver.openOutputStream(uri)) {
                if (saida == null) throw new IOException("Não abriu o arquivo");
                documento.writeTo(saida);
            }
            return "Downloads";
        } else {
            // Android 9 ou menor: salva na pasta do próprio app
            File pasta = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (pasta == null) throw new IOException("Sem armazenamento");
            File arquivo = new File(pasta, NOME_ARQUIVO);
            try (FileOutputStream saida = new FileOutputStream(arquivo)) {
                documento.writeTo(saida);
            }
            return arquivo.getAbsolutePath();
        }
    }
}