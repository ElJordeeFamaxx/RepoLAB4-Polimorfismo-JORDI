package acopiogt;

import java.math.BigDecimal;
import java.time.LocalDate;
public final class CafeOrganico extends CafePergamino {
    private final String certificadora;
    private final LocalDate selloValidoHasta;
    public CafeOrganico(DatosLote datos, double humedad, String certificadora, LocalDate sello) {
        super(datos, humedad);
        this.certificadora = Validacion.texto(certificadora, "Certificadora");
        selloValidoHasta = Validacion.noNulo(sello, "Vigencia del sello");
        if (sello.isBefore(datos.fechaIngreso())) throw new IllegalArgumentException("El sello está vencido en la fecha de ingreso.");
    }
    @Override public String categoria() { return Categorias.ORGANICO; }
    @Override public BigDecimal calcularPago() {
        return super.calcularPago().add(Dinero.decimal(getDatos().pesoRecibido()).multiply(new BigDecimal("0.75")));
    }
}
