package acopiogt;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
public final class IteradorBodega implements Iterator<Lote> {
    private final Bodega bodega;
    private final List<Lote> ordenados;
    private int posicion;
    private Lote ultimo;
    private boolean puedeEliminar;
    IteradorBodega(Bodega bodega, List<Lote> copia) { this.bodega = bodega; ordenados = copia; }
    @Override public boolean hasNext() { return posicion < ordenados.size(); }
    @Override public Lote next() {
        if (!hasNext()) throw new NoSuchElementException("Fin del recorrido.");
        ultimo = ordenados.get(posicion++); puedeEliminar = true; return ultimo;
    }
    @Override public void remove() {
        if (!puedeEliminar) throw new IllegalStateException("Debe llamar next() antes de remove().");
        // Solo retira lotes cuyo cambio de estado ya coordinó el servicio.
        if (ultimo.getEstado() == EstadoLote.DISPONIBLE)
            throw new IllegalStateException("No se puede retirar del inventario un lote todavía disponible.");
        bodega.retirar(ultimo); ordenados.remove(--posicion); puedeEliminar = false;
    }
}
