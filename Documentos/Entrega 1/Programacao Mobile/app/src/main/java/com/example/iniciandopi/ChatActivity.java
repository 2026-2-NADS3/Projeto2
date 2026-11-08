package com.example.iniciandopi;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    public Button btnVoltarChat, btnEnviarMensagem;
    public EditText edtMensagem;
    public LinearLayout containerMensagens;
    public ScrollView scrollChat;
    public TextView msgBot1;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        btnVoltarChat = findViewById(R.id.btnVoltarChat);
        btnEnviarMensagem = findViewById(R.id.btnEnviarMensagem);
        edtMensagem = findViewById(R.id.edtMensagem);
        containerMensagens = findViewById(R.id.containerMensagens);
        scrollChat = findViewById(R.id.scrollChat);
        msgBot1 = findViewById(R.id.msgBot1);

        carregarSaudacaoAluno();

        if (btnVoltarChat != null) {
            btnVoltarChat.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        if (btnEnviarMensagem != null) {
            btnEnviarMensagem.setOnClickListener(v -> enviarMensagem());
        }
    }

    private void carregarSaudacaoAluno() {
        if (dbHelper == null || msgBot1 == null) return;

        Cursor cursor = dbHelper.obterAlunoLogado();
        if (cursor == null) return;

        if (cursor.moveToFirst()) {
            int indexNome = cursor.getColumnIndex("nome");
            if (indexNome != -1) {
                String nome = cursor.getString(indexNome);
                if (nome != null && !nome.trim().isEmpty()) {
                    String primeiroNome = nome.trim().split(" ")[0];
                    msgBot1.setText("Olá, " + primeiroNome + "! Como posso te ajudar com seus cursos hoje?");
                }
            }
        }
        cursor.close();
    }

    public void enviarMensagem() {
        if (edtMensagem == null || containerMensagens == null) return;

        String texto = edtMensagem.getText().toString().trim();
        if (texto.isEmpty()) return;

        TextView tvUser = new TextView(this);
        LinearLayout.LayoutParams paramsUser = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsUser.gravity = Gravity.END;
        paramsUser.setMargins(0, 0, 0, dpToPx(12));
        tvUser.setLayoutParams(paramsUser);
        tvUser.setMaxWidth(dpToPx(270));
        tvUser.setText(texto);
        tvUser.setTextColor(0xFFFFFFFF);
        tvUser.setTextSize(13);
        tvUser.setBackgroundResource(R.drawable.bg_bubble_user);
        tvUser.setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14));

        containerMensagens.addView(tvUser);
        edtMensagem.setText("");

        if (scrollChat != null) {
            scrollChat.post(() -> scrollChat.fullScroll(ScrollView.FOCUS_DOWN));
        }

        // Tenta enviar para a Edge Function de IA no Supabase via Retrofit
        consultarIaRetrofit(texto);
    }

    private void consultarIaRetrofit(String texto) {
        try {
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            ChatRequest request = new ChatRequest(texto, "student");

            Call<ChatResponse> call = apiService.enviarMensagemChat("anon_key_demo", "Bearer token", request);
            call.enqueue(new Callback<ChatResponse>() {
                @Override
                public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getReply() != null) {
                        exibirRespostaIa(response.body().getReply());
                    } else {
                        gerarRespostaIaContextual(texto);
                    }
                }

                @Override
                public void onFailure(Call<ChatResponse> call, Throwable t) {
                    gerarRespostaIaContextual(texto);
                }
            });
        } catch (Exception e) {
            gerarRespostaIaContextual(texto);
        }
    }

    // ---------- Respostas automáticas (usadas quando a API não responde) ----------

    /** Tira acentos e deixa minúsculo: "Presença" vira "presenca". */
    private String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }

    /** Vale se o texto contém qualquer um dos pedaços (serve para palavras longas). */
    private boolean contem(String texto, String... pedacos) {
        for (String p : pedacos) {
            if (texto.contains(p)) return true;
        }
        return false;
    }

    /** Vale só se for a palavra inteira (serve para palavras curtas como "ra" e "qr"). */
    private boolean temPalavra(String texto, String... palavras) {
        for (String p : palavras) {
            if (Pattern.compile("\\b" + Pattern.quote(p) + "\\b").matcher(texto).find()) {
                return true;
            }
        }
        return false;
    }

    private void gerarRespostaIaContextual(String pergunta) {
        String p = normalizar(pergunta);
        String resposta;

        if (temPalavra(p, "oi", "ola", "bom dia", "boa tarde", "boa noite")) {
            resposta = "Olá! Posso ajudar com cursos, agenda, presença por QR Code, certificados, carteira do estudante e teste vocacional. Sobre o que você quer saber?";
        } else if (contem(p, "obrigad", "valeu")) {
            resposta = "De nada! Se tiver mais alguma dúvida, é só perguntar.";
        } else if (contem(p, "certificad", "pdf", "linkedin")) {
            resposta = "Para visualizar ou baixar seus certificados em PDF, acesse a aba 'Perfil' > 'Meus Certificados' e clique em 'Baixar PDF'. Lá também há o botão para adicionar ao LinkedIn.";
        } else if (contem(p, "presenca", "frequencia", "chamada") || temPalavra(p, "qr", "qrcode")) {
            resposta = "Você pode registrar sua presença abrindo o 'QR Presença' na Home e apontando a câmera do celular para o QR Code da sala. O histórico fica em 'Perfil' > 'Frequência'.";
        } else if (contem(p, "curso", "inscricao", "inscrever", "modulo", "trilha")) {
            resposta = "Você pode consultar os cursos disponíveis e confirmar sua inscrição na aba 'Cursos' no menu inferior.";
        } else if (contem(p, "agenda", "aula", "horario", "calendario")) {
            resposta = "Sua agenda com os horários de aulas e encontros presenciais ou online fica na aba 'Agenda'. Toque em um evento para adicioná-lo ao Google Agenda.";
        } else if (contem(p, "carteira", "carteirinha") || temPalavra(p, "card", "ra")) {
            resposta = "Sua Carteira Digital de Estudante com seu RA fica disponível em 'Meu Card' na Home ou na aba 'Perfil'.";
        } else if (contem(p, "teste", "vocacional", "riasec", "vocacao", "profissao")) {
            resposta = "Você pode responder ao Teste Vocacional RIASEC para descobrir sua vocação em 'Perfil' > 'Teste Vocacional'.";
        } else {
            resposta = "Entendi sua dúvida sobre '" + pergunta + "'. Sou a IA assistente da Próxima Etapa! Posso ajudar com cursos, agenda, presença, certificados, carteira e teste vocacional.";
        }

        exibirRespostaIa(resposta);
    }

    private void exibirRespostaIa(String textoResposta) {
        if (containerMensagens == null) return;

        TextView tvBot = new TextView(this);
        LinearLayout.LayoutParams paramsBot = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsBot.gravity = Gravity.START;
        paramsBot.setMargins(0, 0, 0, dpToPx(12));
        tvBot.setLayoutParams(paramsBot);
        tvBot.setMaxWidth(dpToPx(270));
        tvBot.setText(textoResposta);
        tvBot.setTextColor(0xFF0F172A);
        tvBot.setTextSize(13);
        tvBot.setBackgroundResource(R.drawable.bg_bubble_bot);
        tvBot.setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14));

        containerMensagens.addView(tvBot);

        if (scrollChat != null) {
            scrollChat.post(() -> scrollChat.fullScroll(ScrollView.FOCUS_DOWN));
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}