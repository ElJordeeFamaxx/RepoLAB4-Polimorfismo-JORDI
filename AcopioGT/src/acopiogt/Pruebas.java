package acopiogt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/** Pruebas ejecutables sin JUnit ni dependencias externas. */
public final class Pruebas {
    private static int total;
    private static final LocalDate F = LocalDate.of(2026, 3, 5);
    private Pruebas() {}
    private static DatosLote d(String codigo, double peso, Calidad calidad) {
        return new DatosLote(codigo, "Ana", F, peso, calidad);
    }
    private static void igual(String caso, Object esperado, Object obtenido) {
        boolean ok = esperado instanceof BigDecimal && obtenido instanceof BigDecimal
            ? ((BigDecimal) esperado).compareTo((BigDecimal) obtenido) == 0
            : Objects.equals(esperado, obtenido);
        System.out.println("| " + (++total) + " | " + caso + " | " + esperado + " | " + obtenido + " | " + (ok ? "OK" : "FALLO") + " |");
        if (!ok) throw new AssertionError(caso + ": esperado=" + esperado + ", obtenido=" + obtenido);
    }
    private static void monto(String caso, String esperado, BigDecimal obtenido) { igual(caso, new BigDecimal(esperado), obtenido); }
    private static void rechazo(String caso, Runnable operacion) {
        boolean rechazado = false;
        try { operacion.run(); } catch (IllegalArgumentException | IllegalStateException e) { rechazado = true; }
        igual(caso, true, rechazado);
    }
    private static String foto(AcopioGT s) {
        StringBuilder b = new StringBuilder(s.getFechaOperacion().toString());
        for (Lote l : s.getHistorial()) b.append(l.getDatos()).append(l.getKgExactos()).append(l.getEstado()).append(s.bodegaOriginal(l.getDatos().codigo()));
        for (Bodega x : s.getBodegas()) {
            b.append(x.getCodigo()).append(x.ocupacionExacta());
            for (Lote l : x) b.append(l.getDatos().codigo());
        }
        return b.append(s.getMovimientos()).append(s.getDespachos()).append(s.totalesPorCategoria()).toString();
    }
    private static void rechazoSinCambios(String caso, AcopioGT s, Runnable operacion) {
        String antes = foto(s); rechazo(caso, operacion); igual(caso + ": estado idéntico", true, antes.equals(foto(s)));
    }
    private static AcopioGT vacio() {
        AcopioGT s = new AcopioGT(F);
        s.registrarBodega(new Bodega("S1", 1000, "SECA"));
        s.registrarBodega(new Bodega("S2", 1000, "SECA"));
        s.registrarBodega(new Bodega("F1", 1000, "FRIA"));
        return s;
    }
    private static String codigos(List<Lote> lotes) { return String.join(",", lotes.stream().map(l -> l.getDatos().codigo()).toList()); }
    public static void main(String[] args) {
        System.out.println("# Evidencia de pruebas de AcopioGT\n");
        System.out.println("Ejecución automática; importes sin redondeo interno. Los rechazos comparan fecha, inventario, bodegas, movimientos, despachos y totales.\n");
        System.out.println("| N.º | Entrada / caso | Esperado | Obtenido | Resultado |");
        System.out.println("|---|---|---|---|---|");
        monto("Café 500 kg A, humedad 13.4 %", "5640", new CafePergamino(d("P",500,Calidad.A),13.4).calcularPago());
        monto("Café 10 kg A, humedad 12 %", "120", new CafePergamino(d("P",10,Calidad.A),12).calcularPago());
        monto("Café 10 kg B, humedad 8 %", "90", new CafePergamino(d("P",10,Calidad.B),8).calcularPago());
        monto("Café 10 kg C, humedad 14 %", "56.4", new CafePergamino(d("P",10,Calidad.C),14).calcularPago());
        monto("Café 100 kg A, humedad 12.01 %", "1164", new CafePergamino(d("P",100,Calidad.A),12.01).calcularPago());
        monto("Café 100 kg A, humedad 13 %", "1164", new CafePergamino(d("P",100,Calidad.A),13).calcularPago());
        monto("Orgánico 500 kg A, humedad 13.4 %", "6015", new CafeOrganico(d("O",500,Calidad.A),13.4,"Sello",F).calcularPago());
        monto("Cardamomo 80 kg B, verde 90 %", "4224", new Cardamomo(d("C",80,Calidad.B),90).calcularPago());
        monto("Cardamomo 10 kg A, verde 85 %", "660", new Cardamomo(d("C",10,Calidad.A),85).calcularPago());
        monto("Cardamomo 10 kg C, verde 84.9 %", "350", new Cardamomo(d("C",10,Calidad.C),84.9).calcularPago());
        monto("Cardamomo 10 kg A, verde 0 % válido", "600", new Cardamomo(d("C",10,Calidad.A),0).calcularPago());
        monto("Cardamomo 10 kg A, verde 100 % válido", "660", new Cardamomo(d("C",10,Calidad.A),100).calcularPago());
        monto("Hortaliza 200 kg A", "760", new Hortaliza(d("H",200,Calidad.A),7).calcularPago());
        monto("Hortaliza 10 kg B", "28.5", new Hortaliza(d("H",10,Calidad.B),3).calcularPago());
        monto("Hortaliza 10 kg C", "19", new Hortaliza(d("H",10,Calidad.C),14).calcularPago());
        monto("Queso 40 kg A", "1460", new QuesoFresco(d("Q",40,Calidad.A),30).calcularPago());
        monto("Queso 10 kg B", "285", new QuesoFresco(d("Q",10,Calidad.B),10).calcularPago());
        rechazo("Peso cero", () -> d("X",0,Calidad.A));
        rechazo("Peso negativo", () -> d("X",-1,Calidad.A));
        rechazo("Peso NaN", () -> d("X",Double.NaN,Calidad.A));
        rechazo("Peso infinito", () -> d("X",Double.POSITIVE_INFINITY,Calidad.A));
        rechazo("Código vacío", () -> d(" ",10,Calidad.A));
        rechazo("Productor vacío", () -> new DatosLote("X"," ",F,1,Calidad.A));
        rechazo("Calidad nula", () -> d("X",10,null));
        rechazo("Café humedad 7.9", () -> new CafePergamino(d("X",10,Calidad.A),7.9));
        rechazo("Café humedad 14.1", () -> new CafePergamino(d("X",10,Calidad.A),14.1));
        rechazo("Humedad NaN", () -> new CafePergamino(d("X",10,Calidad.A),Double.NaN));
        rechazo("Orgánico sello anterior al ingreso", () -> new CafeOrganico(d("X",10,Calidad.A),10,"Sello",F.minusDays(1)));
        rechazo("Certificadora vacía", () -> new CafeOrganico(d("X",10,Calidad.A),10,"",F));
        rechazo("Verde negativo", () -> new Cardamomo(d("X",10,Calidad.A),-1));
        rechazo("Verde mayor que 100", () -> new Cardamomo(d("X",10,Calidad.A),101));
        rechazo("Vida útil 2", () -> new Hortaliza(d("X",10,Calidad.A),2));
        rechazo("Vida útil 15", () -> new Hortaliza(d("X",10,Calidad.A),15));
        rechazo("Queso calidad C", () -> new QuesoFresco(d("X",10,Calidad.C),20));
        rechazo("Grasa 9.9", () -> new QuesoFresco(d("X",10,Calidad.A),9.9));
        rechazo("Grasa 40.1", () -> new QuesoFresco(d("X",10,Calidad.A),40.1));
        rechazo("Capacidad cero", () -> new Bodega("S",0,"SECA"));
        rechazo("Pedido negativo", () -> new Pedido("Cliente",Categorias.CAFE,-1));
        rechazo("Pedido infinito", () -> new Pedido("Cliente",Categorias.CAFE,Double.POSITIVE_INFINITY));
        Hortaliza h = new Hortaliza(d("H",200,Calidad.A),7);
        igual("Ingreso 05/03 + 7 días", LocalDate.of(2026,3,12), h.fechaVencimiento());
        igual("Válido durante 12/03", false, h.estaVencido(LocalDate.of(2026,3,12)));
        igual("Vencido desde 13/03", true, h.estaVencido(LocalDate.of(2026,3,13)));
        igual("Cardamomo +180 días", F.plusDays(180), new Cardamomo(d("C",10,Calidad.A),85).fechaVencimiento());
        igual("Queso +21 días", F.plusDays(21), new QuesoFresco(d("Q",10,Calidad.A),40).fechaVencimiento());
        igual("Café no vence", Optional.empty(), new CafePergamino(d("P",10,Calidad.A),10).vencimiento());
        igual("Orgánico no vence aunque expire sello", false, new CafeOrganico(d("O",10,Calidad.A),10,"S",F).estaVencido(F.plusYears(5)));
        AcopioGT s = vacio(); String antes = foto(s);
        monto("Preliquidación H 200 kg A", "760", s.preliquidar(h).pago());
        igual("Preliquidación no registra nada", true, antes.equals(foto(s)));
        rechazoSinCambios("Hortaliza en bodega seca", s, () -> s.recibir(h,"S1"));
        CafePergamino p = new CafePergamino(d("P",500,Calidad.A),13.4);
        rechazoSinCambios("Café en cámara fría", s, () -> s.recibir(p,"F1"));
        rechazoSinCambios("Bodega inexistente", s, () -> s.recibir(p,"NO"));
        rechazoSinCambios("Lote excede capacidad", s, () -> s.recibir(new CafePergamino(d("GR",1001,Calidad.A),10),"S1"));
        rechazoSinCambios("Bodega duplicada", s, () -> s.registrarBodega(new Bodega("S1",300,"SECA")));
        rechazoSinCambios("Ingreso anterior al día operativo", s, () -> s.recibir(new CafePergamino(new DatosLote("Y","Ana",F.minusDays(1),10,Calidad.A),10),"S1"));
        rechazoSinCambios("Ingreso futuro", s, () -> s.recibir(new CafePergamino(new DatosLote("Y","Ana",F.plusDays(1),10,Calidad.A),10),"S1"));
        s.recibir(p,"S1"); s.recibir(h,"F1");
        monto("Pago completo café recibido", "5640", s.totalesPorCategoria().get(Categorias.CAFE).pagado());
        monto("Pago completo hortaliza recibida", "760", s.totalesPorCategoria().get(Categorias.HORTALIZA).pagado());
        monto("Merma no reduce inventario", "200", h.getKgExactos());
        rechazoSinCambios("Código de lote repetido", s, () -> s.recibir(new CafePergamino(d("P",1,Calidad.A),10),"S2"));
        rechazoSinCambios("Pedido mayor al stock", s, () -> s.despachar(new Pedido("Cliente",Categorias.CAFE,501)));
        rechazoSinCambios("Categoría sin existencias", s, () -> s.despachar(new Pedido("Cliente",Categorias.ORGANICO,1)));
        rechazoSinCambios("Retroceder fecha", s, () -> s.avanzarFecha(F.minusDays(1)));
        rechazoSinCambios("Avanzar a la misma fecha", s, () -> s.avanzarFecha(F));
        monto("100 kg café a costo 11.28 +35 %", "1522.8", s.despachar(new Pedido("Cliente",Categorias.CAFE,100)).total());
        monto("Café restante después del despacho", "400", p.getKgExactos());
        igual("Estado después de despacho parcial", EstadoLote.DISPONIBLE, p.getEstado());
        s.despachar(new Pedido("Cliente",Categorias.HORTALIZA,150));
        s.avanzarFecha(LocalDate.of(2026,3,12));
        igual("Hortaliza sigue disponible el día de vencer", EstadoLote.DISPONIBLE,h.getEstado());
        s.avanzarFecha(LocalDate.of(2026,3,13));
        igual("Hortaliza descartada al siguiente día", EstadoLote.DESCARTADO,h.getEstado());
        monto("Pérdida de 50 kg a Q3.80", "190", s.totalesPorCategoria().get(Categorias.HORTALIZA).perdido());
        igual("Descarte libera bodega", 0,s.contenido("F1").size());
        monto("Lote descartado queda con cero", "0", h.getKgExactos());
        s.avanzarFecha(LocalDate.of(2026,3,14));
        monto("Pérdida no se duplica", "190", s.totalesPorCategoria().get(Categorias.HORTALIZA).perdido());
        AcopioGT o = vacio();
        o.recibir(new CafePergamino(d("Z",100,Calidad.B),12),"S1");
        o.recibir(new CafePergamino(d("B",100,Calidad.A),12),"S2");
        o.recibir(new CafePergamino(d("A",100,Calidad.A),12),"S1");
        o.recibir(new Cardamomo(d("C",10,Calidad.A),80),"S1");
        igual("Bodega: perecedero antes de café", "C,A,Z", codigos(o.contenido("S1")));
        igual("Orden por calidad y código global", "A,B,Z", codigos(o.existencias(Categorias.CAFE)));
        Despacho mezcla = o.despachar(new Pedido("Comprador",Categorias.CAFE,250));
        igual("Despacho mezcla bodegas en orden global", List.of("A","B","Z"), mezcla.detalles().stream().map(DetalleDespacho::codigoLote).toList());
        monto("Despacho 100 A +100 A +50 B", "3847.5", mezcla.total());
        igual("Lotes agotados salen de bodega S2", 0,o.contenido("S2").size());
        igual("Historial conserva agotados", 4,o.getHistorial().size());
        igual("Estado agotado", EstadoLote.AGOTADO,o.getHistorial().stream().filter(l -> l.getDatos().codigo().equals("A")).findFirst().orElseThrow().getEstado());
        rechazoSinCambios("Código agotado no se reutiliza", o, () -> o.recibir(new CafePergamino(d("A",1,Calidad.A),10),"S2"));
        AcopioGT ord = new AcopioGT(F);
        Map<Lote,String> ini = new LinkedHashMap<>();
        ini.put(new CafePergamino(new DatosLote("Z","Ana",F.minusDays(1),1,Calidad.C),10),"S");
        ini.put(new CafePergamino(d("A",1,Calidad.A),10),"S");
        ini.put(new Cardamomo(new DatosLote("P2","Ana",F.minusDays(100),1,Calidad.A),80),"S");
        ini.put(new Cardamomo(new DatosLote("P1","Ana",F.minusDays(179),1,Calidad.C),80),"S");
        ord.cargarInicial(List.of(new Bodega("S",100,"SECA")),ini);
        igual("Orden completo: vencimiento, luego ingreso", "P1,P2,Z,A",codigos(ord.contenido("S")));
        AcopioGT dec = new AcopioGT(F); dec.registrarBodega(new Bodega("S",0.3,"SECA"));
        dec.recibir(new CafePergamino(d("A",0.1,Calidad.A),10),"S");
        dec.recibir(new CafePergamino(d("B",0.2,Calidad.A),10),"S");
        monto("Capacidad exacta 0.1 +0.2", "0.3",dec.getBodegas().get(0).ocupacionExacta());
        rechazoSinCambios("Capacidad ocupada acumulada", dec, () -> dec.recibir(new CafePergamino(d("C",0.01,Calidad.A),10),"S"));
        monto("Despacho exacto 0.3 kg", "4.86",dec.despachar(new Pedido("Cliente",Categorias.CAFE,0.3)).total());
        igual("Sin residuos decimales",0,dec.contenido("S").size());
        dec.recibir(new CafePergamino(d("D",0.3,Calidad.A),10),"S");
        dec.despachar(new Pedido("Cliente",Categorias.CAFE,0.1));
        dec.despachar(new Pedido("Cliente",Categorias.CAFE,0.2));
        igual("Agotamiento exacto en dos despachos",0,dec.contenido("S").size());
        AcopioGT ejemplo = DatosEjemplo.crear();
        igual("Carga inicial: 10 lotes",10,ejemplo.getHistorial().size());
        igual("Carga inicial: 3 bodegas",3,ejemplo.getBodegas().size());
        igual("Carga inicial sin movimientos",0,ejemplo.getMovimientos().size());
        for (String c : ejemplo.totalesPorCategoria().keySet()) {
            igual("Dos lotes iniciales de " + c,2,ejemplo.existencias(c).size());
            monto("Pagos iniciales " + c,"0",ejemplo.totalesPorCategoria().get(c).pagado());
        }
        igual("Vencen entre 12 y 15 de marzo", "H01,Q01,C01",codigos(ejemplo.proximosAVencer()));
        ejemplo.avanzarFecha(LocalDate.of(2026,4,30));
        igual("Retira todos los vencidos durante recorrido",0,ejemplo.contenido("F1").size());
        monto("Pérdida hortalizas iniciales", "1045",ejemplo.totalesPorCategoria().get(Categorias.HORTALIZA).perdido());
        monto("Pérdida quesos iniciales", "3170",ejemplo.totalesPorCategoria().get(Categorias.QUESO).perdido());
        monto("Pérdida cardamomo inicial vencido", "4224",ejemplo.totalesPorCategoria().get(Categorias.CARDAMOMO).perdido());
        igual("Cafés no vencen",2,ejemplo.existencias(Categorias.CAFE).size());
        rechazoSinCambios("Código descartado no se reutiliza",ejemplo, () -> ejemplo.recibir(new Hortaliza(new DatosLote("H01","Ana",ejemplo.getFechaOperacion(),1,Calidad.A),7),"F1"));
        AcopioGT carga = new AcopioGT(F);
        Bodega b = new Bodega("S",10,"SECA");
        Map<Lote,String> excesivos = new LinkedHashMap<>();
        excesivos.put(new CafePergamino(d("A",6,Calidad.A),10),"S");
        excesivos.put(new CafePergamino(d("B",6,Calidad.A),10),"S");
        rechazoSinCambios("Carga inicial excede capacidad acumulada",carga, () -> carga.cargarInicial(List.of(b),excesivos));
        igual("Carga fallida deja bodega externa vacía",false,b.iterator().hasNext());
        Map<Lote,String> repetidos = new LinkedHashMap<>();
        repetidos.put(new CafePergamino(d("A",1,Calidad.A),10),"S");
        repetidos.put(new CafePergamino(d("A",1,Calidad.B),10),"S");
        rechazoSinCambios("Carga inicial con códigos duplicados",carga, () -> carga.cargarInicial(List.of(b),repetidos));
        rechazoSinCambios("Carga inicial con vencido",carga, () -> carga.cargarInicial(List.of(new Bodega("F",100,"FRIA")), Map.of(new Hortaliza(new DatosLote("H","Ana",F.minusDays(8),1,Calidad.A),7),"F")));
        Iterator<Lote> it = o.getBodegas().get(0).iterator();
        rechazo("Iterator.remove sin next", it::remove);
        it.next(); rechazo("Iterator no retira un lote aún disponible", it::remove);
        Bodega local = new Bodega("F",100,"FRIA");
        Hortaliza l1 = new Hortaliza(d("I1",1,Calidad.A),3), l2 = new Hortaliza(d("I2",1,Calidad.A),3);
        local.almacenar(l1);local.almacenar(l2);
        Iterator<Lote> iterador = local.iterator(); iterador.next().descartar(); iterador.remove();
        rechazo("Iterator.remove dos veces", iterador::remove);
        igual("No salta siguiente lote después de remove", "I2", iterador.next().getDatos().codigo());
        rechazo("No elimina el segundo lote disponible", iterador::remove);
        System.out.println("\nTotal: " + total + " comprobaciones correctas.");
    }
}
