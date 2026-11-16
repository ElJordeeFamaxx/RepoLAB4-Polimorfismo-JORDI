package acopiogt;

import java.math.BigDecimal;
import java.math.RoundingMode;
public class CafePergamino extends Lote {
    private final double humedad;
    public CafePergamino(DatosLote datos, double humedad) {
        super(datos);
        this.humedad = Validacion.rango(humedad, 8, 14, "Humedad");
    }
    @Override public String categoria() { return Categorias.CAFE; }
    @Override public String condicion() { return "SECA"; }
    @Override public BigDecimal calcularPago() {
        BigDecimal puntos = Dinero.decimal(humedad).subtract(new BigDecimal("12"))
            .max(BigDecimal.ZERO).setScale(0, RoundingMode.CEILING);
        return pagoBase("12", "9", "6").multiply(BigDecimal.ONE.subtract(puntos.multiply(new BigDecimal("0.03"))));
    }
}
