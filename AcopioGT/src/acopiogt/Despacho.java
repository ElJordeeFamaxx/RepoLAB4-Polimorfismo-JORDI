package acopiogt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
public record Despacho(LocalDate fecha, Pedido pedido, List<DetalleDespacho> detalles) {
    public Despacho {
        Validacion.noNulo(fecha, "Fecha"); Validacion.noNulo(pedido, "Pedido");
        detalles = List.copyOf(detalles);
        if (detalles.isEmpty()) throw new IllegalArgumentException("Despacho sin detalles.");
    }
    public BigDecimal total() { return detalles.stream().map(DetalleDespacho::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add); }
}
