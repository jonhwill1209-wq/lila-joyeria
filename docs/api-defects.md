# Registro de defectos y pruebas — API REST

## Defectos encontrados y correcciones aplicadas

| ID | Hallazgo | Corrección |
|---|---|---|
| API-D01 | El cliente consumidor no ofrecía eliminación aunque existía `DELETE`. | Se añadió acción de eliminación solo visible para ADMIN, confirmación y refresco del catálogo; el servidor sigue validando el rol. |
| API-D02 | El consumidor siempre intentaba leer JSON, incompatible con la respuesta HTTP 204 de DELETE. | El cliente ahora interpreta 204 como operación completada sin intentar parsear un cuerpo. |
| API-D03 | IDs cero/negativos en PUT/DELETE no se rechazaban como entrada inválida de forma consistente. | PUT y DELETE devuelven 400 para IDs no positivos. |
| API-D04 | Los errores internos devolvían mensajes genéricos, pero no registraban la excepción en el log de aplicación. | Se centralizó la serialización de respuestas y se agregó registro del error sin exponer detalles al cliente. |
| API-D05 | CORS permitía cualquier origen, sin una política segura para cookies de sesión. | La respuesta cross-origin ahora requiere una lista explícita de orígenes permitidos; por defecto no habilita orígenes externos. |

## Casos de prueba incluidos en Postman

| Caso | Esperado |
|---|---|
| Login ADMIN y consulta de sesión | 200; respuesta común con usuario sin contraseña |
| Listar y consultar joya | 200; datos con forma esperada |
| Crear, actualizar y eliminar | 201, 200 y 204, respectivamente |
| Consultar ID cero | 400 y respuesta de error común |
| Enviar JSON mal formado o nombre vacío | 400 y respuesta de error común |
| Crear con valores máximos permitidos | 201; después se elimina la fila de prueba |
| Mutar sin sesión | 401 y respuesta de error común |
| Ejecutar con una cuenta CLIENTE | 403; requiere credenciales de una cuenta CLIENTE para añadirla al entorno local |

La colección automatiza todos los casos salvo el 403, que requiere credenciales de
CLIENTE no versionadas. No se afirma que estas pruebas HTTP se hayan ejecutado:
para cerrar esa evidencia, ejecutar el Runner con Tomcat y MySQL disponibles y
adjuntar/exportar el resultado en el proceso de entrega.
