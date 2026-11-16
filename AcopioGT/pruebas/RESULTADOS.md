# Evidencia de pruebas de AcopioGT

Ejecución automática; importes sin redondeo interno. Los rechazos comparan fecha, inventario, bodegas, movimientos, despachos y totales.

| N.º | Entrada / caso | Esperado | Obtenido | Resultado |
|---|---|---|---|---|
| 1 | Café 500 kg A, humedad 13.4 % | 5640 | 5640.000 | OK |
| 2 | Café 10 kg A, humedad 12 % | 120 | 120.000 | OK |
| 3 | Café 10 kg B, humedad 8 % | 90 | 90.000 | OK |
| 4 | Café 10 kg C, humedad 14 % | 56.4 | 56.400 | OK |
| 5 | Café 100 kg A, humedad 12.01 % | 1164 | 1164.000 | OK |
| 6 | Café 100 kg A, humedad 13 % | 1164 | 1164.000 | OK |
| 7 | Orgánico 500 kg A, humedad 13.4 % | 6015 | 6015.000 | OK |
| 8 | Cardamomo 80 kg B, verde 90 % | 4224 | 4224.000 | OK |
| 9 | Cardamomo 10 kg A, verde 85 % | 660 | 660.000 | OK |
| 10 | Cardamomo 10 kg C, verde 84.9 % | 350 | 350.0 | OK |
| 11 | Cardamomo 10 kg A, verde 0 % válido | 600 | 600.0 | OK |
| 12 | Cardamomo 10 kg A, verde 100 % válido | 660 | 660.000 | OK |
| 13 | Hortaliza 200 kg A | 760 | 760.000 | OK |
| 14 | Hortaliza 10 kg B | 28.5 | 28.500 | OK |
| 15 | Hortaliza 10 kg C | 19 | 19.000 | OK |
| 16 | Queso 40 kg A | 1460 | 1460.000 | OK |
| 17 | Queso 10 kg B | 285 | 285.000 | OK |
| 18 | Peso cero | true | true | OK |
| 19 | Peso negativo | true | true | OK |
| 20 | Peso NaN | true | true | OK |
| 21 | Peso infinito | true | true | OK |
| 22 | Código vacío | true | true | OK |
| 23 | Productor vacío | true | true | OK |
| 24 | Calidad nula | true | true | OK |
| 25 | Café humedad 7.9 | true | true | OK |
| 26 | Café humedad 14.1 | true | true | OK |
| 27 | Humedad NaN | true | true | OK |
| 28 | Orgánico sello anterior al ingreso | true | true | OK |
| 29 | Certificadora vacía | true | true | OK |
| 30 | Verde negativo | true | true | OK |
| 31 | Verde mayor que 100 | true | true | OK |
| 32 | Vida útil 2 | true | true | OK |
| 33 | Vida útil 15 | true | true | OK |
| 34 | Queso calidad C | true | true | OK |
| 35 | Grasa 9.9 | true | true | OK |
| 36 | Grasa 40.1 | true | true | OK |
| 37 | Capacidad cero | true | true | OK |
| 38 | Pedido negativo | true | true | OK |
| 39 | Pedido infinito | true | true | OK |
| 40 | Ingreso 05/03 + 7 días | 2026-03-12 | 2026-03-12 | OK |
| 41 | Válido durante 12/03 | false | false | OK |
| 42 | Vencido desde 13/03 | true | true | OK |
| 43 | Cardamomo +180 días | 2026-09-01 | 2026-09-01 | OK |
| 44 | Queso +21 días | 2026-03-26 | 2026-03-26 | OK |
| 45 | Café no vence | Optional.empty | Optional.empty | OK |
| 46 | Orgánico no vence aunque expire sello | false | false | OK |
| 47 | Preliquidación H 200 kg A | 760 | 760.000 | OK |
| 48 | Preliquidación no registra nada | true | true | OK |
| 49 | Hortaliza en bodega seca | true | true | OK |
| 50 | Hortaliza en bodega seca: estado idéntico | true | true | OK |
| 51 | Café en cámara fría | true | true | OK |
| 52 | Café en cámara fría: estado idéntico | true | true | OK |
| 53 | Bodega inexistente | true | true | OK |
| 54 | Bodega inexistente: estado idéntico | true | true | OK |
| 55 | Lote excede capacidad | true | true | OK |
| 56 | Lote excede capacidad: estado idéntico | true | true | OK |
| 57 | Bodega duplicada | true | true | OK |
| 58 | Bodega duplicada: estado idéntico | true | true | OK |
| 59 | Ingreso anterior al día operativo | true | true | OK |
| 60 | Ingreso anterior al día operativo: estado idéntico | true | true | OK |
| 61 | Ingreso futuro | true | true | OK |
| 62 | Ingreso futuro: estado idéntico | true | true | OK |
| 63 | Pago completo café recibido | 5640 | 5640.000 | OK |
| 64 | Pago completo hortaliza recibida | 760 | 760.000 | OK |
| 65 | Merma no reduce inventario | 200 | 200.0 | OK |
| 66 | Código de lote repetido | true | true | OK |
| 67 | Código de lote repetido: estado idéntico | true | true | OK |
| 68 | Pedido mayor al stock | true | true | OK |
| 69 | Pedido mayor al stock: estado idéntico | true | true | OK |
| 70 | Categoría sin existencias | true | true | OK |
| 71 | Categoría sin existencias: estado idéntico | true | true | OK |
| 72 | Retroceder fecha | true | true | OK |
| 73 | Retroceder fecha: estado idéntico | true | true | OK |
| 74 | Avanzar a la misma fecha | true | true | OK |
| 75 | Avanzar a la misma fecha: estado idéntico | true | true | OK |
| 76 | 100 kg café a costo 11.28 +35 % | 1522.8 | 1522.80000 | OK |
| 77 | Café restante después del despacho | 400 | 400.0 | OK |
| 78 | Estado después de despacho parcial | DISPONIBLE | DISPONIBLE | OK |
| 79 | Hortaliza sigue disponible el día de vencer | DISPONIBLE | DISPONIBLE | OK |
| 80 | Hortaliza descartada al siguiente día | DESCARTADO | DESCARTADO | OK |
| 81 | Pérdida de 50 kg a Q3.80 | 190 | 190.000 | OK |
| 82 | Descarte libera bodega | 0 | 0 | OK |
| 83 | Lote descartado queda con cero | 0 | 0 | OK |
| 84 | Pérdida no se duplica | 190 | 190.000 | OK |
| 85 | Bodega: perecedero antes de café | C,A,Z | C,A,Z | OK |
| 86 | Orden por calidad y código global | A,B,Z | A,B,Z | OK |
| 87 | Despacho mezcla bodegas en orden global | [A, B, Z] | [A, B, Z] | OK |
| 88 | Despacho 100 A +100 A +50 B | 3847.5 | 3847.50000 | OK |
| 89 | Lotes agotados salen de bodega S2 | 0 | 0 | OK |
| 90 | Historial conserva agotados | 4 | 4 | OK |
| 91 | Estado agotado | AGOTADO | AGOTADO | OK |
| 92 | Código agotado no se reutiliza | true | true | OK |
| 93 | Código agotado no se reutiliza: estado idéntico | true | true | OK |
| 94 | Orden completo: vencimiento, luego ingreso | P1,P2,Z,A | P1,P2,Z,A | OK |
| 95 | Capacidad exacta 0.1 +0.2 | 0.3 | 0.3 | OK |
| 96 | Capacidad ocupada acumulada | true | true | OK |
| 97 | Capacidad ocupada acumulada: estado idéntico | true | true | OK |
| 98 | Despacho exacto 0.3 kg | 4.86 | 4.86000 | OK |
| 99 | Sin residuos decimales | 0 | 0 | OK |
| 100 | Agotamiento exacto en dos despachos | 0 | 0 | OK |
| 101 | Carga inicial: 10 lotes | 10 | 10 | OK |
| 102 | Carga inicial: 3 bodegas | 3 | 3 | OK |
| 103 | Carga inicial sin movimientos | 0 | 0 | OK |
| 104 | Dos lotes iniciales de CAFE_PERGAMINO | 2 | 2 | OK |
| 105 | Pagos iniciales CAFE_PERGAMINO | 0 | 0 | OK |
| 106 | Dos lotes iniciales de CAFE_ORGANICO | 2 | 2 | OK |
| 107 | Pagos iniciales CAFE_ORGANICO | 0 | 0 | OK |
| 108 | Dos lotes iniciales de CARDAMOMO | 2 | 2 | OK |
| 109 | Pagos iniciales CARDAMOMO | 0 | 0 | OK |
| 110 | Dos lotes iniciales de HORTALIZA | 2 | 2 | OK |
| 111 | Pagos iniciales HORTALIZA | 0 | 0 | OK |
| 112 | Dos lotes iniciales de QUESO_FRESCO | 2 | 2 | OK |
| 113 | Pagos iniciales QUESO_FRESCO | 0 | 0 | OK |
| 114 | Vencen entre 12 y 15 de marzo | H01,Q01,C01 | H01,Q01,C01 | OK |
| 115 | Retira todos los vencidos durante recorrido | 0 | 0 | OK |
| 116 | Pérdida hortalizas iniciales | 1045 | 1045.000 | OK |
| 117 | Pérdida quesos iniciales | 3170 | 3170.000 | OK |
| 118 | Pérdida cardamomo inicial vencido | 4224 | 4224.000 | OK |
| 119 | Cafés no vencen | 2 | 2 | OK |
| 120 | Código descartado no se reutiliza | true | true | OK |
| 121 | Código descartado no se reutiliza: estado idéntico | true | true | OK |
| 122 | Carga inicial excede capacidad acumulada | true | true | OK |
| 123 | Carga inicial excede capacidad acumulada: estado idéntico | true | true | OK |
| 124 | Carga fallida deja bodega externa vacía | false | false | OK |
| 125 | Carga inicial con códigos duplicados | true | true | OK |
| 126 | Carga inicial con códigos duplicados: estado idéntico | true | true | OK |
| 127 | Carga inicial con vencido | true | true | OK |
| 128 | Carga inicial con vencido: estado idéntico | true | true | OK |
| 129 | Iterator.remove sin next | true | true | OK |
| 130 | Iterator no retira un lote aún disponible | true | true | OK |
| 131 | Iterator.remove dos veces | true | true | OK |
| 132 | No salta siguiente lote después de remove | I2 | I2 | OK |
| 133 | No elimina el segundo lote disponible | true | true | OK |

Total: 133 comprobaciones correctas.
