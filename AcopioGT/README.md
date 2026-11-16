# AcopioGT — Laboratorio 4: Polimorfismo

Aplicación de consola en Java para preliquidar, recibir, almacenar y despachar
lotes; controlar vencimientos y consultar existencias, pagos, cobros y pérdidas.

## Requisitos

- **JDK 17 o superior**. Se necesita el JDK, que incluye `javac`, no solamente el entorno de ejecución.
- Terminal CMD, PowerShell, Linux o macOS.
- No requiere Maven, Gradle, bases de datos, bibliotecas externas ni Internet.

Verifica la instalación:

```console
java -version
javac -version
```

Ambos comandos deben mostrar la versión 17 o superior. Si `javac` no se reconoce,
configura la carpeta `bin` del JDK en la variable `PATH`.

## Inicio rápido en Windows

1. Descomprime el ZIP.
2. Abre la carpeta `AcopioGT`.
3. Ejecuta **ejecutar.bat**.

El archivo compila todas las clases y abre el menú. No ejecutes directamente los
archivos `.java` individuales.

## Compilar y ejecutar manualmente

Abre una terminal dentro de la carpeta que contiene este README y ejecuta:

```console
javac -encoding UTF-8 -d out @fuentes.txt
java -Dfile.encoding=UTF-8 -cp out acopiogt.Aplicacion
```

En **PowerShell**, escribe el argumento del archivo entre comillas:

```powershell
javac -encoding UTF-8 -d out "@fuentes.txt"
java -Dfile.encoding=UTF-8 -cp out acopiogt.Aplicacion
```

En Linux o macOS también puedes usar:

```sh
sh ejecutar.sh
```

`fuentes.txt` enumera todos los archivos que se compilan. Si agregas nuevas clases,
agrega su ruta a ese archivo.

## Ejecutar las pruebas

Después de compilar:

```console
java -Dfile.encoding=UTF-8 -cp out acopiogt.Pruebas
```

También puedes abrir `probar.bat` en Windows o ejecutar `sh probar.sh` en Linux/macOS.

El resultado esperado es:

```text
Total: 133 comprobaciones correctas.
```

Las pruebas arrojan `AssertionError` y terminan con error si un resultado no coincide.
No necesitan activar `-ea` porque utilizan comprobaciones explícitas.

## Menú

| Opción | Operación |
|---|---|
| 1 | Preliquidar sin registrar datos ni pagos. |
| 2 | Preliquidar, confirmar recepción e indicar la bodega. |
| 3 | Registrar una bodega seca o cámara fría. |
| 4 | Crear un pedido y despacharlo completo. |
| 5 | Avanzar la fecha y descartar vencidos. |
| 6 | Consultar existencias por categoría. |
| 7 | Consultar bodegas, ocupación y contenido ordenado. |
| 8 | Consultar vencimientos desde hoy hasta hoy + 3 días, ambos inclusive. |
| 9 | Consultar pagos, cobros y pérdidas por categoría. |
| 10 | Consultar el historial, incluidos agotados y descartados. |
| 0 | Salir. |

Las fechas se introducen con formato **AAAA-MM-DD**. Se acepta punto o coma como
separador decimal, sin separadores de miles. Los porcentajes se escriben como `13.4`,
no como `0.134`. La calidad puede escribirse en mayúscula o minúscula.

Una operación con datos inválidos muestra el error y regresa al menú. Si una
captura se rechaza, vuelve a elegir la opción para iniciarla de nuevo.

## Datos iniciales

La fecha de operación inicial es **2026-03-12**. Es fija para que los ejemplos y las
pruebas se reproduzcan, independientemente del día real.

| Bodega | Condición | Capacidad |
|---|---|---|
| S1 | SECA | 3000 kg |
| S2 | SECA | 3000 kg |
| F1 | FRIA | 2000 kg |

| Lote | Categoría | Kg | Calidad | Bodega | Datos particulares |
|---|---|---|---|---|---|
| Z01 | Café pergamino | 500 | A | S2 | Humedad 13.4 %; pago teórico Q5640.00. |
| A01 | Café pergamino | 100 | B | S1 | Humedad 12 %; misma fecha que Z01. |
| O01 | Café orgánico | 500 | A | S1 | Humedad 13.4 %; pago teórico Q6015.00. |
| O02 | Café orgánico | 100 | C | S2 | Humedad 10 %; sello válido al ingreso. |
| C01 | Cardamomo | 80 | B | S1 | Verde 90 %; vence el 13 de marzo. |
| C02 | Cardamomo | 50 | C | S2 | Verde 80 %; no recibe bono. |
| H01 | Hortaliza | 200 | A | F1 | Vida útil 7 días; vence el 12 de marzo. |
| H02 | Hortaliza | 100 | B | F1 | Vida útil 7 días; vence el 18 de marzo. |
| Q01 | Queso fresco | 40 | A | F1 | Grasa 30 %; vence el 13 de marzo. |
| Q02 | Queso fresco | 60 | B | F1 | Grasa 20 %; vence el 31 de marzo. |

Estos lotes se cargan directamente, **sin registrar pagos ni cobros**. Sí tienen
costo por kilogramo calculado según su categoría. Los pagos teóricos de la tabla
no representan movimientos registrados.

Los datos se conservan solamente en memoria. Al cerrar y volver a ejecutar,
se restauran los datos de ejemplo.

## Recorrido de demostración

Cada ejemplo supone que acabas de iniciar el programa.

### Pago y preliquidación

Selecciona opción 1, café pergamino, un código nuevo, productor, 500 kg, calidad A
y humedad 13.4 %. Debe mostrar **Q5640.00**. Consulta opción 9: los pagos siguen
en cero porque la preliquidación no registra nada.

### Despacho entre bodegas

Selecciona opción 4, comprador, café pergamino y **550 kg**.
Se consumen primero 500 kg de Z01 (calidad A) y después 50 kg de A01 (calidad B),
aunque A01 tenga un código alfabéticamente menor:

- Z01: 500 × Q11.28 × 1.35 = Q7614.00.
- A01: 50 × Q9.00 × 1.35 = Q607.50.
- Total: **Q8221.50**.

Z01 queda agotado y sale de S2. A01 queda con 50 kg en S1.

### Vencimiento y pérdida

Despacha **150 kg de hortaliza** desde la opción 4. Quedan 50 kg de H01.
Avanza a **2026-03-13**. H01 se descarta y registra pérdida de **Q190.00**
(50 × Q3.80). El vencimiento del 12 de marzo permitía usarlo durante ese día.

### Rechazo sin cambios

Solicita más kilogramos de una categoría de los que existen. Se rechaza el pedido
completo; puedes comparar opciones 6, 7 y 9 antes y después.

## Organización del código

Los archivos están en `src/acopiogt/`, con una clase, interfaz o enumeración por archivo.

| Elementos | Responsabilidad |
|---|---|
| Aplicacion | `main`, captura de datos y menú. |
| AcopioGT | Servicio de recepción, pedidos, fechas, consultas y movimientos. |
| DatosEjemplo | Carga inicial reproducible. |
| Lote | Estado, inventario, costo y orden; implementa `Comparable<Lote>` y `Almacenable`. |
| LotePerecedero | Cálculo común de vencimiento; implementa `Perecedero`. |
| CafePergamino, CafeOrganico | Pago de café y bonificación orgánica por herencia. |
| Cardamomo, Hortaliza, QuesoFresco | Reglas particulares de productos perecederos. |
| Almacenable, Perecedero, Refrigerado | Contratos de almacenamiento, vencimiento y refrigeración. |
| Bodega | Capacidad, compatibilidad y `Iterable<Lote>`. |
| IteradorBodega | `Iterator<Lote>` ordenado; permite retirar vencidos durante el recorrido. |
| DatosLote, PreLiquidacion, Pedido | Valores de entrada y resultados inmutables. |
| Despacho, DetalleDespacho | Detalle y total de una salida. |
| Movimiento, Totales | Registro y resumen de pagos, cobros y pérdidas. |
| Calidad, EstadoLote, TipoMovimiento | Enumeraciones. |
| Categorias | Identificadores de categoría. |
| Validacion, Dinero | Validaciones comunes y manejo decimal. |
| Pruebas | Pruebas de dominio, validación e integridad. |

## Decisiones de implementación

- Las reglas por categoría se ejecutan mediante polimorfismo. El servicio no utiliza
  `instanceof` ni pregunta por clases concretas. La selección por categoría del menú
  únicamente construye el objeto correcto.
- `compareTo` define el único criterio: perecederos primero; vencimiento o ingreso;
  calidad A/B/C; código. Los despachos reúnen todas las bodegas antes de ordenar.
- El iterador trabaja sobre una copia ordenada de referencias y `remove()` también
  elimina en la bodega original. Exige `next()` previo, una eliminación por elemento
  y que el lote ya haya pasado a agotado o descartado.
- La recepción, carga inicial y despacho se validan antes de modificar inventario.
  Los lotes agotados y descartados permanecen en el historial; sus códigos no se reutilizan.
- Un orgánico pertenece exclusivamente a `CAFE_ORGANICO`; no atiende un pedido
  de `CAFE_PERGAMINO` aunque herede de esa clase.
- El sello solo tiene que ser válido al ingresar; su expiración no vence el café.
- La merma de hortalizas afecta el pago, no los kilogramos almacenados.
- `BigDecimal` conserva cantidades internas y dinero. Las entradas públicas de peso
  mantienen `double`, validado y convertido mediante `BigDecimal.valueOf`.
  Esto refina el UML previo: el campo interno `kgDisponibles` y `DetalleDespacho.kg`
  usan `BigDecimal`; se agrega `getKgExactos()`.
- Se utiliza DECIMAL128 para divisiones y HALF_UP a dos decimales al mostrar montos.
  No se redondea el costo por kg antes de calcular el cobro o la pérdida.
- Los códigos distinguen mayúsculas y minúsculas y se recortan al capturarlos.
- El diseño está destinado a una aplicación de consola de un solo hilo.

## Extensión

Una categoría nueva extiende `Lote` o `LotePerecedero` e implementa categoría,
pago y condición. Agrega su captura en `Aplicacion`, su identificador en
`Categorias` si lo deseas y la ruta de la nueva clase en `fuentes.txt`.

El servicio reconoce categorías nuevas a partir de los lotes registrados y no
necesita cambios en despacho, descarte ni agrupación de movimientos.

Para congelación, `Bodega` puede configurarse con `CONGELADA`; el producto debe
devolver esa misma condición. Agrega la opción al menú de registro. Reglas
adicionales de temperatura requerirían ampliar el contrato de almacenamiento.

## Evidencia incluida

- `pruebas/RESULTADOS.md`: **133 comprobaciones** ejecutadas, con entradas, esperado y obtenido.
- `pruebas/CONSOLA.md`: **5 sesiones** de integración del menú.
- `pruebas/consola_N_entrada.txt` y `consola_N_salida.txt`: transcripciones reproducibles.

La verificación se ejecutó con OpenJDK 21, compilando el lenguaje y bytecode para
Java 17. Se emplean únicamente APIs disponibles en Java 17 o anteriores.
Los scripts para Windows se incluyen; la ejecución de consola se verificó en Linux.

## Archivos para entregar

El ZIP contiene código fuente, README, scripts y evidencia textual. No contiene
datos persistentes ni necesita archivos de datos para funcionar. El repositorio
público de GitHub y el PDF de análisis/UML exigidos por el enunciado se entregan
por separado; este paquete no publica automáticamente un repositorio.
