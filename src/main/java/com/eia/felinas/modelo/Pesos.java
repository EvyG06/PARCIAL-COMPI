package com.eia.felinas.modelo;

/**
 * Centinela compartido para "no existe ruta".
 *
 * Regla de la seccion 2.1 del enunciado: NUNCA se hace aritmetica sobre este
 * valor. Todo algoritmo que sume pesos debe comprobar primero que ninguno de
 * los operandos sea SIN_RUTA; sumarle algo a Long.MIN_VALUE desborda y
 * produce resultados absurdos silenciosamente.
 */
public final class Pesos {

    public static final long SIN_RUTA = Long.MIN_VALUE;

    private Pesos() {
    }
}
