package acopiogt;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public final class DatosEjemplo {
    private DatosEjemplo() {}
    private static DatosLote d(String codigo, LocalDate fecha, double kg, Calidad calidad) {
        return new DatosLote(codigo, "Productor " + codigo, fecha, kg, calidad);
    }
    public static AcopioGT crear() {
        LocalDate f = LocalDate.of(2026, 3, 12);
        AcopioGT s = new AcopioGT(f);
        List<Bodega> bodegas = List.of(new Bodega("S1", 3000, "SECA"), new Bodega("S2", 3000, "SECA"), new Bodega("F1", 2000, "FRIA"));
        Map<Lote, String> lotes = new LinkedHashMap<>();
        // Empate por fecha: A sale antes que B aunque el código A01 sea menor.
        lotes.put(new CafePergamino(d("Z01", f.minusDays(10), 500, Calidad.A), 13.4), "S2");
        lotes.put(new CafePergamino(d("A01", f.minusDays(10), 100, Calidad.B), 12), "S1");
        lotes.put(new CafeOrganico(d("O01", f.minusDays(8), 500, Calidad.A), 13.4, "CertiGT", f.plusYears(1)), "S1");
        lotes.put(new CafeOrganico(d("O02", f.minusDays(3), 100, Calidad.C), 10, "CertiGT", f), "S2");
        lotes.put(new Cardamomo(d("C01", f.minusDays(179), 80, Calidad.B), 90), "S1");
        lotes.put(new Cardamomo(d("C02", f.minusDays(10), 50, Calidad.C), 80), "S2");
        lotes.put(new Hortaliza(d("H01", f.minusDays(7), 200, Calidad.A), 7), "F1");
        lotes.put(new Hortaliza(d("H02", f.minusDays(1), 100, Calidad.B), 7), "F1");
        lotes.put(new QuesoFresco(d("Q01", f.minusDays(20), 40, Calidad.A), 30), "F1");
        lotes.put(new QuesoFresco(d("Q02", f.minusDays(2), 60, Calidad.B), 20), "F1");
        s.cargarInicial(bodegas, lotes);
        return s;
    }
}
