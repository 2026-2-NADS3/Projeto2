# Projeto Mobile - Entrega Final (Entrega 2)

## Integrantes
- Julia Basílio
- Luis Felipe Trindade
- Davi Bigotto
- Maria Eduarda


---

## Descrição do Projeto

Aplicativo Android desenvolvido como a **Entrega Final** da disciplina de Programação Mobile. 

Esta versão consolida a conclusão do projeto, apresentando uma aplicação completa, integrada e funcional que simula um portal acadêmico do aluno. O sistema combina **persistência de dados local (SQLite)** com **consumo de APIs REST na nuvem (Supabase)** via **Retrofit**, oferecendo uma experiência robusta que abrange autenticação, agenda dinâmica, cursos, assistente de IA, controle de presença por QR Code, carteirinha digital e emissão de certificados.

---

## Arquitetura e Tecnologias Utilizadas

- **Linguagem:** Java
- **UI / Layout:** XML, ConstraintLayout, Material Design 3, Design Responsivo (Edge-to-Edge)
- **Networking & APIs:** Retrofit 2, Gson Converter (`ApiClient` & `ApiService`)
- **Backend & Cloud:** Supabase (Autenticação, Banco de Dados PostgreSQL via REST, Edge Functions para IA)
- **Persistência Local:** SQLite (`DatabaseHelper`)
- **Ferramentas:** Android Studio, Git & GitHub

---

## Integração com APIs e Backend (Supabase)

O aplicativo se conecta a um backend na nuvem utilizando **Supabase** como BaaS (Backend-as-a-Service), consumido através da biblioteca **Retrofit**. 

A classe `ApiClient` gerencia a instância do Retrofit apontando para a URL base do projeto:
`https://jzaageilkvvucgedlwxr.supabase.co/`

A interface `ApiService` define os seguintes endpoints consumidos pelo aplicativo:

1. **Autenticação (`POST /auth/v1/token?grant_type=password`):**
   - Realiza o login do usuário enviando credenciais (`LoginRequest`) e recebendo o token de acesso (`LoginResponse`).
2. **Cursos (`GET /rest/v1/courses`):**
   - Busca a listagem de cursos disponíveis na plataforma (`CourseModel`).
3. **Certificados (`GET /rest/v1/certificates`):**
   - Consulta os certificados emitidos para o usuário filtrando por `user_id` (`CertificateModel`).
4. **Resultados de Testes (`POST /rest/v1/test_results`):**
   - Salva no banco de dados da nuvem os resultados dos testes de perfil do aluno (`TestResultRequest`).
5. **Assistente de IA (`POST /functions/v1/functions/v1/ai-assistant`):**
   - Comunicação com a Edge Function do Supabase para o chat interativo de suporte ao aluno (`ChatRequest` / `ChatResponse`).

---

## Persistência de Dados Local (SQLite)

Além da nuvem, o aplicativo utiliza um banco de dados local SQLite (`DatabaseHelper`) para garantir funcionamento offline e cache rápido de informações essenciais, como:
- Dados do aluno logado e sessão atual.
- Registros locais de progresso e preferências.

---

## Principais Funcionalidades da Entrega Final

- **Autenticação e Cadastro:** Telas de Login e Cadastro integradas tanto com autenticação remota quanto com o banco SQLite local.
- **Tela Inicial (Home Dashboard):** Saudação personalizada, barra de progresso da trilha de aprendizagem ("Tecnologia & Inovação"), atalhos de Acesso Rápido e **Agenda Destaque** totalmente dinâmica calculada de acordo com o dia da semana.
- **Agenda Acadêmica:** Navegação por dias úteis da semana com listagem detalhada de horários, locais e integração com o Google Agenda.
- **Chat com IA (`ChatActivity`):** Assistente virtual integrado via API para tirar dúvidas do aluno.
- **QR Presença:** Sistema de leitura de código QR para registro de presença.
- **Meu Card (Carteirinha Digital):** Exibição da identidade estudantil com dados do aluno.
- **Cursos e Trilhas:** Consulta de disciplinas e conteúdos programáticos.
- **Comunicados e Notificações:** Central de avisos com contador dinâmico de mensagens não lidas.
- **Certificados e Downloads (`BaixarPDF`):** Visualização e download de arquivos em PDF.

---

## Estrutura das Principais Telas

- `LoginActivity` & `CadastroActivity` (Autenticação e Cadastro)
- `MainActivity` (Tela Inicial / Dashboard)
- `Agenda` (Calendário e Eventos Acadêmicos)
- `ChatActivity` (Assistente de IA / Chat)
- `CursosActivity` (Módulo de Cursos)
- `ComunicadosActivity` (Central de Avisos)
- `CarteiraActivity` (Carteirinha Digital / Meu Card)
- `QrPresencaActivity` (Validação de Presença)
- `Certificados` & `BaixarPDF` (Gestão e Download de Certificados em PDF)
- `Perfil` (Dados do Usuário)

---

## Como Executar o Projeto

### 1. Clonar o Repositório

```bash
git clone <url-do-repositorio>
```

### 2. Abrir no Android Studio

1. Abra o Android Studio.
2. Selecione **Open** e escolha a pasta do projeto da Entrega Final.
3. Aguarde o Gradle finalizar a sincronização das dependências.

### 3. Configuração do Emulador

Para garantir a compatibilidade e a correta visualização da interface (suporte a Edge-to-Edge e densidade de tela), recomendamos o uso do seguinte dispositivo:

### Device Recomendado

✅ **Pixel 9** (API 34+)

---

## Como Testar

1. Execute o aplicativo no Android Studio utilizando o emulador recomendado (**Pixel 9**).
2. Na tela de login/cadastro, insira seus dados para autenticar (conectado ao Supabase / SQLite).
3. Na tela principal (`MainActivity`), verifique:
   - A saudação personalizada;
   - A **Agenda Destaque** dinâmica baseada na data atual;
   - Os atalhos de Acesso Rápido (**QR Presença** e **Meu Card**).
4. Teste as demais funcionalidades navegando pela barra inferior (`Home`, `Cursos`, `Agenda`, `Perfil`), o chat com IA e o ícone de notificações (`🔔`).

---

## Requisitos

- Android Studio (versão recente)
- Android SDK (API 24 até API 34+)
- Conexão com a Internet (para requisições de API via Retrofit/Supabase)

---

## Controle de Versão

Este projeto utiliza Git e GitHub para o versionamento de código.

Para atualizar o repositório local:
```bash
git pull
```

Para enviar alterações:
```bash
git add .
git commit -m "Descrição da alteração"
git push
```

---

## Observações

Projeto desenvolvido como **Entrega Final** da disciplina de Programação Mobile. O uso do emulador **Pixel 9** é estritamente recomendado para assegurar a fidelidade visual e o layout responsivo projetados.
