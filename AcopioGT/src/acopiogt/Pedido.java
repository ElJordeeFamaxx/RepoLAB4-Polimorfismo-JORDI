package acopiogt;

public record Pedido(String comprador, String categoria, double kg) {
    public Pedido {
        comprador = Validacion.texto(comprador, "Comprador");
        categoria = Validacion.texto(categoria, "Categoría");
        Validacion.positivo(kg, "Cantidad solicitada");
    }
}
