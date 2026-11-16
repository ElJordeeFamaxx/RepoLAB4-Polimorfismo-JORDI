package acopiogt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
/** Servicio de consola monohilo: valida y prepara antes de aplicar cambios. */
public final class AcopioGT {
    private LocalDate fechaOperacion;
    private final Map<String, Bodega> bodegas = new LinkedHashMap<>();
    private final Map<String, Lote> historial = new LinkedHashMap<>();
    private final Map<String, String> ubicacionOriginal = new LinkedHashMap<>();
    private final List<Movimiento> movimientos = new ArrayList<>();
    private final List<Despacho> despachos = new ArrayList<>();
    public AcopioGT(LocalDate fechaInicial) { fechaOperacion = Validacion.noNulo(fechaInicial, "Fecha inicial"); }
    public LocalDate getFechaOperacion() { return fechaOperacion; }
    public List<Bodega> getBodegas() { return List.copyOf(bodegas.values()); }
    public List<Movimiento> getMovimientos() { return List.copyOf(movimientos); }
    public List<Despacho> getDespachos() { return List.copyOf(despachos); }
    public List<Lote> getHistorial() { return historial.values().stream().sorted().toList(); }
    public String bodegaOriginal(String codigoLote) {
        if (!ubicacionOriginal.containsKey(codigoLote)) throw new IllegalArgumentException("Lote no registrado.");
        return ubicacionOriginal.get(codigoLote);
    }
    public void registrarBodega(Bodega b) {
        Validacion.noNulo(b, "Bodega");
        if (bodegas.containsKey(b.getCodigo())) throw new IllegalArgumentException("Código de bodega repetido.");
        if (b.iterator().hasNext()) throw new IllegalArgumentException("La nueva bodega debe estar vacía.");
        bodegas.put(b.getCodigo(), b);
    }
    private void validarCandidato(Lote l, boolean inicial) {
        Validacion.noNulo(l, "Lote");
        if (historial.containsKey(l.getDatos().codigo())) throw new IllegalArgumentException("Código de lote repetido, incluso en el historial.");
        if (inicial ? l.getDatos().fechaIngreso().isAfter(fechaOperacion) : !l.getDatos().fechaIngreso().equals(fechaOperacion))
            throw new IllegalArgumentException("Fecha de ingreso incompatible con la fecha de operación.");
        if (l.estaVencido(fechaOperacion)) throw new IllegalArgumentException("No se recibe un lote vencido.");
        if (l.getEstado() != EstadoLote.DISPONIBLE || l.getKgExactos().compareTo(Dinero.decimal(l.getDatos().pesoRecibido())) != 0)
            throw new IllegalArgumentException("El lote nuevo debe estar íntegro y disponible.");
        Validacion.texto(l.categoria(), "Categoría"); Validacion.texto(l.condicion(), "Condición");
        if (l.calcularPago().signum() < 0) throw new IllegalArgumentException("Pago inválido.");
    }
    private Bodega buscarBodega(String codigo) {
        Bodega b = bodegas.get(codigo);
        if (b == null) throw new IllegalArgumentException("Bodega inexistente: " + codigo);
        return b;
    }
    private void validarNuevo(Lote lote, Bodega b) { validarCandidato(lote, false); b.validarIngreso(lote); }
    public PreLiquidacion preliquidar(Lote candidato) {
        validarCandidato(candidato, false);
        return new PreLiquidacion(candidato.calcularPago(), candidato.vencimiento());
    }
    public void recibir(Lote candidato, String codigoBodega) {
        Bodega b = buscarBodega(codigoBodega);
        validarNuevo(candidato, b);
        Movimiento pago = new Movimiento(fechaOperacion, candidato.categoria(), TipoMovimiento.PAGO, candidato.calcularPago());
        b.almacenar(candidato);
        historial.put(candidato.getDatos().codigo(), candidato);
        ubicacionOriginal.put(candidato.getDatos().codigo(), codigoBodega);
        movimientos.add(pago);
    }
    private List<Lote> lotesOrdenados() {
        List<Lote> todos = new ArrayList<>();
        for (Bodega b : bodegas.values()) for (Lote l : b) todos.add(l);
        todos.sort(null);
        return List.copyOf(todos);
    }
    public List<Lote> existencias(String categoria) {
        Validacion.texto(categoria, "Categoría");
        return lotesOrdenados().stream().filter(l -> l.categoria().equals(categoria)).toList();
    }
    public List<Lote> contenido(String codigoBodega) {
        List<Lote> resultado = new ArrayList<>();
        buscarBodega(codigoBodega).forEach(resultado::add);
        return List.copyOf(resultado);
    }
    private List<DetalleDespacho> planificar(Pedido p) {
        Validacion.noNulo(p, "Pedido");
        BigDecimal falta = Dinero.decimal(p.kg());
        List<DetalleDespacho> plan = new ArrayList<>();
        for (Lote l : existencias(p.categoria())) {
            if (l.getEstado() != EstadoLote.DISPONIBLE || l.estaVencido(fechaOperacion)) continue;
            BigDecimal aporte = falta.min(l.getKgExactos());
            plan.add(new DetalleDespacho(l.getDatos().codigo(), aporte, l.costoPorKg()));
            falta = falta.subtract(aporte);
            if (falta.signum() == 0) break;
        }
        if (falta.signum() > 0) throw new IllegalArgumentException("Existencias insuficientes. No se descontó ningún kilogramo.");
        return plan;
    }
    public Despacho despachar(Pedido pedido) {
        List<DetalleDespacho> plan = planificar(pedido);
        Despacho resultado = new Despacho(fechaOperacion, pedido, plan);
        Movimiento cobro = new Movimiento(fechaOperacion, pedido.categoria(), TipoMovimiento.COBRO, resultado.total());
        for (DetalleDespacho d : plan) {
            Lote l = historial.get(d.codigoLote());
            l.retirar(d.kg());
            if (l.getEstado() == EstadoLote.AGOTADO) buscarBodega(ubicacionOriginal.get(d.codigoLote())).retirar(l);
        }
        despachos.add(resultado); movimientos.add(cobro);
        return resultado;
    }
    public void avanzarFecha(LocalDate nueva) {
        Validacion.noNulo(nueva, "Nueva fecha");
        if (!nueva.isAfter(fechaOperacion)) throw new IllegalArgumentException("La nueva fecha debe ser posterior a la actual.");
        // Se calculan todas las pérdidas antes de cambiar el inventario o la fecha.
        List<Movimiento> perdidas = new ArrayList<>();
        for (Lote l : lotesOrdenados()) if (l.estaVencido(nueva))
            perdidas.add(new Movimiento(nueva, l.categoria(), TipoMovimiento.PERDIDA, l.getKgExactos().multiply(l.costoPorKg())));
        for (Bodega b : bodegas.values()) {
            Iterator<Lote> it = b.iterator();
            while (it.hasNext()) {
                Lote l = it.next();
                if (l.estaVencido(nueva)) { l.descartar(); it.remove(); }
            }
        }
        fechaOperacion = nueva; movimientos.addAll(perdidas);
    }
    public List<Lote> proximosAVencer() {
        LocalDate limite = fechaOperacion.plusDays(3);
        return lotesOrdenados().stream().filter(l -> l.vencimiento()
            .map(v -> !v.isBefore(fechaOperacion) && !v.isAfter(limite)).orElse(false)).toList();
    }
    public Map<String, Totales> totalesPorCategoria() {
        Map<String, Totales> resultado = new LinkedHashMap<>();
        for (String c : List.of(Categorias.CAFE, Categorias.ORGANICO, Categorias.CARDAMOMO, Categorias.HORTALIZA, Categorias.QUESO)) resultado.put(c, Totales.cero());
        for (Lote l : historial.values()) resultado.putIfAbsent(l.categoria(), Totales.cero());
        for (Movimiento m : movimientos) resultado.put(m.categoria(), resultado.getOrDefault(m.categoria(), Totales.cero()).agregar(m));
        return Collections.unmodifiableMap(resultado);
    }
    /** Carga atómica de un conjunto inicial; no utiliza recibir() ni registra pagos. */
    public void cargarInicial(List<Bodega> nuevas, Map<Lote, String> lotes) {
        if (!bodegas.isEmpty() || !historial.isEmpty()) throw new IllegalArgumentException("La carga inicial solo se permite en un sistema vacío.");
        Validacion.noNulo(nuevas, "Bodegas"); Validacion.noNulo(lotes, "Lotes");
        Map<String, Bodega> temporal = new LinkedHashMap<>();
        Map<String, BigDecimal> ocupaciones = new HashMap<>();
        Set<String> codigos = new HashSet<>();
        for (Bodega b : nuevas) {
            Validacion.noNulo(b, "Bodega");
            if (temporal.putIfAbsent(b.getCodigo(), b) != null || b.iterator().hasNext())
                throw new IllegalArgumentException("Bodega inicial repetida o no vacía.");
            ocupaciones.put(b.getCodigo(), BigDecimal.ZERO);
        }
        for (Map.Entry<Lote, String> e : lotes.entrySet()) {
            Lote l = e.getKey(); validarCandidato(l, true);
            if (!codigos.add(l.getDatos().codigo())) throw new IllegalArgumentException("Lote inicial repetido.");
            Bodega b = temporal.get(e.getValue());
            if (b == null) throw new IllegalArgumentException("Bodega inicial inexistente.");
            b.validarIngreso(l);
            BigDecimal ocupacion = ocupaciones.get(b.getCodigo()).add(l.getKgExactos());
            if (ocupacion.compareTo(Dinero.decimal(b.getCapacidadKg())) > 0) throw new IllegalArgumentException("Capacidad inicial excedida.");
            ocupaciones.put(b.getCodigo(), ocupacion);
        }
        bodegas.putAll(temporal);
        for (Map.Entry<Lote, String> e : lotes.entrySet()) {
            Lote l = e.getKey(); bodegas.get(e.getValue()).almacenar(l);
            historial.put(l.getDatos().codigo(), l); ubicacionOriginal.put(l.getDatos().codigo(), e.getValue());
        }
    }
}
