# Automação de Backup e Rastreabilidade — Próxima Etapa

Script em Shell (Bash) desenvolvido para a disciplina de **Cloud Native (Entrega 1)**, integrando a infraestrutura e rotinas operacionais do Projeto Interdisciplinar: o aplicativo da **ONG Próxima Etapa** (3º Semestre ADS - FECAP, 2026).

---

## 1. Problema Resolvido

No aplicativo da Próxima Etapa, são gerenciados dados fundamentais para a trajetória dos alunos da rede pública, como o banco de dados SQLite local, registros de presença via QR Code e certificados emitidos em PDF.

Caso o ambiente sofra uma falha ou corrupção de arquivos sem uma rotina de contingência, esses dados podem ser perdidos. Para reduzir esse risco e automatizar a administração desse armazenamento, foi desenvolvido este script em Shell.

O script realiza o empacotamento e a compactação dos dados, aplica restrições de permissão por segurança (`chmod 600`), valida a integridade do arquivo gerado, controla o espaço em disco mantendo apenas as versões mais recentes por meio de rotação de backups e registra as principais etapas e ocorrências da execução em arquivo de log.

---

## 2. Conceitos de Sistemas Operacionais Aplicados

Para atender aos requisitos técnicos da entrega, foram aplicados na prática conceitos estudados em Linux:

- **Códigos de Retorno e Tratamento de Erros:** avaliação dos códigos de retorno (`$?`) após comandos críticos e utilização de códigos de encerramento (`exit 1` até `exit 8`) para tratamento de diferentes situações de erro.
- **Permissões e Segurança (`chmod`):** aplicação da permissão `600` (`-rw-------`) nos arquivos de backup gerados, permitindo que somente o usuário proprietário possa ler ou modificar os arquivos.
- **Redirecionamento e Pipes (`|`):** encadeamento de comandos para identificar, ordenar e remover backups antigos (`find ... -printf | sort -n | head -n | cut | while read`), além do uso do `tee -a` para exibir mensagens no terminal e gravá-las simultaneamente no arquivo de log.
- **Manipulação do Sistema de Arquivos:** criação recursiva de diretórios com `mkdir -p` e manipulação de caminhos utilizando `dirname` e `basename`.
- **Variáveis e Parâmetros Dinâmicos:** leitura e validação de argumentos de linha de comando utilizando `while`, `case`, `shift` e expressões regulares.
- **Compactação de Dados:** utilização do `tar` com compressão `gzip` para gerar arquivos de backup no formato `.tar.gz`.
- **Validação de Integridade:** verificação do arquivo `.tar.gz` após sua criação utilizando `tar -tzf`, garantindo que o conteúdo compactado possa ser lido corretamente.
- **Automação e Rotação de Backups:** controle automático da quantidade máxima de cópias armazenadas, removendo os backups mais antigos quando o limite configurado é ultrapassado.

---

## 3. Estrutura do Projeto

O script foi estruturado de forma independente em um único arquivo executável, facilitando sua execução e portabilidade em ambientes Linux:

```text
.
├── backup_proxima_etapa_comentado.sh   # Script principal
├── dados-app/                           # Dados utilizados no backup (origem)
├── backups/                             # Diretório dos arquivos .tar.gz
├── logs/
│   └── backup.log                       # Arquivo de auditoria gerado automaticamente
└── README.md                            # Documentação técnica do projeto
```

As pastas `backups/` e `logs/` podem ser criadas automaticamente pelo script caso ainda não existam.

O arquivo `backup.log` também é criado ou atualizado automaticamente durante a execução.

---

## 4. Funcionamento

O script recebe como parâmetros o diretório de origem, o diretório de destino e, opcionalmente, a quantidade máxima de backups que devem ser mantidos.

### Exemplo:

```bash
./backup_proxima_etapa_comentado.sh -o ./dados-app -d ./backups -n 5
```

Nesse exemplo:

- `-o` define a pasta de origem dos dados;
- `-d` define a pasta onde os backups serão armazenados;
- `-n 5` determina que serão mantidas no máximo 5 cópias.

O script também possui um modo de simulação:

```bash
./backup_proxima_etapa_comentado.sh -o ./dados-app -d ./backups -n 5 --dry-run
```

Nesse modo, a rotina é simulada sem criar ou remover arquivos de backup.

---

## 5. Como Testar na VM Ubuntu

O script foi desenvolvido para execução em ambiente Linux utilizando Bash. Para testar a solução, recomenda-se utilizar uma VM com Ubuntu.

### 5.1. Colocar o script na VM

Após transferir o arquivo `backup_proxima_etapa_comentado.sh` para a VM Ubuntu, abra o terminal e acesse a pasta onde o arquivo está localizado.

Caso o arquivo esteja na pasta Home:

```bash
cd ~
```

Verifique se o arquivo está presente:

```bash
ls -l
```

### 5.2. Dar permissão de execução

Execute:

```bash
chmod +x backup_proxima_etapa_comentado.sh
```

### 5.3. Verificar a sintaxe

Antes de executar o script, pode ser realizada uma verificação de sintaxe:

```bash
bash -n backup_proxima_etapa_comentado.sh
```

Se nenhum erro for apresentado, a sintaxe do script está correta.

### 5.4. Criar dados fictícios para teste

Para realizar o teste sem utilizar dados reais da aplicação, podem ser criadas pastas e arquivos de exemplo:

```bash
mkdir -p dados-app backups
```

Depois:

```bash
echo "Banco de dados de teste" > dados-app/banco.txt
echo "Registro de presença via QR Code" > dados-app/presenca.txt
echo "Certificado de teste" > dados-app/certificado.pdf
```

Verifique os arquivos:

```bash
ls -l dados-app
```

### 5.5. Executar o modo de simulação

O modo `--dry-run` permite verificar o comportamento do script antes de realizar um backup real:

```bash
./backup_proxima_etapa_comentado.sh -o ./dados-app -d ./backups -n 5 --dry-run
```

Durante a simulação, o script informa:

- onde o backup seria criado;
- qual permissão seria aplicada;
- qual limite de retenção está configurado.

O `dry-run` não cria nem remove arquivos de backup.

### 5.6. Executar o backup

Depois da simulação, execute o backup normalmente:

```bash
./backup_proxima_etapa_comentado.sh -o ./dados-app -d ./backups -n 5
```

Verifique o arquivo gerado:

```bash
ls -lh backups
```

Deverá ser criado um arquivo semelhante a:

```text
proxima_etapa_backup_20260924_103000.tar.gz
```

### 5.7. Verificar a integridade

Para visualizar o conteúdo do backup:

```bash
tar -tzf backups/*.tar.gz
```

Os arquivos presentes em `dados-app/` deverão aparecer na listagem.

Essa verificação também representa a mesma validação de integridade realizada automaticamente pelo script.

### 5.8. Verificar a permissão

O script aplica a permissão `600` ao arquivo de backup.

Para verificar:

```bash
ls -l backups
```

O arquivo deverá apresentar uma permissão semelhante a:

```text
-rw-------
```

Isso significa que somente o proprietário possui permissão de leitura e escrita.

### 5.9. Verificar os logs

As execuções ficam registradas em:

```text
logs/backup.log
```

Para visualizar:

```bash
cat logs/backup.log
```

O log apresenta informações como:

- data e horário;
- diretório de origem;
- diretório de destino;
- tamanho dos dados;
- resultado da criação do backup;
- validação de integridade;
- política de retenção;
- possíveis erros.

### 5.10. Testar a política de retenção

Para testar a rotação automática, utilize um limite menor, como 3 backups:

```bash
./backup_proxima_etapa_comentado.sh -o ./dados-app -d ./backups -n 3
```

Execute o comando algumas vezes e depois verifique:

```bash
ls -lh backups
```

O diretório deverá manter no máximo 3 arquivos de backup. Quando o limite for ultrapassado, os backups mais antigos serão removidos automaticamente.

---

## 6. Processo de Backup

A execução segue as seguintes etapas:

1. Criação da pasta de logs, caso ela ainda não exista.
2. Leitura dos parâmetros informados pelo usuário.
3. Validação dos parâmetros obrigatórios.
4. Validação da quantidade máxima de backups.
5. Verificação das dependências necessárias.
6. Validação do diretório de origem.
7. Criação do diretório de destino, caso necessário.
8. Cálculo do tamanho dos dados de origem.
9. Geração de um nome único para o backup utilizando data e hora.
10. Verificação do modo `dry-run`.
11. Compactação dos dados utilizando `tar` e `gzip`.
12. Verificação do código de retorno do `tar`.
13. Verificação da existência do arquivo gerado.
14. Validação da integridade do arquivo `.tar.gz`.
15. Aplicação da permissão `600`.
16. Registro das informações do backup no arquivo de log.
17. Verificação da política de retenção.
18. Remoção dos backups mais antigos quando o limite é ultrapassado.
19. Finalização da rotina com código de retorno apropriado.

---

## 7. Política de Retenção

O script permite definir quantos backups devem permanecer armazenados.

Por padrão:

```text
Máximo de backups: 5
```

É possível alterar esse valor utilizando o parâmetro `-n`:

```bash
./backup_proxima_etapa_comentado.sh -o ./dados-app -d ./backups -n 3
```

Nesse caso, o sistema manterá no máximo 3 arquivos de backup, removendo automaticamente os mais antigos quando necessário.

A ordenação dos backups é realizada com base na data de modificação dos arquivos.

---

## 8. Validação de Integridade

Após a criação do arquivo `.tar.gz`, o script realiza uma validação utilizando:

```bash
tar -tzf arquivo.tar.gz
```

Essa operação verifica se o arquivo compactado pode ser aberto e listado corretamente.

Caso a validação falhe, o script:

1. registra o erro no log;
2. remove o arquivo de backup inválido;
3. encerra a execução com código de erro.

Essa etapa evita que um arquivo que não possa ser lido corretamente seja mantido como um backup válido.

---

## 9. Segurança

Os arquivos de backup recebem a permissão:

```text
600
```

Representação:

```text
-rw-------
```

Isso significa que somente o usuário proprietário possui permissão de leitura e escrita no arquivo.

Essa configuração reduz o risco de acesso indevido aos dados armazenados nos backups.

---

## 10. Logs e Rastreabilidade

As principais etapas da execução são registradas em:

```text
logs/backup.log
```

As mensagens possuem data, hora e nível de informação, por exemplo:

```text
[2026-09-23 20:45:00] INFO: ===== Iniciando rotina de backup =====
[2026-09-23 20:45:01] SUCCESS: Backup criado com êxito
[2026-09-23 20:45:01] SUCCESS: Integridade do backup confirmada.
```

O uso do `tee -a` permite que as mensagens sejam exibidas simultaneamente no terminal e armazenadas no arquivo de log.

O log permite acompanhar a execução da rotina e identificar possíveis falhas.

---

## 11. Tratamento de Erros

O script utiliza diferentes códigos de saída para identificar situações específicas:

| Código | Situação |
|---|---|
| `0` | Execução concluída com sucesso |
| `1` | Parâmetros inválidos ou ausentes |
| `2` | Dependência obrigatória não encontrada |
| `3` | Diretório de origem inválido ou inexistente |
| `4` | Falha ao preparar o diretório de destino |
| `5` | Falha durante a compactação com `tar` |
| `6` | Arquivo de backup não encontrado após a compactação |
| `7` | Falha na validação da integridade do backup |
| `8` | Falha ao aplicar a permissão de segurança |

Essa diferenciação facilita a identificação da etapa em que uma execução apresentou problema.

---

## 12. Modo Dry-Run

O parâmetro `--dry-run` permite simular a execução sem criar ou remover arquivos de backup.

Exemplo:

```bash
./backup_proxima_etapa_comentado.sh -o ./dados-app -d ./backups -n 5 --dry-run
```

Durante a simulação, o script informa:

- onde o backup seria criado;
- qual permissão seria aplicada;
- qual é o limite de retenção configurado.

O modo de simulação não cria nem remove arquivos de backup. As mensagens da simulação podem ser registradas normalmente no arquivo de log.

---

## 13. Tecnologias e Comandos Utilizados

- Bash / Shell Script
- Linux / Ubuntu
- `tar`
- `gzip`
- `find`
- `sort`
- `head`
- `cut`
- `du`
- `date`
- `mkdir`
- `rm`
- `chmod`
- `tee`
- `dirname`
- `basename`
- `wc`
- Expressões regulares
- Pipes e redirecionamento de saída

---

## 14. Objetivo da Automação

A solução demonstra a aplicação prática de conceitos de Sistemas Operacionais e Cloud Native em uma rotina de infraestrutura.

A automação reduz a necessidade de intervenção manual, padroniza a criação dos backups, valida a integridade dos arquivos gerados, controla o armazenamento das versões e fornece rastreabilidade por meio dos registros de execução.

Dessa forma, a rotina de backup torna-se mais organizada, reproduzível e adequada para um cenário de aplicação que trabalha com dados importantes dos alunos.