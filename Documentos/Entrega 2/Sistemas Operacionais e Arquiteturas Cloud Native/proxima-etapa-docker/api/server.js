const express = require('express');
const { Pool } = require('pg');

const app = express();
app.use(express.json());

const pool = new Pool({
  host: process.env.DB_HOST,
  port: process.env.DB_PORT,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME,
});

app.get('/health', async (req, res) => {
  try {
    await pool.query('SELECT 1');
    res.json({ status: 'ok', database: 'connected' });
  } catch (err) {
    res.status(500).json({ status: 'error', database: 'unreachable' });
  }
});

app.get('/cursos', async (req, res) => {
  try {
    const result = await pool.query('SELECT * FROM cursos ORDER BY id');
    res.json(result.rows);
  } catch (err) {
    res.status(500).json({ erro: 'Falha ao consultar cursos' });
  }
});

app.post('/cursos', async (req, res) => {
  const { nome, local, carga_horaria } = req.body;
  if (!nome || !Number.isInteger(carga_horaria) || carga_horaria <= 0) {
    return res.status(400).json({ erro: 'nome e carga_horaria (inteiro > 0) são obrigatórios' });
  }
  try {
    const result = await pool.query(
      'INSERT INTO cursos (nome, local, carga_horaria) VALUES ($1, $2, $3) RETURNING *',
      [nome, local, carga_horaria]
    );
    res.status(201).json(result.rows[0]);
  } catch (err) {
    res.status(500).json({ erro: 'Falha ao cadastrar curso' });
  }
});

app.listen(3000, () => console.log('API rodando na porta 3000'));
