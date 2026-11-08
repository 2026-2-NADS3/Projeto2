package com.example.iniciandopi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ProximaEtapa.db";
    private static final int DATABASE_VERSION = 5;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT UNIQUE, " +
                "senha TEXT, " +
                "nome TEXT, " +
                "ra TEXT)";

        String createTableAluno = "CREATE TABLE IF NOT EXISTS aluno (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT, " +
                "nome TEXT, " +
                "ra TEXT, " +
                "token TEXT)";

        String createTablePresencas = "CREATE TABLE IF NOT EXISTS presencas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "aula TEXT, " +
                "data TEXT, " +
                "sincronizado INTEGER)";

        String createTableCursos = "CREATE TABLE IF NOT EXISTS cursos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titulo TEXT, " +
                "descricao TEXT, " +
                "categoria TEXT, " +
                "duracao TEXT, " +
                "status TEXT)";

        String createTableCertificados = "CREATE TABLE IF NOT EXISTS certificados (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titulo TEXT, " +
                "data TEXT, " +
                "horas TEXT)";

        db.execSQL(createTableUsuarios);
        db.execSQL(createTableAluno);
        db.execSQL(createTablePresencas);
        db.execSQL(createTableCursos);
        db.execSQL(createTableCertificados);

        populaDadosIniciais(db);
    }

    private void populaDadosIniciais(SQLiteDatabase db) {
        ContentValues c1 = new ContentValues();
        c1.put("titulo", "Orientação Vocacional & Carreira");
        c1.put("descricao", "Descubra seus pontos fortes, afinidades e planeje seus próximos passos.");
        c1.put("categoria", "MÓDULO 1");
        c1.put("duracao", "40 Horas");
        c1.put("status", "Concluído");
        db.insert("cursos", null, c1);

        ContentValues c2 = new ContentValues();
        c2.put("titulo", "Comunicação Assertiva & Oratória");
        c2.put("descricao", "Técnicas de oratória, expressão verbal, postura e apresentações em público.");
        c2.put("categoria", "MÓDULO 2");
        c2.put("duracao", "30 Horas");
        c2.put("status", "Em Andamento");
        db.insert("cursos", null, c2);

        ContentValues c3 = new ContentValues();
        c3.put("titulo", "Preparatório ENEM, ProUni & FIES");
        c3.put("descricao", "Estratégias de prova, redação nota 1000 e como obter bolsas na faculdade.");
        c3.put("categoria", "WORKSHOP");
        c3.put("duracao", "50 Horas");
        c3.put("status", "Inscrito");
        db.insert("cursos", null, c3);

        ContentValues c4 = new ContentValues();
        c4.put("titulo", "Liderança & Trabalho em Equipe");
        c4.put("descricao", "Desenvolva inteligência emocional, liderança empática e gestão de conflitos.");
        c4.put("categoria", "MÓDULO 4");
        c4.put("duracao", "25 Horas");
        c4.put("status", "Disponível");
        db.insert("cursos", null, c4);

        ContentValues cert1 = new ContentValues();
        cert1.put("titulo", "Orientação Vocacional & Carreira");
        cert1.put("data", "15 Out 2026");
        cert1.put("horas", "40h");
        db.insert("certificados", null, cert1);

        ContentValues cert2 = new ContentValues();
        cert2.put("titulo", "Workshop de Oratória e Comunicação");
        cert2.put("data", "02 Set 2026");
        cert2.put("horas", "12h");
        db.insert("certificados", null, cert2);

        ContentValues p1 = new ContentValues();
        p1.put("aula", "Aula 03: Comunicação Assertiva");
        p1.put("data", "28/10/2026 às 14:00");
        p1.put("sincronizado", 1);
        db.insert("presencas", null, p1);

        ContentValues p2 = new ContentValues();
        p2.put("aula", "Aula 02: Fundamentos de Oratória");
        p2.put("data", "21/10/2026 às 14:00");
        p2.put("sincronizado", 1);
        db.insert("presencas", null, p2);

        ContentValues p3 = new ContentValues();
        p3.put("aula", "Aula 01: Introdução ao Módulo 2");
        p3.put("data", "14/10/2026 às 14:00");
        p3.put("sincronizado", 1);
        db.insert("presencas", null, p3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS aluno");
        db.execSQL("DROP TABLE IF EXISTS presencas");
        db.execSQL("DROP TABLE IF EXISTS cursos");
        db.execSQL("DROP TABLE IF EXISTS certificados");
        onCreate(db);
    }

    /** Diz se já existe uma conta com este e-mail. */
    public boolean emailJaCadastrado(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM usuarios WHERE email = ?", new String[]{email});
        boolean existe = cursor.moveToFirst();
        cursor.close();
        return existe;
    }

    public boolean cadastrarUsuario(String email, String senha, String nome, String ra) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("senha", senha);
        values.put("nome", nome);
        values.put("ra", ra);

        // insert normal: se o e-mail já existir, devolve -1 e NÃO sobrescreve a conta antiga
        long result = db.insert("usuarios", null, values);
        if (result != -1) {
            salvarAluno(email, nome, ra, "token_local");
            return true;
        }
        return false;
    }

    public Cursor buscarUsuarioPorEmailESenha(String email, String senha) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM usuarios WHERE email = ? AND senha = ?", new String[]{email, senha});
    }

    public boolean salvarAluno(String email, String nome, String ra, String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM aluno");

        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("nome", nome);
        values.put("ra", ra);
        values.put("token", token);

        long result = db.insert("aluno", null, values);
        return result != -1;
    }

    public Cursor obterAlunoLogado() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM aluno LIMIT 1", null);
    }

    public Cursor obterCursosLocais() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM cursos", null);
    }

    public Cursor obterCertificadosLocais() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM certificados", null);
    }

    public Cursor obterPresencas() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM presencas ORDER BY id DESC", null);
    }

    public void limparSessao() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM aluno");
    }

    public boolean salvarPresencaLocal(String aula, String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("aula", aula);
        values.put("data", data);
        values.put("sincronizado", 0);

        long result = db.insert("presencas", null, values);
        return result != -1;
    }

    /**
     * Verifica se já existe presença para esta aula neste dia.
     * O dia vem no formato "dd/MM/yyyy" e a coluna data guarda "dd/MM/yyyy às HH:mm".
     */
    public boolean existePresenca(String aula, String dia) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM presencas WHERE aula = ? AND data LIKE ?",
                new String[]{aula, dia + "%"});
        boolean existe = cursor.moveToFirst();
        cursor.close();
        return existe;
    }
}