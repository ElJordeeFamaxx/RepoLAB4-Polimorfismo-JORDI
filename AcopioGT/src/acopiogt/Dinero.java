package acopiogt;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
public final class Dinero {
    public static final MathContext PRECISION = MathContext.DECIMAL128;
    private Dinero() {}
    public static BigDecimal decimal(double valor) { return BigDecimal.valueOf(valor); }
    public static String mostrar(BigDecimal monto) { return "Q" + monto.setScale(2, RoundingMode.HALF_UP).toPlainString(); }
    public static String kg(BigDecimal valor) { return valor.stripTrailingZeros().toPlainString(); }
}
