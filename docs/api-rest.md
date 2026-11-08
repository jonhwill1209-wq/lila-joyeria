# API REST de Lila Joyería

La API se implementa con Jakarta Servlets y Jackson y está disponible bajo el mismo
contexto de despliegue que la aplicación web. No depende de las vistas JSF para
resolver las operaciones: los Servlets llaman a los DAO y estos persisten mediante
JPA/Hibernate. El cliente web independiente del sitio principal está en
`src/main/webapp/cliente-app/`.

## Base URL y formato

La base URL local es `http://localhost:8080/lila-joyeria`. Todas las rutas son
relativas a ella. Las solicitudes con cuerpo usan `Content-Type: application/json`;
las respuestas JSON usan UTF-8 y el formato común:

```json
{
  "exito": true,
  "mensaje": "Joya encontrada",
  "datos": {}
}
```

En respuestas de error, `exito` es `false`, `mensaje` explica el rechazo y `datos`
es `null`. `DELETE` exitoso devuelve `204 No Content` sin cuerpo.

## Contrato de endpoints

| Método y ruta | Acceso | Cuerpo | Respuesta de éxito |
|---|---|---|---|
| `GET /api/joyas` | Público | — | `200`, `datos` es una lista |
| `GET /api/joyas/{id}` | Público | — | `200`, `datos` es una joya |
| `POST /api/joyas` | Solo ADMIN | JSON de joya | `201`, `datos` es la joya creada |
| `PUT /api/joyas/{id}` | Solo ADMIN | JSON de joya | `200`, `datos` es la joya actualizada |
| `DELETE /api/joyas/{id}` | Solo ADMIN | — | `204`, sin cuerpo |
| `POST /api/auth/login` | Público | `{"email":"...","password":"..."}` | `200`, datos públicos del usuario |
| `POST /api/auth/logout` | Sesión opcional | — | `200`, sesión invalidada |
| `GET /api/auth/sesion` | Sesión requerida | — | `200`, usuario actual; `401` si no hay sesión |

Ejemplo de cuerpo de joya:

```json
{
  "nombre": "Anillo de prueba",
  "material": "Oro",
  "descripcion": "Joya de ejemplo",
  "quilates": 18.0,
  "precio": 150.00,
  "stock": 5,
  "idCategoria": 1
}
```

`nombre` (máximo 100 caracteres), `material` (máximo 50), `precio` (mayor que
cero) y `stock` (entero no negativo) son obligatorios en POST y PUT. El precio
admite hasta `99999999.99` y dos decimales; el stock admite hasta `2147483647`.
`descripcion`, `quilates` e `imagen` son opcionales; los quilates admiten de `0` a
`99.99` con dos decimales y la imagen hasta 150 caracteres. `idCategoria` es
opcional; si se envía debe ser positiva y referir a una categoría existente. Los
identificadores de ruta deben ser enteros positivos.

## Códigos de error

| Código | Uso |
|---|---|
| `400 Bad Request` | JSON inválido, campos que incumplen restricciones o ID no entero/no positivo |
| `401 Unauthorized` | No hay sesión para una operación restringida o las credenciales son inválidas |
| `403 Forbidden` | La sesión no tiene rol ADMIN para mutar joyas |
| `404 Not Found` | No existe la joya solicitada |
| `500 Internal Server Error` | Error inesperado o de persistencia; el detalle interno no se expone |

Los errores JSON usan la misma estructura `exito`, `mensaje`, `datos`. Los detalles
de excepciones de servidor se registran en el log del contenedor sin incluir el
cuerpo de la solicitud ni contraseñas. Las mutaciones autorizadas y los rechazos
de autorización también se registran.

## Autenticación, origen y privacidad

El login establece una sesión HTTP (`JSESSIONID`); el cliente debe conservar la
cookie para mutaciones. Las lecturas del catálogo son públicas; POST, PUT y DELETE
requieren ADMIN. El DTO de autenticación no contiene contraseña. El cliente estático
se puede desplegar por separado: cambiar el `content` de `meta[name="api-base-url"]`
en `index.html` y `login.html` por la URL absoluta terminada en `/api/` y publicar
también el directorio `img` junto al directorio del cliente. Para
habilitar el origen del cliente, configurar una lista explícita de orígenes permitidos en el proceso Tomcat con
`-Dlila.api.allowedOrigins=https://cliente.ejemplo` o la variable
`LILA_API_ALLOWED_ORIGINS` (separar varios orígenes por coma). Sin configuración
no se permite CORS cross-origin. Usar HTTPS en entornos no locales.

Para autenticación en un origen distinto, el navegador debe permitir cookies
cross-origin: alojar cliente y API bajo el mismo sitio HTTPS cuando sea posible; si
se usan sitios distintos, configurar `JSESSIONID` con `SameSite=None; Secure` en el
contenedor y verificar la política de cookies del navegador.

## Pruebas reproducibles

Importar `LilaJoyeria_API.postman_collection.json` en Postman. Seleccionar el entorno
local en el selector de entornos y definir sus valores actuales (`Current value`):

| Variable | Valor local |
|---|---|
| `baseUrl` | `http://localhost:8081/lila_joyeria_war_exploded` |
| `adminEmail` | Correo de una cuenta ADMIN existente en la base configurada para la aplicación |
| `adminPassword` | Contraseña actual de esa cuenta |

La colección no proporciona credenciales predeterminadas. No guardes contraseñas en
el repositorio ni uses variables de colección para sustituir las credenciales del
entorno. Un `401` en `Login ADMIN` indica que el correo/contraseña no autentican
contra la base de datos a la que está conectada la aplicación; verifica la cuenta
ADMIN en esa base y que el valor actual del entorno no esté vacío. Si el login falla,
las pruebas que comprueban los datos públicos también fallarán porque `datos` es
`null`; primero resuelve el `401`.

Ejecutar la colección en el Runner en el orden definido. La secuencia incluye login/sesión, lectura, CRUD, identificador
inválido, JSON mal formado, validación de campo obligatorio, valores límite y
rechazo de mutación sin sesión. Hace falta una base MySQL inicializada con
`recursos_db/lila_joyeria_schema.sql` y una cuenta ADMIN válida.

La colección registra aserciones de estado HTTP y de estructura JSON. Un resultado
de Runner es la evidencia de ejecución; el archivo de colección por sí solo no
demuestra que se haya ejecutado contra una instancia.

## Seguimiento

El backlog del alcance REST está en [api-backlog.md](api-backlog.md) y el registro
de defectos/casos pendientes está en [api-defects.md](api-defects.md).
