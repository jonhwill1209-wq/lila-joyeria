# Persona 2 Controladores JSF y lógica de negocio

## Responsable

Persona 2

## Objetivo

Implementar los Managed Beans de JSF y las reglas de negocio para autenticación, catálogo, carrito, pedidos, totales e inventario.

## Implementación realizada

### Configuración JSF

- Se agregaron las dependencias de Jakarta Faces y CDI.
- Se configuraron `faces-config.xml`, `beans.xml` y `web.xml`.
- Se verificó el funcionamiento de JSF con una página de prueba desplegada en Tomcat.

### Managed Beans

| Bean | Scope | Responsabilidad |
| --- | --- | --- |
| `AuthBean` | `@SessionScoped` | Inicio y cierre de sesión, usuario autenticado y control de acceso. |
| `CatalogoBean` | `@ViewScoped` | Carga de joyas desde MySQL para el catálogo. |
| `CarritoBean` | `@SessionScoped` | Agregar, eliminar, vaciar productos y recalcular el total. |
| `PedidoBean` | `@ViewScoped` | Validar sesión, validar carrito y confirmar pedidos. |

### Reglas de negocio

- No se permite iniciar un pedido sin sesión iniciada.
- No se permite confirmar un carrito vacío.
- La cantidad solicitada no puede ser mayor que el stock disponible.
- Al agregar el mismo producto se actualiza el carrito y el total.
- Al confirmar un pedido se guarda la cabecera y los detalles.
- El stock se descuenta dentro de la transacción del pedido.
- Si no hay inventario suficiente, se cancela el pedido y se muestra un mensaje al usuario.

## Pruebas realizadas

| Prueba | Resultado |
| --- | --- |
| Abrir una página JSF en Tomcat | Correcto |
| Login con credenciales incorrectas | Muestra mensaje de error |
| Login con usuario de MySQL | Correcto |
| Cerrar sesión | Correcto |
| Cargar catálogo desde MySQL | Correcto |
| Agregar producto al carrito | Correcto |
| Eliminar producto del carrito | Correcto |
| Vaciar carrito | Correcto |
| Confirmar pedido | Se crea pedido en MySQL |
| Descontar inventario | Stock disminuye después del pedido |
| Intentar agregar una joya sin stock | Muestra mensaje y bloquea la operación |

## Evidencia sugerida para la defensa

1. Pantalla de login.
2. Catálogo cargado desde MySQL.
3. Carrito con una joya y total calculado.
4. Pedido guardado en la tabla `pedidos`.
5. Stock antes y después de confirmar un pedido.
6. Mensaje al intentar agregar una joya sin inventario.

## Pendiente de integración del equipo

- Reemplazar las vistas temporales por las páginas XHTML finales de Persona 3.
- Integrar AJAX, validadores y convertidores de Persona 4.
