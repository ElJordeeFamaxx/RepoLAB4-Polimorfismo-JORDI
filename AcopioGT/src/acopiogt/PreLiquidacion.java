package acopiogt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
public record PreLiquidacion(BigDecimal pago, Optional<LocalDate> vencimiento) {
    public PreLiquidacion { Validacion.noNulo(pago, "Pago"); Validacion.noNulo(vencimiento, "Vencimiento"); }
}
