package acopiogt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
public final class Bodega implements Iterable<Lote> {
    private final String codigo;
    private final double capacidadKg;
    private final String condicion;
    private final List<Lote> lotes = new ArrayList<>();
    public Bodega(String codigo, double capacidadKg, String condicion) {
        this.codigo = Validacion.texto(codigo, "Código de bodega");
        this.capacidadKg = Validacion.positivo(capacidadKg, "Capacidad");
        this.condicion = Validacion.texto(condicion, "Condición").toUpperCase(Locale.ROOT);
    }
    public String getCodigo() { return codigo; }
    public double getCapacidadKg() { return capacidadKg; }
    public String getCondicion() { return condicion; }
    public double ocupacionKg() { return ocupacionExacta().doubleValue(); }
    BigDecimal ocupacionExacta() { return lotes.stream().map(Lote::getKgExactos).reduce(BigDecimal.ZERO, BigDecimal::add); }
    public void validarIngreso(Lote lote) {
        Validacion.noNulo(lote, "Lote");
        if (!condicion.equals(lote.condicion())) throw new IllegalArgumentException("El lote requiere " + lote.condicion() + "; bodega " + codigo + " es " + condicion + ".");
        if (lote.getEstado() != EstadoLote.DISPONIBLE || lote.getKgExactos().compareTo(Dinero.decimal(lote.getDatos().pesoRecibido())) != 0)
            throw new IllegalArgumentException("El lote nuevo debe estar íntegro y disponible.");
        if (lotes.stream().anyMatch(l -> l.getDatos().codigo().equals(lote.getDatos().codigo())))
            throw new IllegalArgumentException("Código de lote repetido en bodega.");
        if (ocupacionExacta().add(lote.getKgExactos()).compareTo(Dinero.decimal(capacidadKg)) > 0)
            throw new IllegalArgumentException("Capacidad insuficiente en bodega " + codigo + ".");
    }
    void almacenar(Lote lote) { validarIngreso(lote); lotes.add(lote); }
    void retirar(Lote lote) { lotes.remove(lote); }
    @Override public Iterator<Lote> iterator() {
        List<Lote> copia = new ArrayList<>(lotes);
        copia.sort(null);
        return new IteradorBodega(this, copia);
    }
}
