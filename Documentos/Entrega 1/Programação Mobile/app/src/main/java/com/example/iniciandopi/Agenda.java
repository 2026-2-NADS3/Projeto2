package com.example.iniciandopi;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Agenda extends AppCompatActivity {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    /** Guarda os dados de um evento da agenda. */
    private static class Evento {
        final String titulo;
        final int horaInicio, minInicio, horaFim, minFim;
        final String local;

        Evento(String titulo, int horaInicio, int minInicio, int horaFim, int minFim, String local) {
            this.titulo = titulo;
            this.horaInicio = horaInicio;
            this.minInicio = minInicio;
            this.horaFim = horaFim;
            this.minFim = minFim;
            this.local = local;
        }

        String detalhe() {
            return String.format(Locale.ROOT, "%02d:%02d - %02d:%02d • %s",
                    horaInicio, minInicio, horaFim, minFim, local);
        }
    }

    private Button btnInicio, btnCursos, btnCertificados, btnPerfil;
    private TextView txtLabelHoje;

    private LinearLayout[] cardsDia;
    private TextView[] siglas;
    private TextView[] numeros;

    private View cardEvent1, cardEvent2;
    private TextView txtTituloEvento1, txtDetEvento1, txtTituloEvento2, txtDetEvento2;

    private final Calendar[] dias = new Calendar[5];
    private int diaSelecionado = 0;
    private Evento[] eventosAtuais = new Evento[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agenda);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnInicio = findViewById(R.id.btnInicio);
        btnCursos = findViewById(R.id.btnCursos);
        btnCertificados = findViewById(R.id.btnCertificados);
        btnPerfil = findViewById(R.id.btnPerfil);

        txtLabelHoje = findViewById(R.id.txtLabelHoje);

        cardsDia = new LinearLayout[]{
                findViewById(R.id.cardDiaSeg),
                findViewById(R.id.cardDiaTer),
                findViewById(R.id.cardDiaQua),
                findViewById(R.id.cardDiaQui),
                findViewById(R.id.cardDiaSex)
        };
        siglas = new TextView[]{
                findViewById(R.id.txtSiglaDia1), findViewById(R.id.txtSiglaDia2),
                findViewById(R.id.txtSiglaDia3), findViewById(R.id.txtSiglaDia4),
                findViewById(R.id.txtSiglaDia5)
        };
        numeros = new TextView[]{
                findViewById(R.id.txtNumDia1), findViewById(R.id.txtNumDia2),
                findViewById(R.id.txtNumDia3), findViewById(R.id.txtNumDia4),
                findViewById(R.id.txtNumDia5)
        };

        cardEvent1 = findViewById(R.id.cardEvent1);
        cardEvent2 = findViewById(R.id.cardEvent2);
        txtTituloEvento1 = findViewById(R.id.txtTituloEvento1);
        txtDetEvento1 = findViewById(R.id.txtDetEvento1);
        txtTituloEvento2 = findViewById(R.id.txtTituloEvento2);
        txtDetEvento2 = findViewById(R.id.txtDetEvento2);

        montarDiasUteis();
        preencherDias();

        for (int i = 0; i < cardsDia.length; i++) {
            final int indice = i;
            cardsDia[i].setOnClickListener(v -> selecionarDia(indice));
        }

        // Tocar em um evento abre o Google Agenda com os dados preenchidos
        cardEvent1.setOnClickListener(v -> adicionarAoGoogleAgenda(eventosAtuais[0]));
        cardEvent2.setOnClickListener(v -> adicionarAoGoogleAgenda(eventosAtuais[1]));

        selecionarDia(0);
        configurarMenu();
    }

    // ---------- Datas: só dias úteis ----------

    private void pularFimDeSemana(Calendar cal) {
        while (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void montarDiasUteis() {
        Calendar cal = Calendar.getInstance();
        pularFimDeSemana(cal);

        for (int i = 0; i < dias.length; i++) {
            dias[i] = (Calendar) cal.clone();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            pularFimDeSemana(cal);
        }
    }

    private String siglaDoDia(Calendar cal) {
        String sigla = new SimpleDateFormat("EEE", PT_BR).format(cal.getTime())
                .toUpperCase(PT_BR).replace(".", "");
        return sigla.length() > 3 ? sigla.substring(0, 3) : sigla;
    }

    private void preencherDias() {
        SimpleDateFormat dfNum = new SimpleDateFormat("dd", PT_BR);
        for (int i = 0; i < dias.length; i++) {
            siglas[i].setText(siglaDoDia(dias[i]));
            numeros[i].setText(dfNum.format(dias[i].getTime()));
        }
    }

    // ---------- Seleção do dia ----------

    private void selecionarDia(int indice) {
        diaSelecionado = indice;
        for (int i = 0; i < cardsDia.length; i++) {
            boolean ativo = (i == indice);
            cardsDia[i].setBackgroundResource(ativo ? R.drawable.bg_day_selected : R.drawable.bg_day_unselected);
            siglas[i].setTextColor(ativo ? 0xFFDDD6FE : 0xFF64748B);
            numeros[i].setTextColor(ativo ? 0xFFFFFFFF : 0xFF0F172A);
        }
        atualizarRotulo(dias[indice]);
        atualizarEventos(dias[indice]);
    }

    private void atualizarRotulo(Calendar cal) {
        Calendar agora = Calendar.getInstance();
        boolean ehHoje = cal.get(Calendar.YEAR) == agora.get(Calendar.YEAR)
                && cal.get(Calendar.DAY_OF_YEAR) == agora.get(Calendar.DAY_OF_YEAR);

        String data = new SimpleDateFormat("dd 'DE' MMMM", PT_BR)
                .format(cal.getTime()).toUpperCase(PT_BR);
        String texto = siglaDoDia(cal) + ", " + data;

        txtLabelHoje.setText(ehHoje ? "HOJE (" + texto + ")" : texto);
    }

    // ---------- Eventos de cada dia da semana (exemplos) ----------

    private Evento[] eventosDoDia(int diaDaSemana) {
        String lab = "Lab 04 (FECAP)";
        String meet = "On-line (Google Meet)";

        switch (diaDaSemana) {
            case Calendar.MONDAY:
                return new Evento[]{
                        new Evento("Aula 03: Lógica & Algoritmos", 14, 0, 16, 0, lab),
                        new Evento("Mentoria de Carreira", 16, 30, 17, 30, meet)};
            case Calendar.TUESDAY:
                return new Evento[]{
                        new Evento("Oficina de Currículo", 14, 0, 15, 30, "Sala 12 (FECAP)"),
                        new Evento("Plantão de Dúvidas", 16, 0, 17, 0, meet)};
            case Calendar.WEDNESDAY:
                return new Evento[]{
                        new Evento("Visita Técnica FECAP", 14, 0, 16, 30, "Campus Liberdade"),
                        new Evento("Roda de Conversa: Carreiras", 17, 0, 18, 0, meet)};
            case Calendar.THURSDAY:
                return new Evento[]{
                        new Evento("Aula 04: Comunicação Assertiva", 14, 0, 16, 0, lab),
                        new Evento("Mentoria em Grupo", 16, 30, 17, 30, meet)};
            default: // sexta
                return new Evento[]{
                        new Evento("Workshop de Oratória", 14, 0, 16, 0, "Auditório (FECAP)"),
                        new Evento("Revisão da Semana", 16, 30, 17, 0, meet)};
        }
    }

    private void atualizarEventos(Calendar cal) {
        eventosAtuais = eventosDoDia(cal.get(Calendar.DAY_OF_WEEK));

        String dica = "\n＋ Toque para adicionar ao Google Agenda";
        txtTituloEvento1.setText(eventosAtuais[0].titulo);
        txtDetEvento1.setText(eventosAtuais[0].detalhe() + dica);
        txtTituloEvento2.setText(eventosAtuais[1].titulo);
        txtDetEvento2.setText(eventosAtuais[1].detalhe() + dica);
    }

    // ---------- Integração com o Google Agenda ----------

    /** Monta o horário (início ou fim) do evento no dia selecionado, em milissegundos. */
    private long horarioEmMillis(Evento e, boolean inicio) {
        Calendar c = (Calendar) dias[diaSelecionado].clone();
        c.set(Calendar.HOUR_OF_DAY, inicio ? e.horaInicio : e.horaFim);
        c.set(Calendar.MINUTE, inicio ? e.minInicio : e.minFim);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    private void adicionarAoGoogleAgenda(Evento e) {
        if (e == null) return;

        long inicio = horarioEmMillis(e, true);
        long fim = horarioEmMillis(e, false);

        // Aqui galera abre o app de calendário do celular já com tudo preenchido
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, inicio)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, fim)
                .putExtra(CalendarContract.Events.TITLE, e.titulo)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, e.local)
                .putExtra(CalendarContract.Events.DESCRIPTION, "Próxima Etapa - Agenda do aluno");

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // CASO O CELULAR NÃO TENHA O APP: abre o Google Agenda no navegador
            abrirNoNavegador(e, inicio, fim);
        }
    }

    private void abrirNoNavegador(Evento e, long inicio, long fim) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.ROOT);
        String url = "https://calendar.google.com/calendar/render?action=TEMPLATE"
                + "&text=" + Uri.encode(e.titulo)
                + "&dates=" + fmt.format(inicio) + "/" + fmt.format(fim)
                + "&location=" + Uri.encode(e.local)
                + "&details=" + Uri.encode("Próxima Etapa - Agenda do aluno");

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "Não foi possível abrir o Google Agenda neste dispositivo.", Toast.LENGTH_LONG).show();
        }
    }

    // ---------- Menu inferior ----------

    private void configurarMenu() {
        if (btnInicio != null) {
            btnInicio.setOnClickListener(v -> abrirTela(MainActivity.class));
        }
        if (btnCursos != null) {
            btnCursos.setOnClickListener(v -> abrirTela(CursosActivity.class));
        }
        if (btnCertificados != null) {
            btnCertificados.setOnClickListener(v -> abrirTela(Certificados.class));
        }
        if (btnPerfil != null) {
            btnPerfil.setOnClickListener(v -> abrirTela(Perfil.class));
        }
    }

    private void abrirTela(Class<?> destino) {
        startActivity(new Intent(Agenda.this, destino));
        overridePendingTransition(0, 0);
        finish();
    }
}