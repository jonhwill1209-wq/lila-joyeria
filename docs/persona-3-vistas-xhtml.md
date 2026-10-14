# Persona 3: Vistas XHTML, tablas y navegación por roles

## Objetivo

Migrar las vistas JSP a Facelets (XHTML), maquetar los formularios de acceso,
construir tablas con filtrado/edición/eliminación, aplicar `css/estilos.css`
y estructurar la navegación según `RolUsuario`.

## Vistas

| Vista | Acceso | Descripción |
| --- | --- | --- |
| `index.xhtml` | Todos | Portada con galería (JS movido a `js/galeria.js`). |
| `login.xhtml` / `registro.xhtml` | Visitante | Formularios con validación básica. Todo registro crea un `CLIENTE`. |
| `catalogo.xhtml` | Todos | Tarjetas de producto con filtro por texto, categoría y disponibilidad. |
| `carrito.xhtml` | Visitante y CLIENTE | Tabla del carrito; confirmar pedido exige sesión. |
| `pedidos.xhtml` | CLIENTE y ADMIN | CLIENTE: sus pedidos. ADMIN: todos, cambio de estado y eliminación de PENDIENTES. |
| `admin/joyas.xhtml` | ADMIN | Tabla con filtros, alta, edición y eliminación de joyas. |
| `admin/usuarios.xhtml` | ADMIN | Tabla con filtros, edición (nombre, correo, rol) y eliminación. |
| `acceso-denegado.xhtml` | Todos | Destino cuando un CLIENTE intenta entrar a `/admin/*`. |

Todas usan `WEB-INF/templates/plantilla.xhtml` (cabecera, menú por rol,
mensajes globales y pie) y `css/estilos.css`.

## Navegación por rol

| Enlace | Visitante | CLIENTE | ADMIN |
| --- | :-: | :-: | :-: |
| Inicio, Catálogo | ✔ | ✔ | ✔ |
| Carrito | ✔ | ✔ | |
| Login, Registro | ✔ | | |
| Mis pedidos | | ✔ | |
| Joyas, Usuarios, Pedidos | | | ✔ |
| Cerrar sesión | | ✔ | ✔ |

El menú solo **oculta** enlaces. La protección real está en
`filter/AccesoFilter`, que bloquea `/admin/*` (solo ADMIN) y
`/pedidos.xhtml` (usuario autenticado) leyendo el rol que `AuthBean`
publica en la sesión. Las acciones sensibles de `PedidosBean` además
comprueban `authBean.isAdmin()`.

## Cambios en código existente

- `AuthBean`: `isAdmin()`, `isCliente()`, publicación del rol en sesión y
  redirección por rol tras el login (ADMIN → `/admin/joyas`).
- `CatalogoBean`: filtros (`texto`, `idCategoria`, `soloDisponibles`).
  `getJoyas()` devuelve ahora la lista filtrada.
- `PedidoBean`: tras confirmar redirige a `pedidos` conservando el mensaje.
- Se eliminaron `index/login/registro/catalogo/carrito.jsp` y los servlets
  `Login/Registro/Catalogo/CarritoServlet`, que duplicaban la sesión y el
  carrito de los beans JSF.
- `web.xml`: parámetros JSF, página de inicio, timeout y página para
  `ViewExpiredException`.

## Pruebas manuales sugeridas

1. Visitante: `/admin/joyas.xhtml` redirige a `login.xhtml`.
2. Registrar un usuario → aparece en `admin/usuarios` con rol CLIENTE.
3. Login CLIENTE: el menú no muestra Joyas/Usuarios; `/admin/joyas.xhtml`
   redirige a `acceso-denegado.xhtml`.
4. Catálogo: filtrar por categoría y "solo disponibles".
5. Carrito → Realizar pedido → aparece en "Mis pedidos" con stock descontado.
6. Login ADMIN: crear, editar y eliminar una joya; intentar eliminar una joya
   con pedidos (debe mostrar mensaje, no error 500).
7. ADMIN no puede eliminarse ni quitarse el rol a sí mismo.
8. ADMIN: cambiar el estado de un pedido; eliminar un pedido PENDIENTE.

## Limitaciones conocidas

- Eliminar un pedido PENDIENTE **no repone el stock** descontado al crearlo.
- Los cambios de rol de otro usuario aplican en su próximo inicio de sesión.
- Las contraseñas se guardan y comparan en texto plano (`UsuarioDAO.validarLogin`).
- Los filtros usan envío normal del formulario; para AJAX (Persona 4) basta
  añadir `<f:ajax execute="@form" render="formTabla" />` a los botones.
