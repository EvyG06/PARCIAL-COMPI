package com.eia.felinas.parser;

/**
 * Lector de la entrada como flujo de tokens separados por espacios en blanco.
 *
 * Requisito 2.2 del enunciado: no se puede asumir un numero fijo de tokens
 * por linea y hay que tolerar lineas vacias y espacios finales. Por eso este
 * lector ignora por completo la estructura de lineas: divide el texto entero
 * por cualquier secuencia de espacios, tabulaciones o saltos de linea y
 * entrega los tokens uno a uno.
 */
public final class LectorTokens {

    private final String[] tokens;
    private int posicion = 0;

    public LectorTokens(String entrada) {
        String limpia = entrada == null ? "" : entrada.trim();
        this.tokens = limpia.isEmpty() ? new String[0] : limpia.split("\\s+");
    }

    /** Indica si quedan tokens por leer. */
    public boolean hayTokens() {
        return posicion < tokens.length;
    }

    /**
     * Lee el siguiente token como entero. La descripcion se usa para armar
     * un mensaje de error legible si la entrada esta incompleta o el token
     * no es un numero.
     */
    public int siguienteEntero(String descripcion) {
        String token = siguienteToken(descripcion);
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new ErrorDeEntrada(
                    "Se esperaba un numero entero para " + descripcion
                    + " pero se encontro: \"" + token + "\"");
        }
    }

    /** Igual que siguienteEntero pero en long (pesos acumulados, seccion 2.1). */
    public long siguienteLong(String descripcion) {
        String token = siguienteToken(descripcion);
        try {
            return Long.parseLong(token);
        } catch (NumberFormatException e) {
            throw new ErrorDeEntrada(
                    "Se esperaba un numero entero para " + descripcion
                    + " pero se encontro: \"" + token + "\"");
        }
    }

    private String siguienteToken(String descripcion) {
        if (!hayTokens()) {
            throw new ErrorDeEntrada(
                    "La entrada termino antes de tiempo: faltaba " + descripcion);
        }
        return tokens[posicion++];
    }
}
