package acopiogt;

import java.math.BigDecimal;
import java.time.LocalDate;
public record Movimiento(LocalDate fecha, String categoria, TipoMovimiento tipo, BigDecimal monto) {
    public Movimiento {
        Validacion.noNulo(fecha, "Fecha"); categoria = Validacion.texto(categoria, "Categoría");
        Validacion.noNulo(tipo, "Tipo"); Validacion.noNulo(monto, "Monto");
        if (monto.signum() < 0) throw new IllegalArgumentException("Monto negativo.");
    }
}
