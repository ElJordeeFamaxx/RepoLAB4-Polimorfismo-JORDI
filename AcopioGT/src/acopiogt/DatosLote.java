package acopiogt;

import java.time.LocalDate;
public record DatosLote(String codigo, String productor, LocalDate fechaIngreso, double pesoRecibido, Calidad calidad) {
    public DatosLote {
        codigo = Validacion.texto(codigo, "Código");
        productor = Validacion.texto(productor, "Productor");
        Validacion.noNulo(fechaIngreso, "Fecha de ingreso");
        Validacion.positivo(pesoRecibido, "Peso recibido");
        Validacion.noNulo(calidad, "Calidad");
    }
}
