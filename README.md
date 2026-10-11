# ms-prestamos

Microservicio de Préstamos - Biblioteca Digital EDUCA+
Asignatura: Java: Diseño y Construcción de Soluciones Nativas en Nube

## Propósito
Gestionar el ciclo de reserva, préstamo y devolución de libros físicos,
validando usuarios y stock mediante comunicación con otros microservicios.

## Tecnologías
- Java 21
- Spring Boot 3.3.0 (Web, Data JPA, Validation, RestClient)
- MySQL 8
- Maven

## Dependencias con otros microservicios
Este servicio **necesita que estén en ejecución**:

| Microservicio | Puerto | Para qué se usa |
|---|---|---|
| `ms-usuarios` | 8081 | Validar que el usuario existe |
| `ms-catalogo` | 8082 | Validar el libro, consultar y actualizar su stock |

Orden recomendado de arranque: `ms-usuarios`, `ms-catalogo` y por último
`ms-prestamos` (puerto 8083). Cada uno se ejecuta siguiendo las instrucciones
de su propio README. Si alguno no está disponible, `ms-prestamos` responde
`503 Service Unavailable`.

## Decisión de diseño: sin relaciones entre bases de datos
Cada microservicio tiene su propia base de datos. `ms-prestamos` no usa claves
foráneas hacia las otras bases: solo guarda `usuarioId` y `libroId`, y obtiene
los datos reales mediante peticiones HTTP (`RestClient`), reconstruyendo la
relación en memoria. Por eso `Prestamo` no usa `@ManyToOne` hacia otros
servicios.

## Estructura
```
src/main/java/com/educa/msprestamos/
├── client/       Clientes HTTP hacia ms-usuarios y ms-catalogo
├── controller/   Endpoints REST
├── dto/          Objetos de transferencia de datos
├── service/      Lógica de negocio
├── repository/   Acceso a datos (Spring Data JPA)
├── model/        Entidad Prestamo, enum EstadoPrestamo y request
└── exception/    Manejo global de errores
```

## Requisitos previos
- JDK 21
- Maven 3.9 o superior
- MySQL 8 en ejecución
- Git
- `ms-usuarios` y `ms-catalogo` en ejecución (ver arriba)

## Cómo ejecutarlo

1. Clonar el repositorio:
```bash
git clone https://github.com/mathiasAla/ms-prestamos.git
cd ms-prestamos
```

2. Crear la base de datos en MySQL:
```sql
CREATE DATABASE db_prestamos;
```

3. Editar `src/main/resources/application.properties` y colocar el usuario y
la contraseña de MySQL. Si los otros servicios usan otros puertos, ajustar
también sus URLs:
```properties
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD
usuarios.service.url=http://localhost:8081
catalogo.service.url=http://localhost:8082
```

4. Compilar y empaquetar (genera `target/ms-prestamos-1.0.0.jar`):
```bash
mvn clean package
```

5. Ejecutar:
```bash
java -jar target/ms-prestamos-1.0.0.jar
```
También se puede usar `mvn spring-boot:run`. El servicio queda en
`http://localhost:8083`. Las tablas se crean automáticamente al iniciar.

6. Probar: importar `docs/ms-prestamos.postman_collection.json` en Postman.
Para registrar un préstamo, el usuario y el libro deben existir antes en
`ms-usuarios` y `ms-catalogo`:
```json
POST /api/prestamos
{
  "usuarioId": 1,
  "libroId": 3
}
```

## Endpoints

| Método | Ruta | Descripción | Código |
|---|---|---|---|
| GET | `/api/prestamos` | Listar préstamos | 200 |
| GET | `/api/prestamos/{id}` | Obtener un préstamo | 200 / 404 |
| GET | `/api/prestamos/{id}/detalle` | Préstamo con datos de usuario y libro | 200 / 404 / 503 |
| GET | `/api/prestamos/usuario/{usuarioId}` | Préstamos de un usuario | 200 / 404 / 503 |
| POST | `/api/prestamos` | Registrar préstamo (descuenta stock) | 201 / 400 / 404 / 409 / 503 |
| PUT | `/api/prestamos/{id}/devolucion` | Registrar devolución (repone stock) | 200 / 404 / 409 |
| DELETE | `/api/prestamos/{id}` | Eliminar préstamo | 204 / 404 |

## Reglas de negocio
- El plazo de devolución es de 14 días desde la fecha del préstamo.
- No se puede prestar un libro sin stock (`409`).
- Un préstamo ya devuelto no se puede devolver otra vez (`409`).
- Al prestar se descuenta una unidad del stock en `ms-catalogo`; al devolver, se repone.

## Limitación conocida
La actualización del stock en `ms-catalogo` y el registro del préstamo en esta
base de datos no forman una única transacción distribuida. Una solución
productiva usaría un patrón como Saga o mensajería con eventos.

## Control de versiones
Ramas: `main` (versión estable), `develop` (integración) y `feature/*`
(cada funcionalidad), integradas mediante Pull Request.