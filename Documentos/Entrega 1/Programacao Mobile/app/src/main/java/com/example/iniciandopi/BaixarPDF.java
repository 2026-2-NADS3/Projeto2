package com.example.iniciandopi;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
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
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BaixarPDF extends AppCompatActivity {

    public Button btnVoltarTelaBaixarPDF, btnBaixarPdfConfirmar, btnAdicionarLinkedin;
    public Button btnCompartilharWhatsapp, btnCompartilharEmail;
    private DatabaseHelper dbHelper;
    private CertificadoInfo info;

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

        // Certificado escolhido na tela anterior
        info = CertificadoInfo.doIntent(getIntent());
        TextView txtNomeArquivo = findViewById(R.id.txtNomeArquivoPdf);
        if (txtNomeArquivo != null) txtNomeArquivo.setText(info.getNomeArquivo());

        btnVoltarTelaBaixarPDF = findViewById(R.id.btnVoltarTelaBaixarPDF);
        btnBaixarPdfConfirmar = findViewById(R.id.btnBaixarPdfConfirmar);
        btnAdicionarLinkedin = findViewById(R.id.btnAdicionarLinkedin);
        btnCompartilharWhatsapp = findViewById(R.id.btnCompartilharWhatsapp);
        btnCompartilharEmail = findViewById(R.id.btnCompartilharEmail);

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

        if (btnCompartilharWhatsapp != null) {
            btnCompartilharWhatsapp.setOnClickListener(v -> compartilharWhatsapp());
        }

        if (btnCompartilharEmail != null) {
            btnCompartilharEmail.setOnClickListener(v -> compartilharEmail());
        }
    }

    // ---------- Compartilhar: PDF no cache + FileProvider ----------

    /** Gera o PDF na pasta de cache do app (so para compartilhar) e devolve o arquivo. */
    private File gerarPdfParaCompartilhar() throws IOException {
        File pasta = new File(getCacheDir(), "certificados");
        if (!pasta.exists() && !pasta.mkdirs()) throw new IOException("Sem pasta de cache");
        File arquivo = new File(pasta, info.getNomeArquivo());

        PdfDocument documento = criarPdf(obterNomeAluno());
        try (FileOutputStream saida = new FileOutputStream(arquivo)) {
            documento.writeTo(saida);
        } finally {
            documento.close();
        }
        return arquivo;
    }

    private Uri uriDoPdf(File arquivo) {
        return FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", arquivo);
    }

    // ---------- WhatsApp (PDF anexado) ----------

    private void compartilharWhatsapp() {
        Uri uri;
        try {
            uri = uriDoPdf(gerarPdfParaCompartilhar());
        } catch (IOException | IllegalArgumentException e) {
            Toast.makeText(this, "Não foi possível gerar o PDF para compartilhar.", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "Meu certificado de " + info.titulo + " - Próxima Etapa");
        intent.setClipData(ClipData.newRawUri("Certificado", uri));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Tenta o WhatsApp normal, depois o Business; se nenhum existir, abre o seletor do Android
        String[] pacotes = {"com.whatsapp", "com.whatsapp.w4b"};
        for (String pacote : pacotes) {
            try {
                intent.setPackage(pacote);
                startActivity(intent);
                return;
            } catch (ActivityNotFoundException ignorado) {
                // tenta o proximo
            }
        }

        intent.setPackage(null);
        try {
            startActivity(Intent.createChooser(intent, "Compartilhar certificado"));
            Toast.makeText(this, "WhatsApp não encontrado. Escolha outro app.", Toast.LENGTH_SHORT).show();
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Nenhum app disponível para compartilhar.", Toast.LENGTH_LONG).show();
        }
    }

    // ---------- E-mail (PDF anexado) ----------

    private void compartilharEmail() {
        Uri uri;
        try {
            uri = uriDoPdf(gerarPdfParaCompartilhar());
        } catch (IOException | IllegalArgumentException e) {
            Toast.makeText(this, "Não foi possível gerar o PDF para enviar.", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822"); // faz o Android mostrar só apps de e-mail
        intent.putExtra(Intent.EXTRA_SUBJECT, "Certificado - " + info.titulo + " | Próxima Etapa");
        intent.putExtra(Intent.EXTRA_TEXT,
                "Olá!\n\nSegue em anexo o meu certificado de conclusão de " + info.titulo
                        + " (Próxima Etapa).\n");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setClipData(ClipData.newRawUri("Certificado", uri));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(Intent.createChooser(intent, "Enviar certificado por e-mail"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Nenhum app de e-mail instalado.", Toast.LENGTH_LONG).show();
        }
    }

    // ---------- LinkedIn ----------

    /** Abre o formulário "Licenças e certificados" do LinkedIn já preenchido. */
    private void adicionarAoLinkedin() {
        String url = "https://www.linkedin.com/profile/add?startTask=CERTIFICATION_NAME"
                + "&name=" + Uri.encode(info.titulo)
                + "&organizationName=" + Uri.encode("Próxima Etapa")
                + "&issueYear=" + info.getAno()
                + "&issueMonth=" + info.getMes()
                + "&certId=" + Uri.encode(info.getCodigo());

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
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(842, 595, 1).create();
        PdfDocument.Page pagina = documento.startPage(pageInfo);
        Canvas canvas = pagina.getCanvas();

        int largura = pageInfo.getPageWidth();
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
        canvas.drawText("concluiu com êxito a formação em", centro, 335, texto);

        texto.setColor(Color.parseColor("#8B5CF6"));
        texto.setTypeface(Typeface.DEFAULT_BOLD);
        texto.setTextSize(24);
        canvas.drawText(info.titulo, centro, 375, texto);

        texto.setColor(Color.parseColor("#334155"));
        texto.setTypeface(Typeface.DEFAULT);
        texto.setTextSize(17);
        canvas.drawText("com carga horária total de " + info.getHorasPorExtenso() + ".", centro, 410, texto);

        texto.setColor(Color.parseColor("#94A3B8"));
        texto.setTextSize(13);
        canvas.drawText("Emitido em " + info.data + " • Código: " + info.getCodigo(), centro, 470, texto);

        documento.finishPage(pagina);
        return documento;
    }

    /** Salva o PDF e devolve um texto dizendo onde ficou. */
    private String salvarPdf(PdfDocument documento) throws IOException {
        String nomeArquivo = info.getNomeArquivo();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+: vai direto para a pasta Downloads, sem pedir permissão
            ContentValues valores = new ContentValues();
            valores.put(MediaStore.Downloads.DISPLAY_NAME, nomeArquivo);
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
            File arquivo = new File(pasta, nomeArquivo);
            try (FileOutputStream saida = new FileOutputStream(arquivo)) {
                documento.writeTo(saida);
            }
            return arquivo.getAbsolutePath();
        }
    }
}