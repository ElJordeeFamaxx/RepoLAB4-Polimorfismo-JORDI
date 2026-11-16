package acopiogt;

import java.time.LocalDate;
import java.util.Optional;
public abstract class LotePerecedero extends Lote implements Perecedero {
    private final int vidaUtilDias;
    protected LotePerecedero(DatosLote datos, int dias) {
        super(datos);
        if (dias <= 0) throw new IllegalArgumentException("Vida útil debe ser positiva.");
        vidaUtilDias = dias;
        datos.fechaIngreso().plusDays(dias); // Detecta fechas fuera del rango antes de registrar.
    }
    @Override public final LocalDate fechaVencimiento() { return getDatos().fechaIngreso().plusDays(vidaUtilDias); }
    @Override public final Optional<LocalDate> vencimiento() { return Optional.of(fechaVencimiento()); }
}
