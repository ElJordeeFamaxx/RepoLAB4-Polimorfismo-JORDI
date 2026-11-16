package acopiogt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
/** Identidad y claves de orden inmutables. Mutaciones limitadas al paquete. */
public abstract class Lote implements Comparable<Lote>, Almacenable {
    private final DatosLote datos;
    // Exactitud decimal en inventario: 0.1 + 0.2 no deja residuos binarios.
    private BigDecimal kgDisponibles;
    private EstadoLote estado;
    protected Lote(DatosLote datos) {
        this.datos = Validacion.noNulo(datos, "Datos");
        kgDisponibles = Dinero.decimal(datos.pesoRecibido());
        estado = EstadoLote.DISPONIBLE;
    }
    public final DatosLote getDatos() { return datos; }
    public final double getKgDisponibles() { return kgDisponibles.doubleValue(); }
    public final BigDecimal getKgExactos() { return kgDisponibles; }
    public final EstadoLote getEstado() { return estado; }
    public abstract String categoria();
    public abstract BigDecimal calcularPago();
    public abstract String condicion();
    public Optional<LocalDate> vencimiento() { return Optional.empty(); }
    public final boolean estaVencido(LocalDate fecha) {
        Validacion.noNulo(fecha, "Fecha");
        return vencimiento().map(fecha::isAfter).orElse(false);
    }
    public final BigDecimal costoPorKg() {
        return calcularPago().divide(Dinero.decimal(datos.pesoRecibido()), Dinero.PRECISION);
    }
    protected final BigDecimal pagoBase(String a, String b, String c) {
        String tarifa = switch (datos.calidad()) { case A -> a; case B -> b; case C -> c; };
        return Dinero.decimal(datos.pesoRecibido()).multiply(new BigDecimal(tarifa));
    }
    @Override public final int compareTo(Lote otro) {
        Optional<LocalDate> v1 = vencimiento(), v2 = otro.vencimiento();
        if (v1.isPresent() != v2.isPresent()) return v1.isPresent() ? -1 : 1;
        LocalDate f1 = v1.orElse(datos.fechaIngreso());
        LocalDate f2 = v2.orElse(otro.datos.fechaIngreso());
        int cmp = f1.compareTo(f2);
        if (cmp == 0) cmp = datos.calidad().compareTo(otro.datos.calidad());
        if (cmp == 0) cmp = datos.codigo().compareTo(otro.datos.codigo());
        return cmp;
    }
    final void retirar(double kg) { retirar(Dinero.decimal(Validacion.positivo(kg, "Retiro"))); }
    final void retirar(BigDecimal kg) {
        if (estado != EstadoLote.DISPONIBLE || kg.signum() <= 0 || kg.compareTo(kgDisponibles) > 0)
            throw new IllegalArgumentException("Retiro inválido para el lote " + datos.codigo());
        kgDisponibles = kgDisponibles.subtract(kg);
        if (kgDisponibles.signum() == 0) estado = EstadoLote.AGOTADO;
    }
    final BigDecimal descartar() {
        if (estado != EstadoLote.DISPONIBLE) throw new IllegalArgumentException("Solo se descartan lotes disponibles.");
        BigDecimal perdida = kgDisponibles.multiply(costoPorKg());
        kgDisponibles = BigDecimal.ZERO;
        estado = EstadoLote.DESCARTADO;
        return perdida;
    }
}
