CREATE TABLE IF NOT EXISTS cursos (
  id SERIAL PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  local VARCHAR(100),
  carga_horaria INT NOT NULL CHECK (carga_horaria > 0)
);

INSERT INTO cursos (nome, local, carga_horaria) VALUES
  ('Orientação Profissional', 'Sala 101', 20),
  ('Introdução à Programação', 'Laboratório 3', 40),
  ('Preparação para o Vestibular', 'Auditório', 60);
