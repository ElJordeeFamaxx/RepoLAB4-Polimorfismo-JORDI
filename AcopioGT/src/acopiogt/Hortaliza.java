package acopiogt;

import java.math.BigDecimal;
public final class Hortaliza extends LotePerecedero implements Refrigerado {
    public Hortaliza(DatosLote datos, int vidaUtilDias) {
        super(datos, vidaUtilDias);
        Validacion.rango(vidaUtilDias, 3, 14, "Vida útil");
    }
    @Override public String categoria() { return Categorias.HORTALIZA; }
    @Override public String condicion() { return condicionFria(); }
    @Override public BigDecimal calcularPago() { return pagoBase("4", "3", "2").multiply(new BigDecimal("0.95")); }
}
