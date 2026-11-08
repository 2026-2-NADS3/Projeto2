package com.example.iniciandopi;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

/**
 * Lista de prazos de ingresso no ensino superior (Enem, Sisu, ProUni, Fies e vestibulares).
 * É a ÚNICA fonte de dados: a tela de Comunicados, a Agenda e as notificações leem daqui.
 */
public class PrazosRepositorio {

    private static final String PREFS = "comunicados_prefs";
    private static final String KEY_LIDOS = "lidos";

    public static final int COR_NACIONAL = 0xFF8B5CF6;
    public static final int COR_SAO_PAULO = 0xFF2563EB;

    private static final String LINK_ENEM = "https://www.gov.br/inep/pt-br";
    private static final String LINK_ACESSO_UNICO = "https://acessounico.mec.gov.br";

    /** Um prazo. dataIso = "aaaa-mm-dd". demo = data inventada só para a apresentação. */
    public static class Prazo {
        public final String id, categoria, titulo, descricao, dataIso, link;
        public final int cor;
        public final boolean demo;

        Prazo(String id, String categoria, int cor, String titulo, String descricao,
              String dataIso, String link, boolean demo) {
            this.id = id;
            this.categoria = categoria;
            this.cor = cor;
            this.titulo = titulo;
            this.descricao = descricao;
            this.dataIso = dataIso;
            this.link = link;
            this.demo = demo;
        }

        public boolean temData() {
            return dataIso != null && dataIso.length() >= 10;
        }

        /** Quantos dias faltam (negativo = já passou). Null se não tiver data. */
        public Integer diasRestantes() {
            if (!temData()) return null;
            try {
                Date alvo = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).parse(dataIso);
                Calendar hoje = Calendar.getInstance();
                hoje.set(Calendar.HOUR_OF_DAY, 0);
                hoje.set(Calendar.MINUTE, 0);
                hoje.set(Calendar.SECOND, 0);
                hoje.set(Calendar.MILLISECOND, 0);
                return (int) Math.round((alvo.getTime() - hoje.getTimeInMillis()) / 86400000.0);
            } catch (ParseException e) {
                return null;
            }
        }

        public boolean encerrado() {
            Integer d = diasRestantes();
            return d != null && d < 0;
        }

        /** "08/11" */
        public String dataCurta() {
            if (!temData()) return "";
            return dataIso.substring(8, 10) + "/" + dataIso.substring(5, 7);
        }

        /** "08/11 • HOJE", "15/11 • EM 7 DIAS", "01/11 • ENCERRADO" */
        public String rotuloPrazo() {
            Integer dias = diasRestantes();
            if (dias == null) return "SEM DATA";
            String data = dataCurta() + " • ";
            if (dias < 0) return data + "ENCERRADO";
            if (dias == 0) return data + "HOJE";
            if (dias == 1) return data + "AMANHÃ";
            return data + "EM " + dias + " DIAS";
        }

        /** Vermelho = hoje/amanhã, laranja = esta semana, cinza = resto. */
        public int corRotulo() {
            Integer dias = diasRestantes();
            if (dias == null) return 0xFF64748B;
            if (dias < 0) return 0xFF94A3B8;
            if (dias <= 1) return 0xFFEF4444;
            if (dias <= 7) return 0xFFD97706;
            return 0xFF64748B;
        }

        /** Meia-noite UTC do dia do prazo: é o que o Google Agenda espera para evento de dia inteiro. */
        public long millisDiaInteiroUtc() {
            int ano = Integer.parseInt(dataIso.substring(0, 4));
            int mes = Integer.parseInt(dataIso.substring(5, 7)) - 1;
            int dia = Integer.parseInt(dataIso.substring(8, 10));
            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            c.clear();
            c.set(ano, mes, dia, 0, 0, 0);
            return c.getTimeInMillis();
        }
    }

    // =====================================================================
    //  LISTA DE PRAZOS  (para mexer nos avisos, é aqui)
    // =====================================================================

    /** Data de hoje + N dias, no formato aaaa-mm-dd. Usada só nas datas de demonstração. */
    private static String hojeMais(int dias) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, dias);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(c.getTime());
    }

    private static List<Prazo> todos() {
        List<Prazo> l = new ArrayList<>();

        // ---------- Datas reais (confirme sempre no site oficial) ----------

        l.add(new Prazo("enem-d1", "ENEM • NACIONAL", COR_NACIONAL,
                "Enem 2026: 1º dia de provas",
                "Linguagens, Ciências Humanas e Redação. Leve documento com foto e confira o horário de fechamento dos portões no site do Inep.",
                "2026-11-08", LINK_ENEM, false));

        l.add(new Prazo("enem-d2", "ENEM • NACIONAL", COR_NACIONAL,
                "Enem 2026: 2º dia de provas",
                "Ciências da Natureza e Matemática. Leve documento com foto e confira o horário de fechamento dos portões no site do Inep.",
                "2026-11-15", LINK_ENEM, false));

        l.add(new Prazo("unifesp-med", "UNIFESP • SÃO PAULO", COR_SAO_PAULO,
                "Unifesp Medicina 2027: provas do Vestibular Misto",
                "Provas em 17 e 18/12 para as vagas de Medicina (nota do Enem 2026 + prova). Os demais cursos da Unifesp usam o Sisu.",
                "2026-12-17", "https://www.unifesp.br", false));

        // ---------- Sem data oficial ainda: datas de DEMONSTRAÇÃO (relativas a hoje) ----------

        l.add(new Prazo("fatec", "FATEC • SÃO PAULO", COR_SAO_PAULO,
                "Fatec 1º semestre 2027: inscrições",
                "Vestibular das Fatecs, com cursos gratuitos de tecnologia. Confira o calendário no site oficial.",
                hojeMais(1), "https://vestibular.fatec.sp.gov.br", true));

        l.add(new Prazo("fuvest", "FUVEST • USP (SÃO PAULO)", COR_SAO_PAULO,
                "Fuvest 2027: provas",
                "Vestibular da USP. Confira o calendário oficial no site da Fuvest.",
                hojeMais(5), "https://www.fuvest.br", true));

        l.add(new Prazo("sisu", "SISU • NACIONAL", COR_NACIONAL,
                "Sisu 2027: inscrições",
                "Vagas em universidades públicas com a nota do Enem, pelo Portal Único de Acesso. A previsão do MEC é janeiro de 2027.",
                hojeMais(10), LINK_ACESSO_UNICO, true));

        l.add(new Prazo("unesp", "UNESP • SÃO PAULO", COR_SAO_PAULO,
                "Unesp 2027: vestibular",
                "Vestibular da Unesp, aplicado também na cidade de São Paulo. Confira o calendário no site oficial.",
                hojeMais(12), "https://vestibular.unesp.br", true));

        l.add(new Prazo("prouni", "PROUNI • BOLSAS", COR_NACIONAL,
                "ProUni 1º semestre de 2027: inscrições",
                "Bolsas integrais e de 50% em faculdades particulares. Acontece depois do Sisu. Tenha o número de inscrição do Enem em mãos.",
                hojeMais(14), LINK_ACESSO_UNICO, true));

        l.add(new Prazo("fies", "FIES • FINANCIAMENTO", COR_NACIONAL,
                "Fies 1º semestre de 2027: inscrições",
                "Financiamento estudantil com a nota do Enem. Acontece depois do ProUni.",
                hojeMais(18), LINK_ACESSO_UNICO, true));

        return l;
    }

    /** Próximos prazos primeiro; os que já encerraram vão para o fim da lista. */
    public static List<Prazo> listar() {
        List<Prazo> lista = todos();
        Collections.sort(lista, new Comparator<Prazo>() {
            @Override
            public int compare(Prazo a, Prazo b) {
                int ga = a.encerrado() ? 1 : 0;
                int gb = b.encerrado() ? 1 : 0;
                if (ga != gb) return ga - gb;
                return a.dataIso.compareTo(b.dataIso);
            }
        });
        return lista;
    }

    // =====================================================================
    //  LIDOS / NÃO LIDOS (alimenta o "● NOVO" e a bolinha do sino)
    // =====================================================================

    public static Set<String> obterLidos(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return new HashSet<>(prefs.getStringSet(KEY_LIDOS, new HashSet<String>()));
    }

    /** Quantos prazos ainda não foram lidos (os já encerrados não contam). */
    public static int contarNaoLidos(Context ctx) {
        Set<String> lidos = obterLidos(ctx);
        int total = 0;
        for (Prazo p : todos()) {
            if (!p.encerrado() && !lidos.contains(p.id)) total++;
        }
        return total;
    }

    public static void marcarComoLido(Context ctx, String id) {
        Set<String> lidos = obterLidos(ctx);
        lidos.add(id);
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putStringSet(KEY_LIDOS, lidos).apply();
    }

    public static void marcarTodosComoLidos(Context ctx) {
        Set<String> lidos = obterLidos(ctx);
        for (Prazo p : todos()) lidos.add(p.id);
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putStringSet(KEY_LIDOS, lidos).apply();
    }
}