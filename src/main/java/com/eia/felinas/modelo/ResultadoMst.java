package com.eia.felinas.modelo;

import java.util.List;

/**
 * Resultado de Kruskal: si las intersecciones quedaron conectadas, el costo
 * total del arbol de expansion minima y los cables (aristas) seleccionados
 * para que la visualizacion los resalte.
 */
public final class ResultadoMst {

    private final boolean conectado;
    private final long costoTotal;
    private final List<Arista> seleccionadas;

    public ResultadoMst(boolean conectado, long costoTotal, List<Arista> seleccionadas) {
        this.conectado = conectado;
        this.costoTotal = costoTotal;
        this.seleccionadas = seleccionadas;
    }

    /** True si el arbol conecta todas las intersecciones (N-1 cables usados, o N <= 1). */
    public boolean conectado() {
        return conectado;
    }

    /** Costo total del arbol de expansion minima; solo valido si conectado(). */
    public long costoTotal() {
        return costoTotal;
    }

    /** Cables (aristas) que forman el MST, en el orden en que Kruskal los acepto. */
    public List<Arista> seleccionadas() {
        return seleccionadas;
    }
}
