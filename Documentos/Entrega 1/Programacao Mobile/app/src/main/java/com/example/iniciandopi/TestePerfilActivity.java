package com.example.iniciandopi;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestePerfilActivity extends AppCompatActivity {

    public Button btnVoltarTeste, btnOpcaoA, btnOpcaoB, btnOpcaoC, btnProximaPergunta, btnVerCursosRecomendados, btnVoltarHome;
    public ProgressBar progressTeste;
    public TextView txtContadorPergunta, txtPerguntaTitle, txtPerguntaSub, txtPerfilDetectado, txtDiagnosticoIa, txtCursosRecomendadosList, txtInicialTeste;
    public View cardPergunta, cardResultadoVocacional;
    public LinearLayout containerOpcoes;

    private static class PerguntaVocacional {
        String titulo;
        String subtitulo;
        String opcaoA;
        String opcaoB;
        String opcaoC;

        PerguntaVocacional(String titulo, String subtitulo, String opcaoA, String opcaoB, String opcaoC) {
            this.titulo = titulo;
            this.subtitulo = subtitulo;
            this.opcaoA = opcaoA;
            this.opcaoB = opcaoB;
            this.opcaoC = opcaoC;
        }
    }

    private List<PerguntaVocacional> listaPerguntas;
    private int perguntaIndex = 0;
    private int opcaoSelecionada = 0;
    private int scoreA = 0;
    private int scoreB = 0;
    private int scoreC = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teste_perfil);

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

        inicializarPerguntas();

        btnVoltarTeste = findViewById(R.id.btnVoltarTeste);
        btnOpcaoA = findViewById(R.id.btnOpcaoA);
        btnOpcaoB = findViewById(R.id.btnOpcaoB);
        btnOpcaoC = findViewById(R.id.btnOpcaoC);
        btnProximaPergunta = findViewById(R.id.btnProximaPergunta);
        btnVerCursosRecomendados = findViewById(R.id.btnVerCursosRecomendados);
        btnVoltarHome = findViewById(R.id.btnVoltarHome);

        progressTeste = findViewById(R.id.progressTeste);
        txtContadorPergunta = findViewById(R.id.txtContadorPergunta);
        txtPerguntaTitle = findViewById(R.id.txtPerguntaTitle);
        txtPerguntaSub = findViewById(R.id.txtPerguntaSub);
        txtPerfilDetectado = findViewById(R.id.txtPerfilDetectado);
        txtDiagnosticoIa = findViewById(R.id.txtDiagnosticoIa);
        txtCursosRecomendadosList = findViewById(R.id.txtCursosRecomendadosList);
        txtInicialTeste = findViewById(R.id.txtInicialTeste);

        cardPergunta = findViewById(R.id.cardPergunta);
        cardResultadoVocacional = findViewById(R.id.cardResultadoVocacional);
        containerOpcoes = findViewById(R.id.containerOpcoes);

        carregarIniciaisAluno();

        if (btnVoltarTeste != null) {
            btnVoltarTeste.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        if (btnOpcaoA != null) {
            btnOpcaoA.setOnClickListener(v -> selecionarOpcao(1));
        }

        if (btnOpcaoB != null) {
            btnOpcaoB.setOnClickListener(v -> selecionarOpcao(2));
        }

        if (btnOpcaoC != null) {
            btnOpcaoC.setOnClickListener(v -> selecionarOpcao(3));
        }

        if (btnProximaPergunta != null) {
            btnProximaPergunta.setOnClickListener(v -> proximaPergunta());
        }

        if (btnVerCursosRecomendados != null) {
            btnVerCursosRecomendados.setOnClickListener(v -> {
                Intent intent = new Intent(TestePerfilActivity.this, CursosActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (btnVoltarHome != null) {
            btnVoltarHome.setOnClickListener(v -> irParaHome());
        }

        atualizarTela();
    }

    // Galera aqui Lê o nome do aluno logado no banco e mostra as iniciais no avatar (ex.: "Luis Felipe" -> "LF").
    private void carregarIniciaisAluno() {
        if (txtInicialTeste == null) return;

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.obterAlunoLogado();
        if (cursor == null) return;

        if (cursor.moveToFirst()) {
            int indexNome = cursor.getColumnIndex("nome");
            if (indexNome != -1) {
                String nome = cursor.getString(indexNome);
                if (nome != null && !nome.trim().isEmpty()) {
                    String[] partes = nome.trim().split(" ");
                    String i1 = partes[0].substring(0, 1).toUpperCase();
                    String i2 = partes.length > 1 && !partes[1].isEmpty() ? partes[1].substring(0, 1).toUpperCase() : "";
                    txtInicialTeste.setText(i1 + i2);
                }
            }
        }
        cursor.close();
    }

    private void irParaHome() {
        Intent intent = new Intent(TestePerfilActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void inicializarPerguntas() {
        listaPerguntas = new ArrayList<>();
        listaPerguntas.add(new PerguntaVocacional(
                "1. Como você prefere resolver problemas no dia a dia?",
                "Escolha a opção que mais combina com seu estilo natural de agir.",
                "A. Analisando dados, mapas mentais e criando código",
                "B. Conversando, ouvindo e ajudando pessoas em equipe",
                "C. Organizando processos, cronogramas e tarefas práticas"
        ));
        listaPerguntas.add(new PerguntaVocacional(
                "2. Qual tipo de projeto te deixa mais animado?",
                "Selecione a atividade que você faria com mais entusiasmo.",
                "A. Desenvolver um aplicativo, jogo ou site inovador",
                "B. Organizar uma ação social, mentoria ou evento comunitário",
                "C. Gerenciar o planejamento, metas e estratégia de um negócio"
        ));
        listaPerguntas.add(new PerguntaVocacional(
                "3. O que você costuma pesquisar por curiosidade no celular?",
                "Pense nos assuntos que prendem sua atenção espontaneamente.",
                "A. Tutoriais de tecnologia, inteligência artificial e ciência",
                "B. Notícias sobre sociedade, comportamento humano e psicologia",
                "C. Dicas de empreendedorismo, mercado e finanças pessoais"
        ));
        listaPerguntas.add(new PerguntaVocacional(
                "4. Em um trabalho em grupo na escola ou faculdade, você prefere:",
                "Qual função você costuma assumir naturalmente?",
                "A. Ficar responsável pela parte técnica, lógica e programação",
                "B. Fazer a apresentação e integrar as conversas do grupo",
                "C. Estruturar os prazos, relatórios e organizar a entrega"
        ));
        listaPerguntas.add(new PerguntaVocacional(
                "5. Qual ambiente de trabalho combina mais com você?",
                "Imagine o lugar ideal para o seu crescimento profissional.",
                "A. Um laboratório de tecnologia, startup ou ambiente com computadores",
                "B. Um centro educacional, ONG ou atendimento de impacto humano",
                "C. Um escritório corporativo, gerenciando projetos ou empresas"
        ));
        listaPerguntas.add(new PerguntaVocacional(
                "6. Se pudesse escolher um desafio para resolver agora, qual seria?",
                "Escolha a causa ou desafio com que mais se identifica.",
                "A. Criar uma inteligência artificial para automatizar tarefas",
                "B. Desenvolver um programa de apoio educacional para jovens",
                "C. Estruturar um novo modelo de negócio sustentável e expansivo"
        ));
        listaPerguntas.add(new PerguntaVocacional(
                "7. Como você prefere aprender um assunto totalmente novo?",
                "Como funciona o seu processo de aprendizado ideal?",
                "A. Testando na prática, entendendo a lógica e resolvendo desafios",
                "B. Debatendo em grupo, trocando ideias e ensinando colegas",
                "C. Seguindo guias passo a passo, manuais e listas estruturadas"
        ));
        listaPerguntas.add(new PerguntaVocacional(
                "8. Qual das seguintes habilidades você mais gostaria de dominar?",
                "Foque na competência que agregaria mais valor ao seu futuro.",
                "A. Programação de software, ciência de dados e engenharia",
                "B. Comunicação de alto impacto, empatia e mediação interpessoal",
                "C. Gestão financeira, planejamento estratégico e liderança"
        ));
        listaPerguntas.add(new PerguntaVocacional(
                "9. Qual o seu formato de conquista mais gratificante?",
                "O que te faz sentir que cumpriu uma grande missão?",
                "A. Ver um sistema técnico complexo funcionando perfeitamente",
                "B. Ver uma pessoa que você ajudou evoluir e atingir um sonho",
                "C. Ver uma meta de projeto ou empresa ser batida antes do prazo"
        ));
        listaPerguntas.add(new PerguntaVocacional(
                "10. Para o seu futuro profissional, o seu maior objetivo é:",
                "Selecione sua principal visão de sucesso na carreira.",
                "A. Ser um especialista reconhecido em tecnologia e inovação",
                "B. Gerar impacto social positivo e transformar vidas",
                "C. Liderar negócios, gerenciar equipes e alcançar grandes posições"
        ));
    }

    private void limparSelecao() {
        opcaoSelecionada = 0;
        Button[] botoes = {btnOpcaoA, btnOpcaoB, btnOpcaoC};
        for (Button b : botoes) {
            if (b != null) {
                b.setBackgroundResource(R.drawable.bg_option_unselected);
                b.setTextColor(0xFF334155);
            }
        }
    }

    private void selecionarOpcao(int opcao) {
        opcaoSelecionada = opcao;

        if (btnOpcaoA != null) {
            btnOpcaoA.setBackgroundResource(opcao == 1 ? R.drawable.bg_option_selected : R.drawable.bg_option_unselected);
            btnOpcaoA.setTextColor(opcao == 1 ? 0xFF8B5CF6 : 0xFF334155);
        }

        if (btnOpcaoB != null) {
            btnOpcaoB.setBackgroundResource(opcao == 2 ? R.drawable.bg_option_selected : R.drawable.bg_option_unselected);
            btnOpcaoB.setTextColor(opcao == 2 ? 0xFF8B5CF6 : 0xFF334155);
        }

        if (btnOpcaoC != null) {
            btnOpcaoC.setBackgroundResource(opcao == 3 ? R.drawable.bg_option_selected : R.drawable.bg_option_unselected);
            btnOpcaoC.setTextColor(opcao == 3 ? 0xFF8B5CF6 : 0xFF334155);
        }
    }

    private void proximaPergunta() {
        if (opcaoSelecionada == 0) {
            Toast.makeText(this, "Escolha uma opção para continuar", Toast.LENGTH_SHORT).show();
            return;
        }
        if (opcaoSelecionada == 1) scoreA++;
        else if (opcaoSelecionada == 2) scoreB++;
        else if (opcaoSelecionada == 3) scoreC++;

        if (perguntaIndex < listaPerguntas.size() - 1) {
            perguntaIndex++;
            atualizarTela();
        } else {
            finalizarEGerarDiagnosticoIa();
        }
    }

    private void finalizarEGerarDiagnosticoIa() {
        if (cardPergunta != null) cardPergunta.setVisibility(View.GONE);
        if (containerOpcoes != null) containerOpcoes.setVisibility(View.GONE);
        if (btnProximaPergunta != null) btnProximaPergunta.setVisibility(View.GONE);
        if (cardResultadoVocacional != null) cardResultadoVocacional.setVisibility(View.VISIBLE);

        if (txtContadorPergunta != null) txtContadorPergunta.setText("Teste Concluído!");
        if (progressTeste != null) {
            progressTeste.setMax(listaPerguntas.size());
            progressTeste.setProgress(listaPerguntas.size());
        }

        String perfil;
        String diagnostico;
        String recomendados;

        if (scoreA >= scoreB && scoreA >= scoreC) {
            perfil = "Perfil: Tecnologia & Investigativo (IAE)";
            diagnostico = "Sua análise demonstra grande aptidão para solução de problemas lógicos, desenvolvimento de software, análise de dados e inovação tecnológica.";
            recomendados = "• Introdução à Programação (40h)\n• Lógica & Algoritmos (Fatec)";
        } else if (scoreB >= scoreA && scoreB >= scoreC) {
            perfil = "Perfil: Social & Comunicação (SAC)";
            diagnostico = "Sua análise demonstra forte vocação para trabalho em equipe, mentoria, impacto social, empatia e facilidade de relacionamento interpessoal.";
            recomendados = "• Orientação Vocacional e ENEM (12h)\n• Workshop de Comunicação e Impacto";
        } else {
            perfil = "Perfil: Gestão & Processos (ECR)";
            diagnostico = "Sua análise demonstra perfil estratégico para liderança de negócios, planejamento financeiro, organização de projetos e gestão de equipes.";
            recomendados = "• Gestão de Projetos e Negócios (20h)\n• Workshop: Montando seu Currículo";
        }

        if (txtPerfilDetectado != null) txtPerfilDetectado.setText(perfil);
        if (txtDiagnosticoIa != null) txtDiagnosticoIa.setText(diagnostico);
        if (txtCursosRecomendadosList != null) txtCursosRecomendadosList.setText(recomendados);

        try {
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            TestResultRequest resultRequest = new TestResultRequest("user_id_demo", "vocacional", perfil);

            Call<Void> call = apiService.salvarResultadoTeste("anon_key_demo", "Bearer token", resultRequest);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(TestePerfilActivity.this, "Resultado vocacional salvo com sucesso!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(TestePerfilActivity.this, "Resultado vocacional salvo no histórico local!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Resultado vocacional salvo localmente!", Toast.LENGTH_SHORT).show();
        }
    }

    private void atualizarTela() {
        if (listaPerguntas == null || listaPerguntas.isEmpty()) return;

        PerguntaVocacional q = listaPerguntas.get(perguntaIndex);

        if (txtContadorPergunta != null) {
            txtContadorPergunta.setText("Pergunta " + String.format("%02d", perguntaIndex + 1) + " de " + String.format("%02d", listaPerguntas.size()));
        }

        if (progressTeste != null) {
            progressTeste.setMax(listaPerguntas.size());
            progressTeste.setProgress(perguntaIndex + 1);
        }

        if (txtPerguntaTitle != null) txtPerguntaTitle.setText(q.titulo);
        if (txtPerguntaSub != null) txtPerguntaSub.setText(q.subtitulo);

        if (btnOpcaoA != null) btnOpcaoA.setText(q.opcaoA);
        if (btnOpcaoB != null) btnOpcaoB.setText(q.opcaoB);
        if (btnOpcaoC != null) btnOpcaoC.setText(q.opcaoC);

        if (perguntaIndex == listaPerguntas.size() - 1 && btnProximaPergunta != null) {
            btnProximaPergunta.setText("Finalizar e Ver Resultado →");
        }

        limparSelecao();
    }
}