# Taller Automotriz

Sistema web para la gestion de un taller automotriz: catalogo de modelos de vehiculo, registro de vehiculos, repuestos compatibles, reparaciones y usuarios con roles. Desarrollado como Proyecto Integrador de **Desarrollo Empresarial** (USTA Tunja, 2026-2).

## Tecnologias

- Java 17
- Spring Boot 3.5.6 (Spring MVC, Spring Data JPA, Spring Security)
- Thymeleaf + Thymeleaf Extras Spring Security
- PostgreSQL 18
- Modelo de datos DBML oficial del proyecto
- Maven
- JUnit 5 + Mockito
- BCrypt para el almacenamiento de claves

## Modelo de datos

La base de datos se implementa en PostgreSQL 18 y sigue el modelo DBML oficial del proyecto. El esquema definitivo esta formado por las tablas `roles`, `usuarios`, `modelos`, `vehiculos`, `repuestos`, `modelos_repuestos` y `reparaciones`, con sus enums y restricciones oficiales. El script `database/05_migracion_dbml.sql` aplica este modelo sobre la base existente conservando los datos.

## Alcance funcional

| Modulo | Funcionalidades |
| --- | --- |
| Autenticacion | Login con correo electronico y Spring Security, cierre de sesion, control de acceso por rol |
| Modelos | Listar, crear, editar, ver detalle y dar de baja logica |
| Vehiculos | CRUD con placa unica, color de placa, datos del dueno y modelo asociado |
| Repuestos | CRUD con EAN unico, precio, stock y compatibilidad con varios modelos (M2M) |
| Reparaciones | Registrar, editar, consultar y cambiar estado (EN_PROCESO, TERMINADO, CANCELADO) |
| Usuarios | CRUD de usuarios, asignacion de rol y baja logica (solo administrador) |
| Busquedas | Repuestos compatibles con un modelo y modelo de un vehiculo por placa |
| Seguridad | CSRF activo, rutas protegidas, paginas de error 400/403/404/500 |

### Roles

- **ADMINISTRADOR**: acceso completo, incluida la gestion de usuarios y las bajas logicas.
- **MECANICO**: consulta de catalogos, creacion y edicion de sus propias reparaciones, cambio de estado y busquedas. No puede administrar usuarios ni crear modelos, vehiculos o repuestos.

## Requisitos previos

- JDK 17
- PostgreSQL 18 en ejecucion
- IntelliJ IDEA (recomendado) o Maven instalado

## Puesta en marcha

### 1. Crear la base de datos y aplicar el modelo oficial

Con pgAdmin 4, conectado a la base `postgres`, ejecutar:

```text
database/01_creacion_bd.sql
```

Despues, conectado a `taller_automotriz`, aplicar los scripts en este orden:

| Orden | Script | Proposito |
| --- | --- | --- |
| 1 | `database/02_esquema.sql` | Crea el esquema anterior. Solo para una base nueva, antes de la migracion. |
| 2 | `database/03_datos_iniciales.sql` | Carga roles y usuarios iniciales con el esquema anterior. |
| 3 | `database/04_datos_ejemplo.sql` | Carga datos de demostracion. Opcional. |
| 4 | `database/05_migracion_dbml.sql` | Migra el esquema anterior al modelo DBML oficial conservando los datos. |

`05_migracion_dbml.sql` es el unico script que lleva la base al modelo DBML oficial. Se ejecuta **una sola vez** y dentro de una transaccion. Tambien se puede aplicar directamente sobre una base existente creada con el esquema anterior o por Hibernate con `ddl-auto=update`.

> **Importante:** despues de ejecutar `05_migracion_dbml.sql`, no volver a ejecutar `03_datos_iniciales.sql` ni `04_datos_ejemplo.sql`. Esos scripts usan el esquema anterior y fallarian o intentarian cargar datos incompatibles. Tampoco se debe ejecutar `02_esquema.sql` sobre una base ya migrada.

### 2. Configurar la conexion

Los valores por defecto estan en `src/main/resources/application.properties`:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/taller_automotriz}
spring.datasource.username=${DB_USER:user_java}
spring.datasource.password=${DB_PASSWORD:0000}
spring.jpa.hibernate.ddl-auto=validate
```

Se pueden sobreescribir con las variables de entorno `DB_URL`, `DB_USER` y `DB_PASSWORD`.

Con `spring.jpa.hibernate.ddl-auto=validate`, Hibernate no crea ni modifica tablas: solo valida que el mapeo de las entidades coincida con el esquema oficial ya aplicado por los scripts SQL.

### 3. Ejecutar la aplicacion

Desde IntelliJ IDEA: ejecutar `TallerAutomotrizApplication`.

Desde la terminal:

```bash
mvn spring-boot:run
```

La aplicacion queda disponible en `http://localhost:8080`.

### 4. Iniciar sesion

| Correo | Clave | Rol |
| --- | --- | --- |
| `admin@taller.com` | `admin123` | ADMINISTRADOR |
| `mecanico@taller.com` | `mecanico123` | MECANICO |

> El login se realiza con el correo electronico. El campo `username` no se usa.
>
> Cambiar estas credenciales antes de un despliegue real.

## Pruebas

```bash
mvn test
```

Resultado actual: **13 pruebas unitarias, 0 fallos**, sobre las capas de servicio:

- `VehiculoServiceTest`: validacion de placa y placa duplicada.
- `ModeloServiceTest`: modelo duplicado (marca + nombre).
- `RepuestoServiceTest`: EAN invalido, EAN duplicado, precio/stock invalidos y validacion de precision decimal.
- `ReparacionServiceTest`: estado inicial, transicion invalida, mecanico que no puede editar una reparacion ajena e historial por vehiculo segun el rol.

## Estructura del proyecto

```text
TallerAutomotriz/
├── database/                  Scripts SQL ejecutables desde pgAdmin 4
├── src/main/java/com/usta/taller/
│   ├── config/                Seguridad y datos iniciales
│   ├── controllers/           Controladores MVC
│   ├── dtos/                  Formularios de entrada
│   ├── entities/              Entidades JPA y enums
│   ├── exceptions/            Excepciones de negocio y manejador global
│   ├── models/daos/           Repositorios Spring Data
│   └── models/services/       Logica de negocio
├── src/main/resources/
│   ├── static/css/            Estilos
│   ├── templates/             Vistas Thymeleaf
│   └── application.properties
├── src/test/java/...          Pruebas unitarias de servicios
└── pom.xml
```

## Rutas principales

| Ruta | Descripcion |
| --- | --- |
| `/login` | Inicio de sesion |
| `/` | Panel principal |
| `/modelos` | Catalogo de modelos |
| `/vehiculos` | Catalogo de vehiculos |
| `/vehiculos/{id}/reparaciones` | Historial de reparaciones del vehiculo, filtrado por rol |
| `/repuestos` | Catalogo de repuestos |
| `/reparaciones` | Reparaciones segun el rol |
| `/usuarios` | Gestion de usuarios (solo administrador) |
| `/busquedas/repuestos-modelo` | Repuestos compatibles con un modelo |
| `/busquedas/modelo-placa` | Modelo de un vehiculo por placa |

Cada modulo incluye listado, formulario de creacion, detalle, edicion y cambio de estado (baja logica).

## Reglas de negocio implementadas

- Placa con formato `ABC-123`, en mayusculas y unica.
- Color de placa limitado a AMARILLA o BLANCA.
- Datos del dueno almacenados en el vehiculo.
- Marca y modelo unicos en combinacion.
- EAN de 13 digitos y unico; precio mayor que cero y stock mayor o igual a cero.
- Repuestos asociados a uno o varios modelos.
- Estados de reparacion: EN_PROCESO, TERMINADO o CANCELADO, sin retorno a estados anteriores.
- Tipos de reparacion: MANTENIMIENTO_PREVENTIVO, REPARACION_CORRECTIVA, REVISION u OTRO.
- Una reparacion tiene un unico mecanico asignado.
- Solo las reparaciones EN_PROCESO pueden editarse; TERMINADO y CANCELADO son de consulta.
- Historial de reparaciones por vehiculo: el administrador ve todas y el mecanico solo las asignadas.
- Bajas logicas mediante campos de estado.
- Login mediante correo electronico y claves almacenadas con BCrypt.

## Funcionalidades adicionales

Las dos funcionalidades adicionales del proyecto (una de negocio y una de interfaz) aun no han sido asignadas por la profesora. La estructura del proyecto queda preparada para integrarlas sin modificar el alcance actual.

## Notas

- El proyecto no modifica ni depende de la carpeta `Libros` usada como referencia.
- El esquema oficial es el DBML del proyecto y se aplica con `database/05_migracion_dbml.sql`.
- `05_migracion_dbml.sql` fue validado en dos escenarios temporales y es de ejecucion unica. Una segunda ejecucion falla de forma controlada y no debe intentarse.
- Los scripts `02`, `03` y `04` pertenecen al esquema anterior. No deben volver a ejecutarse despues de aplicar `05`.
