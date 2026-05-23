# MINIMARKET PLUS

Backend REST para la gestión de un minimarket, desarrollado con Spring Boot 3 y Spring Security.

## Requisitos

- Java 17
- Maven (incluido via `./mvnw`)

## Ejecución local

```bash
cd minimarket
./mvnw spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`.

## Base de datos H2

Consola H2: `http://localhost:8080/h2-console`

- **JDBC URL:** `jdbc:h2:mem:minimarketdb`
- **Usuario:** `sa`
- **Contraseña:** *(vacía)*

## Usuarios de prueba

| Usuario   | Contraseña    | Rol      |
|-----------|---------------|----------|
| admin     | admin123      | ADMIN    |
| gerente   | gerente123    | GERENTE  |
| empleado  | empleado123   | EMPLEADO |
| cliente   | cliente123    | CLIENTE  |

## Autenticación

La API usa **HTTP Basic Auth**. Ejemplo con curl:

```bash
# Catálogo público (sin autenticación)
curl http://localhost:8080/api/productos

# Ventas (requiere rol EMPLEADO, GERENTE o ADMIN)
curl -u empleado:empleado123 http://localhost:8080/api/ventas

# Administración de usuarios (solo ADMIN)
curl -u admin:admin123 http://localhost:8080/api/usuarios
```

También está disponible form login en `/login` para pruebas desde navegador.

## Roles y permisos

| Recurso | Público | CLIENTE | EMPLEADO | GERENTE | ADMIN |
|---------|---------|---------|----------|---------|-------|
| GET productos / categorías | Si | Si | Si | Si | Si |
| POST/PUT/DELETE productos | — | — | — | Si | Si |
| POST/PUT/DELETE categorías | — | — | — | Si | Si |
| Carrito | — | Si | — | — | Si |
| GET inventario | — | — | Si | Si | Si |
| POST/PUT/DELETE inventario | — | — | — | Si | Si |
| Ventas / detalle ventas | — | — | Si | Si | Si |
| Usuarios | — | — | — | — | Si |
| /public/** | Si | Si | Si | Si | Si |

## Tests

```bash
cd minimarket
./mvnw clean test
```
