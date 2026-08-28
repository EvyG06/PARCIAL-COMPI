package com.eia.felinas.parser;

/**
 * Error de entrada malformada. La GUI atrapa esta excepcion y muestra su
 * mensaje al usuario (requisito 2.2: nunca un stack trace, nunca un fallo
 * silencioso). El mensaje siempre describe que se esperaba y que se recibio.
 */
public class ErrorDeEntrada extends RuntimeException {

    public ErrorDeEntrada(String mensaje) {
        super(mensaje);
    }
}
