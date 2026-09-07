# ms-prestamos

Microservicio de Préstamos — Biblioteca Digital EDUCA+
Asignatura: Java: Diseño y Construcción de Soluciones Nativas en Nube

## Propósito general
Gestionar el proceso completo de reserva, préstamo y devolución de libros físicos.

## Funciones clave
- Crear reservas y préstamos
- calcular las fechas de devolución 
- registrar las devoluciones y detectar vencimientos.

## Interacciones con otros microservicios
Consulta a ms-usuarios para validar la identidad y a ms-catalogo para comprobar la disponibilidad. Además, envía eventos a ms-notificaciones cuando un préstamo está por vencer.

## Justificación del aislamiento
Es un proceso transaccional con un ciclo propio, por lo que separarlo permite modificar las reglas de préstamo sin afectar otros módulos.

## Tecnología
- Java 17 + Spring Boot (Spring Web)
- Maven
- Base de datos: SQL (PostgreSQL) — transacciones de préstamo/devolución
- Comunicación: REST API (consultas síncronas) + Event Bus / Cola de
  mensajes (publicación de eventos asíncronos hacia ms-notificaciones)
- Patrón aplicado: Circuit Breaker en la comunicación con servicios externos
