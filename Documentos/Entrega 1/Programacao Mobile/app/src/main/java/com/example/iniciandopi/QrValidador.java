package com.example.iniciandopi;

public class QrValidador {

    private static final String PREFIXO = "PE";
    private static final String TIPO_AULA = "AULA";
    private static final int TAMANHO_MAXIMO = 80;

    /** Diz se o texto lido do QR tem o formato PE|AULA|Nome da aula. */
    public static boolean ehValido(String conteudo) {
        if (conteudo == null) return false;

        String texto = conteudo.trim();
        if (texto.isEmpty() || texto.length() > TAMANHO_MAXIMO) return false;

        String[] partes = texto.split("\\|", -1);
        if (partes.length != 3) return false;

        return partes[0].equals(PREFIXO)
                && partes[1].equals(TIPO_AULA)
                && !partes[2].trim().isEmpty();
    }

    /** Devolve só o nome da aula ("Aula 04") ou null se o QR for inválido. */
    public static String extrairNomeAula(String conteudo) {
        if (!ehValido(conteudo)) return null;
        return conteudo.trim().split("\\|", -1)[2].trim();
    }
}