#!/bin/bash

# Projeto Interdisciplinar (PI) - 3º Semestre ADS (FECAP)
# Aplicação: Próxima Etapa
# Script responsável por realizar o backup dos dados da aplicação.
#
# O script:
# - compacta os dados em .tar.gz;
# - verifica se o backup foi criado corretamente;
# - aplica permissão 600 ao arquivo;
# - mantém somente a quantidade definida de backups;
# - registra as operações em um arquivo de log;
# - possui uma opção de simulação (dry-run).

# Faz com que erros em pipelines sejam identificados corretamente.
set -o pipefail

# Descobre a pasta onde o próprio script está localizado.
# Assim, o arquivo de log fica junto com o projeto.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Pasta e arquivo utilizados para registrar o histórico das execuções.
LOG_DIR="${SCRIPT_DIR}/logs"
LOG_FILE="${LOG_DIR}/backup.log"

# Cria a pasta de logs caso ela ainda não exista.
mkdir -p "$LOG_DIR"


# Função utilizada para padronizar as mensagens exibidas no terminal
# e também armazená-las no arquivo de log.
log_msg() {
    local nivel="$1"
    local msg="$2"
    local timestamp

    timestamp="$(date '+%Y-%m-%d %H:%M:%S')"

    echo "[$timestamp] ${nivel}: ${msg}" | tee -a "$LOG_FILE"
}


# Verifica se todos os comandos utilizados pelo script
# estão disponíveis no sistema Ubuntu.
checar_dependencias() {

    local comandos=(
        "tar"
        "du"
        "find"
        "sort"
        "head"
        "cut"
        "date"
        "mkdir"
        "ls"
        "wc"
        "rm"
        "chmod"
        "tee"
    )

    for cmd in "${comandos[@]}"; do

        if ! command -v "$cmd" &>/dev/null; then

            log_msg "ERROR" \
                "O comando obrigatório '$cmd' não está instalado no sistema."

            return 1
        fi

    done

    return 0
}


# Verifica se a pasta de origem realmente existe
# e se possui algum arquivo para ser copiado.
validar_diretorio_origem() {

    local dir="$1"

    if [ ! -d "$dir" ]; then

        log_msg "ERROR" \
            "Diretório de origem não encontrado: '$dir'"

        return 1
    fi

    if [ -z "$(ls -A "$dir" 2>/dev/null)" ]; then

        log_msg "ERROR" \
            "O diretório de origem está vazio: '$dir'"

        return 1
    fi

    return 0
}


# Verifica se o diretório de destino existe.
# Caso não exista, ele é criado automaticamente.
preparar_diretorio_destino() {

    local dir="$1"

    if [ ! -d "$dir" ]; then

        log_msg "INFO" \
            "Diretório de destino não existia. Criando pasta: '$dir'"

        mkdir -p "$dir" || {

            log_msg "ERROR" \
                "Falha de permissão ao criar pasta de destino: '$dir'"

            return 1
        }

    fi

    return 0
}


# Depois de criar o arquivo .tar.gz, o script tenta
# listar o conteúdo dele. Se o tar conseguir ler o arquivo,
# consideramos que a compactação está íntegra.
validar_integridade_backup() {

    local arquivo="$1"

    log_msg "INFO" \
        "Validando integridade do backup: $(basename "$arquivo")"

    if tar -tzf "$arquivo" >/dev/null 2>&1; then

        log_msg "SUCCESS" \
            "Integridade do backup confirmada."

        return 0

    else

        log_msg "ERROR" \
            "Falha na validação da integridade do backup."

        return 1
    fi
}


# Controla a quantidade de backups armazenados.
# Se ultrapassar o limite informado pelo usuário,
# os arquivos mais antigos são removidos.
rotacionar_backups() {

    local pasta_dest="$1"
    local limite="$2"
    local total_arquivos

    total_arquivos=$(
        find "$pasta_dest" \
            -maxdepth 1 \
            -type f \
            -name "proxima_etapa_backup_*.tar.gz" |
        wc -l
    )

    log_msg "INFO" \
        "Quantidade atual de backups: $total_arquivos | Limite configurado: $limite"

    if [ "$total_arquivos" -gt "$limite" ]; then

        local a_remover=$((total_arquivos - limite))

        log_msg "INFO" \
            "Limite excedido. Serão removido(s) $a_remover backup(s) mais antigo(s)."

        # %T@ retorna a data de modificação do arquivo.
        # sort -n coloca os arquivos mais antigos primeiro.
        find "$pasta_dest" \
            -maxdepth 1 \
            -type f \
            -name "proxima_etapa_backup_*.tar.gz" \
            -printf "%T@ %p\n" |
            sort -n |
            head -n "$a_remover" |
            cut -d' ' -f2- |
            while read -r arquivo_antigo; do

                if rm -f "$arquivo_antigo"; then

                    log_msg "INFO" \
                        "Backup antigo removido: $(basename "$arquivo_antigo")"

                else

                    log_msg "ERROR" \
                        "Não foi possível remover: $arquivo_antigo"

                fi

            done

    else

        log_msg "INFO" \
            "Política de retenção respeitada. Nenhum backup precisa ser removido."

    fi
}


# Mostra as opções disponíveis quando o usuário
# executa o script com -h ou --help.
mostrar_ajuda() {

    cat <<EOF

Uso:
  $0 -o <diretorio_origem> -d <diretorio_destino> [-n <max_copias>] [--dry-run]

Opções:

  -o          Diretório que contém os dados da aplicação (obrigatório)
  -d          Diretório onde os backups serão salvos (obrigatório)
  -n          Quantidade máxima de backups (padrão: 5)
  --dry-run   Simula a execução sem criar ou remover backups
  -h, --help  Mostra esta mensagem de ajuda

Exemplo:

  $0 -o ./dados-app -d ./backups -n 5

EOF
}


# Quantidade padrão de backups que serão mantidos.
MAX_COPIAS=5

# Variáveis que receberão os caminhos informados pelo usuário.
ORIGEM=""
DESTINO=""

# Controla se o script está sendo executado em modo de simulação.
DRY_RUN=false


# Leitura dos parâmetros informados no terminal.
# O case permite identificar cada opção recebida.
while [ $# -gt 0 ]; do

    case "$1" in

        -o)

            if [ -z "$2" ]; then
                echo "Erro: o parâmetro -o precisa receber um diretório."
                exit 1
            fi

            ORIGEM="$2"
            shift 2
            ;;

        -d)

            if [ -z "$2" ]; then
                echo "Erro: o parâmetro -d precisa receber um diretório."
                exit 1
            fi

            DESTINO="$2"
            shift 2
            ;;

        -n)

            if [ -z "$2" ]; then
                echo "Erro: o parâmetro -n precisa receber um valor."
                exit 1
            fi

            MAX_COPIAS="$2"
            shift 2
            ;;

        --dry-run)

            DRY_RUN=true
            shift
            ;;

        -h|--help)

            mostrar_ajuda
            exit 0
            ;;

        *)

            echo "Opção inválida: $1"

            mostrar_ajuda

            exit 1
            ;;

    esac

done


# Verifica se os parâmetros obrigatórios foram informados.
if [ -z "$ORIGEM" ] || [ -z "$DESTINO" ]; then

    echo "Erro: os parâmetros -o e -d são obrigatórios."

    mostrar_ajuda

    exit 1
fi


# Verifica se o número de backups informado pelo usuário
# é realmente um número inteiro positivo.
if ! [[ "$MAX_COPIAS" =~ ^[0-9]+$ ]] ||
   [ "$MAX_COPIAS" -lt 1 ]; then

    echo "Erro: o parâmetro -n deve ser um número inteiro positivo."

    exit 1
fi


# Início da execução principal.
log_msg "INFO" \
    "===== Iniciando rotina de backup (Próxima Etapa) ====="

log_msg "INFO" \
    "Origem: $ORIGEM"

log_msg "INFO" \
    "Destino: $DESTINO"

log_msg "INFO" \
    "Retenção máxima: $MAX_COPIAS"

log_msg "INFO" \
    "Dry-run: $DRY_RUN"


# Antes de realizar o backup, são feitas as validações básicas.
checar_dependencias || exit 2

validar_diretorio_origem "$ORIGEM" || exit 3

preparar_diretorio_destino "$DESTINO" || exit 4


# Cria um nome único para o backup utilizando data e hora.
TIMESTAMP="$(date '+%Y%m%d_%H%M%S')"

NOME_ARQUIVO="proxima_etapa_backup_${TIMESTAMP}.tar.gz"

CAMINHO_FINAL="${DESTINO}/${NOME_ARQUIVO}"


# Calcula o tamanho dos dados antes da compactação.
TAMANHO_ORIGEM="$(
    du -sh "$ORIGEM" 2>/dev/null |
    cut -f1
)"

log_msg "INFO" \
    "Tamanho calculado dos dados de origem: $TAMANHO_ORIGEM"


# O dry-run permite verificar o que o script faria
# sem criar ou excluir arquivos de backup.
if [ "$DRY_RUN" = true ]; then

    log_msg "INFO" \
        "[DRY-RUN] Simulação ativa."

    log_msg "INFO" \
        "[DRY-RUN] O arquivo seria criado em: $CAMINHO_FINAL"

    log_msg "INFO" \
        "[DRY-RUN] A permissão aplicada seria: 600"

    log_msg "INFO" \
        "[DRY-RUN] A política de retenção manteria no máximo $MAX_COPIAS backup(s)."

    log_msg "INFO" \
        "===== Fim da simulação dry-run ====="

    exit 0
fi


# Cria o arquivo compactado.
# -c cria o arquivo
# -z utiliza gzip
# -f define o nome do arquivo
log_msg "INFO" \
    "Compactando pasta '$ORIGEM'..."

tar -czf "$CAMINHO_FINAL" \
    -C "$(dirname "$ORIGEM")" \
    "$(basename "$ORIGEM")"

STATUS_TAR=$?


# Verifica o código de retorno do comando tar.
if [ $STATUS_TAR -ne 0 ]; then

    log_msg "ERROR" \
        "Erro durante a execução do tar. Código: $STATUS_TAR"

    rm -f "$CAMINHO_FINAL"

    exit 5
fi


# Confirma se o arquivo realmente foi criado.
if [ ! -f "$CAMINHO_FINAL" ]; then

    log_msg "ERROR" \
        "Arquivo de backup não encontrado após a compactação."

    exit 6
fi


# Valida se o arquivo .tar.gz pode ser lido corretamente.
validar_integridade_backup "$CAMINHO_FINAL"

STATUS_INTEGRIDADE=$?

if [ $STATUS_INTEGRIDADE -ne 0 ]; then

    log_msg "ERROR" \
        "Backup inválido. O arquivo será removido."

    rm -f "$CAMINHO_FINAL"

    exit 7
fi


# Aplica a permissão 600.
# Isso significa que somente o proprietário pode
# ler e modificar o arquivo de backup.
chmod 600 "$CAMINHO_FINAL"

if [ $? -ne 0 ]; then

    log_msg "ERROR" \
        "Não foi possível aplicar a permissão 600 ao backup."

    rm -f "$CAMINHO_FINAL"

    exit 8
fi


# Obtém o tamanho final do arquivo de backup.
TAMANHO_BACKUP="$(
    du -sh "$CAMINHO_FINAL" 2>/dev/null |
    cut -f1
)"

log_msg "SUCCESS" \
    "Backup criado com êxito: $CAMINHO_FINAL"

log_msg "INFO" \
    "Tamanho do backup: $TAMANHO_BACKUP"

log_msg "INFO" \
    "Permissão 600 aplicada com sucesso."


# Por último, verifica a quantidade de backups existentes
# e remove os mais antigos caso ultrapassem o limite.
rotacionar_backups "$DESTINO" "$MAX_COPIAS"


# Finalização da rotina.
log_msg "INFO" \
    "===== Rotina finalizada com sucesso ====="

exit 0
