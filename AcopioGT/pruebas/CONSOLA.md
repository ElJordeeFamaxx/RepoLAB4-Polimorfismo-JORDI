# Pruebas del menú de consola

Cada sesión comienza con los datos iniciales. Se comprueba salida normal, mensajes y resultados visibles.

| Sesión | Resultado esperado | Resultado obtenido |
|---|---|---|
| Entradas incorrectas y retorno al menú | Entrada inválida; Fecha inválida; Operación rechazada; Q0.00; Hasta pronto. | Coincide; proceso termina sin errores |
| Recepción, despacho parcial y reportes | Bodega registrada.; Lote recibido y pago registrado.; Despacho aceptado:; Total: Q8910.00; NUEVO; Q120.00; Hasta pronto. | Coincide; proceso termina sin errores |
| Preliquidación y recepción cancelada sin pago | Pago: Q760.00; No se registró ningún lote ni pago.; Q0.00; Hasta pronto. | Coincide; proceso termina sin errores |
| Descarte y fecha hacia atrás rechazada | Fecha actualizada; Q760.00; La nueva fecha debe ser posterior; Hasta pronto. | Coincide; proceso termina sin errores |
| Fin de entrada durante captura | Entrada finalizada. Hasta pronto. | Coincide; proceso termina sin errores |

Resultado: 5 sesiones correctas. Los archivos consola_N_entrada.txt y consola_N_salida.txt contienen las transcripciones.
