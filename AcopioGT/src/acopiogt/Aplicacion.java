package acopiogt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.DateTimeException;
import java.util.*;
/** Punto de entrada. Todos los datos se conservan exclusivamente en memoria. */
public final class Aplicacion {
    private final AcopioGT sistema;
    private final Scanner entrada;
    public Aplicacion(AcopioGT sistema) { this(sistema, new Scanner(System.in)); }
    public Aplicacion(AcopioGT sistema, Scanner entrada) { this.sistema = sistema; this.entrada = entrada; }
    public static void main(String[] args) { new Aplicacion(DatosEjemplo.crear()).ejecutar(); }
    private String leer(String mensaje) {
        System.out.print(mensaje);
        if (!entrada.hasNextLine()) throw new NoSuchElementException("Entrada cerrada");
        return entrada.nextLine().trim();
    }
    private int entero(String mensaje) { return Integer.parseInt(leer(mensaje)); }
    private double numero(String mensaje) { return Double.parseDouble(leer(mensaje).replace(',', '.')); }
    private String categoria() {
        System.out.println("1. Café pergamino | 2. Café orgánico | 3. Cardamomo | 4. Hortaliza | 5. Queso fresco");
        return switch (entero("Categoría: ")) {
            case 1 -> Categorias.CAFE; case 2 -> Categorias.ORGANICO; case 3 -> Categorias.CARDAMOMO;
            case 4 -> Categorias.HORTALIZA; case 5 -> Categorias.QUESO;
            default -> throw new IllegalArgumentException("Categoría no válida.");
        };
    }
    private Lote leerLote() {
        String c = categoria();
        String codigo = leer("Código único: "), productor = leer("Productor: ");
        double peso = numero("Peso recibido (kg): ");
        Calidad calidad = Calidad.valueOf(leer("Calidad A/B/C: ").toUpperCase(Locale.ROOT));
        DatosLote d = new DatosLote(codigo, productor, sistema.getFechaOperacion(), peso, calidad);
        // Único lugar que selecciona clases concretas: captura/construcción, no reglas de negocio.
        return switch (c) {
            case Categorias.CAFE -> new CafePergamino(d, numero("Humedad (%): "));
            case Categorias.ORGANICO -> new CafeOrganico(d, numero("Humedad (%): "), leer("Certificadora: "), LocalDate.parse(leer("Sello válido hasta (AAAA-MM-DD): ")));
            case Categorias.CARDAMOMO -> new Cardamomo(d, numero("Grano verde (%): "));
            case Categorias.HORTALIZA -> new Hortaliza(d, entero("Vida útil (3 a 14 días): "));
            case Categorias.QUESO -> new QuesoFresco(d, numero("Grasa (%): "));
            default -> throw new IllegalArgumentException("Categoría no válida.");
        };
    }
    private void preliquidar(boolean recibir) {
        Lote l = leerLote();
        PreLiquidacion p = sistema.preliquidar(l);
        System.out.println("Pago: " + Dinero.mostrar(p.pago()) + " | Vencimiento: " + p.vencimiento().map(Object::toString).orElse("No vence"));
        if (recibir && leer("¿Aceptar y recibir? (S/N): ").equalsIgnoreCase("S")) {
            sistema.recibir(l, leer("Bodega destino: "));
            System.out.println("Lote recibido y pago registrado.");
        } else System.out.println("No se registró ningún lote ni pago.");
    }
    private void registrarBodega() {
        String codigo = leer("Código bodega: "); double capacidad = numero("Capacidad (kg): ");
        String condicion = switch (entero("Tipo: 1. Seca / 2. Cámara fría: ")) {
            case 1 -> "SECA"; case 2 -> "FRIA";
            default -> throw new IllegalArgumentException("Tipo de bodega inválido.");
        };
        sistema.registrarBodega(new Bodega(codigo, capacidad, condicion));
        System.out.println("Bodega registrada.");
    }
    private void despachar() {
        String comprador = leer("Comprador: "), c = categoria();
        Despacho d = sistema.despachar(new Pedido(comprador, c, numero("Kilogramos solicitados: ")));
        System.out.println("Despacho aceptado:");
        for (DetalleDespacho x : d.detalles()) System.out.println("  " + x.codigoLote() + " | " + Dinero.kg(x.kg()) + " kg | " + Dinero.mostrar(x.subtotal()));
        System.out.println("Total: " + Dinero.mostrar(d.total()));
    }
    private void mostrarLotes(List<Lote> lotes) {
        if (lotes.isEmpty()) { System.out.println("Sin lotes."); return; }
        System.out.printf("%-10s %-17s %-10s %-12s %-7s %-12s %-12s %-12s%n", "Código", "Categoría", "Bodega", "Ingreso", "Calidad", "Kg disp.", "Vence", "Estado");
        for (Lote l : lotes) System.out.printf("%-10s %-17s %-10s %-12s %-7s %-12s %-12s %-12s%n",
            l.getDatos().codigo(), l.categoria(), sistema.bodegaOriginal(l.getDatos().codigo()), l.getDatos().fechaIngreso(),
            l.getDatos().calidad(), Dinero.kg(l.getKgExactos()), l.vencimiento().map(Object::toString).orElse("No vence"), l.getEstado());
    }
    private void mostrarBodegas() {
        for (Bodega b : sistema.getBodegas()) {
            System.out.println("\nBodega " + b.getCodigo() + " | " + b.getCondicion() + " | " + Dinero.kg(b.ocupacionExacta()) + "/" + b.getCapacidadKg() + " kg");
            mostrarLotes(sistema.contenido(b.getCodigo()));
        }
    }
    private void mostrarTotales() {
        System.out.printf("%-20s %16s %16s %16s%n", "Categoría", "Pagado", "Cobrado", "Pérdidas");
        for (Map.Entry<String, Totales> e : sistema.totalesPorCategoria().entrySet()) {
            Totales t = e.getValue();
            System.out.printf("%-20s %16s %16s %16s%n", e.getKey(), Dinero.mostrar(t.pagado()), Dinero.mostrar(t.cobrado()), Dinero.mostrar(t.perdido()));
        }
    }
    public void ejecutar() {
        System.out.println("AcopioGT - Laboratorio 4. Datos de ejemplo cargados sin pagos ni cobros.");
        while (true) {
            System.out.println("\nFecha de operación: " + sistema.getFechaOperacion());
            System.out.println("1. Preliquidar\n2. Preliquidar y recibir\n3. Registrar bodega\n4. Pedido y despacho\n5. Avanzar fecha\n6. Existencias por categoría\n7. Contenido de bodegas\n8. Próximos vencimientos\n9. Totales económicos\n10. Historial de lotes\n0. Salir");
            try {
                switch (entero("Opción: ")) {
                    case 0 -> { System.out.println("Hasta pronto."); return; }
                    case 1 -> preliquidar(false);
                    case 2 -> preliquidar(true);
                    case 3 -> registrarBodega();
                    case 4 -> despachar();
                    case 5 -> {
                        sistema.avanzarFecha(LocalDate.parse(leer("Nueva fecha (AAAA-MM-DD): ")));
                        System.out.println("Fecha actualizada; vencidos descartados y pérdidas registradas.");
                    }
                    case 6 -> mostrarLotes(sistema.existencias(categoria()));
                    case 7 -> mostrarBodegas();
                    case 8 -> mostrarLotes(sistema.proximosAVencer());
                    case 9 -> mostrarTotales();
                    case 10 -> mostrarLotes(sistema.getHistorial());
                    default -> System.out.println("Opción fuera del menú.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida: escribe un número válido. No se aplicaron cambios.");
            } catch (DateTimeException e) {
                System.out.println("Fecha inválida: utiliza AAAA-MM-DD y una fecha existente. No se aplicaron cambios.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Operación rechazada: " + e.getMessage());
            } catch (NoSuchElementException e) {
                System.out.println("\nEntrada finalizada. Hasta pronto."); return;
            }
        }
    }
}
