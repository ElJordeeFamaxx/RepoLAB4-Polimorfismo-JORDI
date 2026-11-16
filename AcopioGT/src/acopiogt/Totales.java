package acopiogt;

import java.math.BigDecimal;
public record Totales(BigDecimal pagado, BigDecimal cobrado, BigDecimal perdido) {
    public static Totales cero() { return new Totales(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO); }
    public Totales agregar(Movimiento m) {
        return switch (m.tipo()) {
            case PAGO -> new Totales(pagado.add(m.monto()), cobrado, perdido);
            case COBRO -> new Totales(pagado, cobrado.add(m.monto()), perdido);
            case PERDIDA -> new Totales(pagado, cobrado, perdido.add(m.monto()));
        };
    }
}
