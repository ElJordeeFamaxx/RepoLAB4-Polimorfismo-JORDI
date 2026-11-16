package acopiogt;

import java.math.BigDecimal;
public final class QuesoFresco extends LotePerecedero implements Refrigerado {
    private final double porcentajeGrasa;
    public QuesoFresco(DatosLote datos, double grasa) {
        super(datos, 21);
        porcentajeGrasa = Validacion.rango(grasa, 10, 40, "Grasa");
        if (datos.calidad() == Calidad.C) throw new IllegalArgumentException("No se acepta queso de calidad C.");
    }
    @Override public String categoria() { return Categorias.QUESO; }
    @Override public String condicion() { return condicionFria(); }
    @Override public BigDecimal calcularPago() {
        return pagoBase("38", "30", "0").subtract(Dinero.decimal(getDatos().pesoRecibido()).multiply(new BigDecimal("1.50")));
    }
}
