package com.example.iniciandopi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ProximaEtapa.db";
    private static final int DATABASE_VERSION = 3;

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
        c1.put("titulo", "Introdução à Programação");
        c1.put("descricao", "Aprenda lógica de programação e crie seus primeiros projetos.");
        c1.put("categoria", "PROGRAMA PORTAS ABERTAS");
        c1.put("duracao", "40 Horas");
        c1.put("status", "Disponível");
        db.insert("cursos", null, c1);

        ContentValues c2 = new ContentValues();
        c2.put("titulo", "Orientação Vocacional e ENEM");
        c2.put("descricao", "Como escolher sua carreira e obter bolsas ProUni/FIES.");
        c2.put("categoria", "WORKSHOP");
        c2.put("duracao", "12 Horas");
        c2.put("status", "Inscrito");
        db.insert("cursos", null, c2);

        ContentValues cert1 = new ContentValues();
        cert1.put("titulo", "Lógica de Programação");
        cert1.put("data", "15 Out 2026");
        cert1.put("horas", "40h");
        db.insert("certificados", null, cert1);

        ContentValues cert2 = new ContentValues();
        cert2.put("titulo", "Workshop de Orientação");
        cert2.put("data", "02 Set 2026");
        cert2.put("horas", "12h");
        db.insert("certificados", null, cert2);
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

    public boolean cadastrarUsuario(String email, String senha, String nome, String ra) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("senha", senha);
        values.put("nome", nome);
        values.put("ra", ra);

        long result = db.insertWithOnConflict("usuarios", null, values, SQLiteDatabase.CONFLICT_REPLACE);
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
}
