package acopiogt;

/** Validaciones compartidas; no contiene decisiones por categoría. */
final class Validacion {
    private Validacion() {}
    static String texto(String valor, String campo) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException(campo + " no puede estar vacío.");
        return valor.trim();
    }
    static double positivo(double valor, String campo) {
        if (!Double.isFinite(valor) || valor <= 0) throw new IllegalArgumentException(campo + " debe ser finito y mayor que cero.");
        return valor;
    }
    static double rango(double valor, double min, double max, String campo) {
        if (!Double.isFinite(valor) || valor < min || valor > max)
            throw new IllegalArgumentException(campo + " debe estar entre " + min + " y " + max + ".");
        return valor;
    }
    static <T> T noNulo(T valor, String campo) {
        if (valor == null) throw new IllegalArgumentException(campo + " es obligatorio.");
        return valor;
    }
}
