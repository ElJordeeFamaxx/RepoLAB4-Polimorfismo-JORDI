package acopiogt;

import java.math.BigDecimal;
public final class Cardamomo extends LotePerecedero {
    private final double porcentajeVerde;
    public Cardamomo(DatosLote datos, double verde) {
        super(datos, 180);
        porcentajeVerde = Validacion.rango(verde, 0, 100, "Grano verde");
    }
    @Override public String categoria() { return Categorias.CARDAMOMO; }
    @Override public String condicion() { return "SECA"; }
    @Override public BigDecimal calcularPago() {
        return pagoBase("60", "48", "35").multiply(new BigDecimal(porcentajeVerde >= 85 ? "1.10" : "1"));
    }
}
