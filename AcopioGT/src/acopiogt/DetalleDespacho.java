package acopiogt;

import java.math.BigDecimal;
public record DetalleDespacho(String codigoLote, BigDecimal kg, BigDecimal costoKg) {
    public DetalleDespacho {
        codigoLote = Validacion.texto(codigoLote, "Código de lote");
        Validacion.noNulo(kg, "Kilogramos"); Validacion.noNulo(costoKg, "Costo");
        if (kg.signum() <= 0 || costoKg.signum() < 0) throw new IllegalArgumentException("Detalle inválido.");
    }
    public DetalleDespacho(String codigoLote, double kg, BigDecimal costoKg) {
        this(codigoLote, Dinero.decimal(Validacion.positivo(kg, "Kilogramos")), costoKg);
    }
    public BigDecimal subtotal() { return kg.multiply(costoKg).multiply(new BigDecimal("1.35")); }
}
