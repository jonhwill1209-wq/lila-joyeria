# Persona 1: persistencia JPA / Hibernate

## Alcance entregado

Java 17, Jakarta Persistence 3.1 (API incluida por Hibernate), Hibernate ORM 6.6.11.Final,
MySQL y pool HikariCP. La unidad `lilaJoyeriaPU` está en
`src/main/resources/META-INF/persistence.xml` y utiliza transacciones `RESOURCE_LOCAL`,
adecuadas para Tomcat sin un contenedor JTA.

Los controladores y vistas existentes pueden seguir usando las firmas de los DAOs.
La migración de consultas conserva `SQLException` como contrato de compatibilidad,
envolviendo la excepción JPA original como causa. Los futuros Managed Beans pueden
usar estos mismos DAOs. JSF, AJAX y nuevas reglas de negocio corresponden a las otras personas.

## Modelo y decisiones técnicas

| Entidad | Tabla | Relaciones y restricciones |
| --- | --- | --- |
| Usuario | usuarios | ID autogenerado; email único; rol persistido como texto |
| Categoria | categorias | ID autogenerado; nombre obligatorio de 50 caracteres |
| Joya | joyas | Categoría opcional; precio DECIMAL(10,2); quilates DECIMAL(4,2) |
| Pedido | pedidos | Usuario obligatorio; total DECIMAL(10,2); estado como texto; colección de detalles |
| DetallePedido | detalles_pedido | Pedido y joya obligatorios; precio unitario DECIMAL(10,2) |

Se usa acceso JPA a campos para conservar constructores, getters y validaciones del modelo.
`Pedido.detalles` es inversa (`mappedBy="pedido"`); `DetallePedido.pedido` posee la clave
foránea. El pedido propaga persistencia, edición y eliminación únicamente a sus detalles
mediante `cascade=ALL` y `orphanRemoval=true`. No se propagan eliminaciones a usuarios,
joyas o categorías. Las claves foráneas de MySQL impiden eliminar registros referenciados.

Los detalles se administran a través de `PedidoDAO` como parte del agregado, evitando
un DAO independiente que permita editar cantidades y dejar el total desactualizado.
Se conserva el cálculo de total que ya tenía el modelo y el guardado anterior.
Las nuevas reglas de inventario, totales y autenticación corresponden a Persona 2.

Las búsquedas, listados, filtros, eliminación simple y actualización de estado usan JPQL
con parámetros. Para insertar se usa `persist` (JPQL no ofrece INSERT portable), para
editar entidades separadas se usa `merge`, y para eliminar el agregado se usa `remove`
para respetar las cascadas. No se usa SQL nativo ni JDBC manual en los DAOs.

`JPAUtil` crea una sola fábrica de forma diferida y sincronizada. Cada operación tiene
su propio EntityManager, cerrado en `finally`. Cada escritura ejecuta begin/commit o
rollback; los errores de rollback se conservan como excepciones suprimidas.
`PersistenciaListener` cierra la fábrica y el pool al retirar la aplicación de Tomcat.
Los listados usan `JOIN FETCH` para entregar las relaciones requeridas por las vistas
después de cerrar el EntityManager. Las entidades devueltas quedan detached; para
guardar cambios hay que llamar a `actualizar`.

Tras un rollback se debe descartar el objeto utilizado y construir uno nuevo: JPA puede
haber asignado IDs y referencias aunque la transacción no se haya confirmado.

## Configuración y ejecución

1. En una instalación nueva, ejecutar `recursos_db/lila_joyeria_schema.sql` en MySQL.
   El script crea tablas y datos iniciales; no se debe volver a ejecutar sobre una base existente.
2. Configurar `DB_URL`, `DB_USER` y `DB_PASSWORD` en el entorno del proceso de Tomcat,
   o configurar `db.url`, `db.user`, `db.password` en `src/main/resources/database.properties`.
   Las variables de entorno tienen prioridad; no incluir credenciales reales en commits.
3. Ejecutar `mvn clean package` y desplegar `target/lila-joyeria.war` en Tomcat 10+.

Hibernate utiliza `hibernate.hbm2ddl.auto=validate`: verifica las tablas existentes al
crear la fábrica y no crea, modifica ni elimina tablas de MySQL. No se requiere cambiar
el esquema SQL actual. HikariCP reutiliza conexiones, con mínimo 1 y máximo 10.
La configuración de credenciales se aplica a JPA desde `JPAUtil`, no mediante valores
secretos dentro de `persistence.xml`.

## Pruebas y evidencia

`mvn test` ejecuta cuatro pruebas JUnit en H2 con modo MySQL. Son pruebas de persistencia
real con Hibernate y una base SQL en memoria, sin mocks. Solo estas pruebas usan
`create-drop`; no se conectan a la base MySQL de la aplicación.

| Prueba | Evidencia |
| --- | --- |
| crudYConsultasParametrizadas | CRUD de usuario, categoría y joya; filtros; consulta de login parametrizada; IDs inexistentes |
| agregadoRelacionesEdicionYEliminacion | Pedido con detalle, relaciones detached, edición y total, estado, eliminación de huérfanos y cascada |
| rollbackNoDejaPedidoNiDetallesParciales | Una joya inexistente provoca rollback de cabecera y todos los detalles |
| integridadUnicidadYClavesForaneas | Email duplicado y eliminación de usuario referenciado fallan sin alterar los registros confirmados |

Para demostrar MySQL con los programas de prueba existentes, desde PowerShell:

```powershell
mvn test-compile dependency:build-classpath '-Dmdep.outputFile=target/test-classpath.txt' '-DincludeScope=test'
$jpaClasspath = 'target/classes;target/test-classes;' + (Get-Content target/test-classpath.txt -Raw).Trim()
foreach ($jpaTest in @('CategoriaDAOTest', 'UsuarioDAOTest', 'JoyaDAOTest', 'PedidoDAOTest')) {
    java -cp $jpaClasspath "com.lilajoyeria.dao.$jpaTest"
    if ($LASTEXITCODE -ne 0) { throw "Falló $jpaTest" }
}
```

Estas pruebas requieren la base inicializada y credenciales configuradas; crean y limpian
registros temporales de usuarios, joyas y pedidos. Las categorías iniciales se consultan.
Los errores de restricción impresos durante la prueba de rollback son esperados.

Verificación realizada el 2026-10-06: cuatro pruebas JUnit aprobadas y cuatro programas
de integración aprobados sobre MySQL local 9.6, incluyendo rollback por FK inválida.
El WAR se generó correctamente. No se verificó despliegue ni navegación en Tomcat.
La verificación final se ejecutó con `mvn package`: `mvn clean package` encontró un
bloqueo de Windows al eliminar una carpeta dentro de `target/test-classes`.

## Mejora incremental y backlog

No se proporcionó un acta del Sprint Review anterior. Las siguientes observaciones
provienen de la revisión técnica del repositorio, no de un acta del equipo.

| ID | Observación / trabajo | Responsable | Estado |
| --- | --- | --- | --- |
| P1-01 | Modelo sin anotaciones; mapear cinco entidades y relaciones | Persona 1 | Implementado y probado |
| P1-02 | Consultas y conversión manual de filas duplicadas; migrar DAOs a JPA/JPQL | Persona 1 | Implementado y probado |
| P1-03 | Transacciones JDBC locales; centralizar rollback y cierre de recursos JPA | Persona 1 | Implementado y probado |
| P1-04 | Sin pruebas JUnit ejecutadas por Maven; verificar CRUD, agregado e integridad | Persona 1 | Implementado y probado |
| P1-05 | Configurar unidad, fábrica y pool; validar esquema existente | Persona 1 | Implementado y probado en MySQL |
| EQUIPO-01 | Incorporar observaciones reales del Sprint Review y priorización del equipo | Equipo | Pendiente de acta |
| P2-01 | Managed Beans, scopes, reglas de inventario y autenticación | Persona 2 | Fuera de este incremento |
| P3-01 | Migración a XHTML y componentes JSF | Persona 3 | Fuera de este incremento |
| P4-01 | AJAX, validadores y convertidores | Persona 4 | Fuera de este incremento |

La consulta de autenticación conserva el comportamiento previo de contraseñas; la
adopción de hashing y las reglas de autenticación deben coordinarse con Persona 2.
`ConexionBD` permanece disponible para la prueba JDBC histórica, pero los DAOs ya no la usan.
