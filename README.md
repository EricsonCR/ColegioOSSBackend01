# ColegioOSSBackend01

Backend del sistema de gestión del colegio. Este documento explica qué hace el proyecto, cómo levantarlo en tu máquina, y en qué estado está cada funcionalidad — pensado para que cualquiera del equipo (o alguien nuevo que se sume) pueda entenderlo sin depender de que se lo expliquen en persona.

## ¿Qué es este proyecto?

Un backend en Spring Boot para un sistema escolar, con 4 módulos:

1. **Autenticación** — login, recuperación de contraseña, roles y permisos ✅ completo
2. **Matrícula** — registro de estudiantes, apoderados y matrículas ✅ completo
3. **Notas** — registro de calificaciones y promedios ⏳ no iniciado
4. **Asistencia** — control de asistencia diaria y reportes ⏳ no iniciado

## Stack tecnológico

- **Java 21**
- **Spring Boot 4.1.0**
- **Maven**
- **PostgreSQL**
- **Spring Security + JWT** (autenticación sin sesiones, con tokens)
- **Swagger / OpenAPI** (documentación interactiva de la API)
- **Lombok** (menos código repetitivo)
- **Spring Mail** (envío de correos vía Gmail)
- **springboot4-dotenv** (variables de entorno locales desde un archivo `.env`)

## Dependencias principales

| Dependencia | Para qué sirve |
|---|---|
| `spring-boot-starter-data-jpa` | Conectar y trabajar con la base de datos |
| `spring-boot-starter-webmvc` | Exponer la API REST |
| `spring-boot-starter-security` | Login y control de acceso |
| `spring-boot-starter-validation` | Validar los datos que llegan en cada request |
| `spring-boot-starter-mail` | Enviar correos (recuperación de contraseña) |
| `postgresql` | Conectarse a PostgreSQL |
| `lombok` | Generar getters/setters/builders automáticamente |
| `io.jsonwebtoken:jjwt-*` | Crear y validar los tokens JWT |
| `org.springdoc:springdoc-openapi-starter-webmvc-ui` | Generar la documentación Swagger |
| `me.paulschwarz:springboot4-dotenv` | Leer el archivo `.env` automáticamente |
| `h2` (solo pruebas) | Base de datos en memoria para los tests |

## Antes de empezar, necesitas tener instalado

- **JDK 21**
- **Maven**
- **PostgreSQL** (local, o acceso a una base remota)
- **Cuenta de Gmail** con verificación en 2 pasos, solo si vas a probar el envío de correos

## Cómo configurar tus variables de entorno

El proyecto usa 3 archivos para separar qué es público y qué es secreto:

| Archivo | ¿Se sube a Git? | Para qué |
|---|---|---|
| `application.properties` | Sí | La plantilla de configuración, sin contraseñas reales |
| `.env` | **No** (está en `.gitignore`) | Tus contraseñas y claves reales, solo en tu máquina |
| `.env.example` | Sí | Un ejemplo de `.env` para que sepas qué variables necesitas llenar |

En Railway (donde está desplegado el backend), estas variables se configuran directamente en el panel del proyecto, no se usa el archivo `.env` ahí.

### Variables que necesitas configurar

| Variable | Para qué es | Valor de ejemplo (desarrollo) |
|---|---|---|
| `SPRING_DATASOURCE_URL` | Conexión a tu base de datos | `jdbc:postgresql://localhost:5432/colegio_oss_db` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de PostgreSQL | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de PostgreSQL | (la tuya) |
| `JWT_SECRET` | Clave secreta para firmar los tokens | genera una con `openssl rand -base64 64` |
| `JWT_ACCESS_EXPIRATION_MS` | Cuánto dura el token de acceso | `300000` (5 minutos) |
| `JWT_REFRESH_EXPIRATION_MS` | Cuánto dura el token de refresco | `604800000` (7 días) |
| `MAIL_HOST` | Servidor de correo | `smtp.gmail.com` |
| `MAIL_PORT` | Puerto del servidor de correo | `587` |
| `MAIL_USERNAME` | Tu correo de Gmail | (el tuyo) |
| `MAIL_PASSWORD` | Contraseña de aplicación de Gmail (no la normal) | (la generas en tu cuenta Google) |
| `FRONTEND_RESET_URL` | A dónde apunta el enlace de recuperar contraseña | `http://localhost:4200/restablecer-password` |
| `CORS_ALLOWED_ORIGINS` | Qué frontend(s) pueden llamar a esta API, separados por coma | `http://localhost:4200` |
| `SWAGGER_ENABLED` | Si Swagger está visible o no | `true` solo en local; en producción siempre debe estar en `false` |
| `PORT` | Puerto donde corre el servidor | `8080` |

### Cómo generar la contraseña de Gmail

1. Activa la verificación en 2 pasos: [myaccount.google.com/security](https://myaccount.google.com/security)
2. Genera una contraseña de aplicación: [myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords)
3. Usa esos 16 caracteres (sin espacios) como `MAIL_PASSWORD`

Si no configuras el correo, el resto del sistema sigue funcionando normal — solo el envío de emails fallará en silencio (queda anotado en el log, pero no rompe nada para el usuario).

## Cómo levantar el proyecto

**Desde IntelliJ:** ejecuta `ColegioOssBackend01Application`.

**Desde la terminal:**
```bash
mvn spring-boot:run
```

La primera vez, crea tú mismo la base de datos vacía (`CREATE DATABASE colegio_oss_db;`) — el resto (tablas y datos base) se genera automáticamente al arrancar.

## Usuario de prueba ya incluido

```
usuario: admin
contraseña: Admin123!
```

Este es el único usuario que trae el sistema de fábrica. El resto de usuarios, estudiantes, apoderados y matrículas se crean navegando el propio sistema (registro público, formulario de matricular, etc.) — así las pruebas reflejan el flujo real de uso.

## Qué datos vienen precargados (`data.sql`)

El script es intencionalmente **mínimo**: solo lo esencial para que el sistema arranque y sea usable desde una base de datos vacía. Es idempotente y seguro de ejecutar en cada arranque (`spring.sql.init.mode=always`), porque cada `INSERT` usa `ON CONFLICT` sobre una columna que sí tiene restricción `UNIQUE` real.

**Permisos:** `USUARIO_CREAR`, `USUARIO_EDITAR`, `USUARIO_ELIMINAR`, `USUARIO_VER`, `MATRICULAR`, `MATRICULA_VER`, `MATRICULA_EDITAR`, `NOTA_VER`, `ASISTENCIA_VER`

**Roles:**
| Rol | Qué puede hacer |
|---|---|
| `ADMIN` | Todo (automáticamente incluye cualquier permiso nuevo que se agregue) |
| `DOCENTE` | Ver y editar usuarios |
| `ESTUDIANTE` | Solo ver usuarios |
| `ADMINISTRATIVO` | Matricular estudiantes y gestionar datos de matrícula |
| `APODERADO` | Ver notas y asistencia (para uso futuro) |

**Usuario:** solo el `admin` semilla.

> ⚠️ El rol que antes se llamaba `ALUMNO` fue renombrado a `ESTUDIANTE` para que coincida con el nombre de la entidad `Estudiante`. Si tienes datos antiguos con el rol `ALUMNO`, actualízalos manualmente: `UPDATE rol SET nombre = 'ESTUDIANTE' WHERE nombre = 'ALUMNO';`

## Documentación interactiva (Swagger)

**Disponible solo en local** — en producción (Railway) está deshabilitado por seguridad (`SWAGGER_ENABLED=false`), así que esta URL no funciona ahí.

```
http://localhost:8080/swagger-ui/index.html
```

Para probar endpoints que requieren estar logueado: haz login, copia el `token`, dale click a **"Authorize"** arriba a la derecha, pégalo (no hace falta escribir "Bearer ", se agrega solo).

## Endpoints disponibles

### Autenticación — no requiere estar logueado
| Método | Ruta | Qué hace |
|---|---|---|
| POST | `/api/auth/login` | Iniciar sesión |
| POST | `/api/auth/refresh` | Renovar el token cuando expira |
| POST | `/api/auth/register` | Registrarse como `ESTUDIANTE`, `DOCENTE` o `APODERADO`. Requiere `tipoDocumento` y `numeroDocumento`. `ESTUDIANTE`/`APODERADO` se activan de inmediato y crean automáticamente su registro académico vinculado (`Estudiante`/`Apoderado`); `DOCENTE` queda `PENDIENTE` de aprobación |
| POST | `/api/auth/forgot-password` | Pedir recuperar contraseña |
| POST | `/api/auth/reset-password` | Cambiar la contraseña con el enlace recibido |

### Usuarios — solo ADMIN
| Método | Ruta | Qué hace |
|---|---|---|
| GET | `/api/usuarios` | Listar usuarios (filtros opcionales `estado` y/o `rolId`) |
| GET | `/api/usuarios/pendientes` | Ver quién está esperando aprobación |
| PATCH | `/api/usuarios/{id}/aprobar` | Asignarle un rol y activar su cuenta. Si no se envía `rolId`, usa automáticamente el `rolSolicitado` guardado al momento del registro |
| PATCH | `/api/usuarios/{id}/cambiar-rol` | Cambiar el rol de un usuario ya activo |
| PATCH | `/api/usuarios/{id}/activar` \| `/desactivar` \| `/bloquear` | Cambiar el estado de la cuenta (un admin no puede hacerlo sobre sí mismo) |

> No existe hoy un endpoint para que un admin **cree** una cuenta directamente — la única forma de que un `Usuario` se cree es a través del registro público (`/api/auth/register`). Ver sección de pendientes más abajo.

### Permisos y Roles — solo ADMIN
- `/api/permisos` y `/api/roles`: crear, listar, editar, activar/desactivar (nunca se borran físicamente)
- `PUT /api/roles/{id}/permisos`: definir qué permisos tiene un rol

### Estudiantes y Apoderados — para ADMIN y ADMINISTRATIVO
- `/api/estudiantes` y `/api/apoderados`: crear, listar, buscar por documento, editar, activar/desactivar
- `/api/estudiantes/{id}/apoderados`: asignar o quitar apoderados de un estudiante

### Matrícula
| Método | Ruta | Qué hace |
|---|---|---|
| POST | `/api/matriculas` | Matricular a un estudiante (nuevo o existente) junto con sus apoderados, todo en un solo paso |
| GET | `/api/matriculas` | Listar matrículas (filtros opcionales: periodo, nivel, grado, estado) |
| GET | `/api/matriculas/{id}` | Ver el detalle completo de una matrícula (estudiante + apoderados) |
| PUT | `/api/matriculas/{id}` | Editar nivel, grado, sección, tipo o fecha de matrícula |
| PATCH | `/api/matriculas/{id}/retirar` | Marcar la matrícula como retirada (ej. expulsión a mitad de año) |
| PATCH | `/api/matriculas/{id}/trasladar` | Marcar la matrícula como trasladada |

## Todas las Historias de Usuario del proyecto

El proyecto completo tiene 12 historias de usuario, repartidas en 4 módulos.

### Módulo: Autenticación y accesos

| HU | Descripción | Prioridad | Estado |
|---|---|---|---|
| HU-01 | Iniciar sesión con usuario y contraseña | Alta | ✅ Completa |
| HU-02 | Recuperar contraseña en caso de olvido | Media | ✅ Completa |
| HU-03 | Gestionar roles y permisos de los usuarios | Alta | ✅ Completa |

### Módulo: Matrícula

| HU | Descripción | Prioridad | Estado |
|---|---|---|---|
| HU-04 | Registrar la matrícula de un nuevo estudiante | Alta | ✅ Completa |
| HU-05 | Registrar los datos del apoderado del estudiante | Media | ✅ Completa |
| HU-06 | Consultar y editar la información de matrícula | Media | ✅ Completa |

### Módulo: Notas

| HU | Descripción | Prioridad | Estado |
|---|---|---|---|
| HU-07 | Registrar notas de estudiantes por curso y periodo | Alta | ⏳ No iniciado |
| HU-08 | Calcular automáticamente el promedio del estudiante | Alta | ⏳ No iniciado |
| HU-09 | Consultar notas como padre de familia | Media | ⏳ No iniciado |

### Módulo: Asistencia

| HU | Descripción | Prioridad | Estado |
|---|---|---|---|
| HU-10 | Registrar asistencia diaria de los estudiantes | Alta | ⏳ No iniciado |
| HU-11 | Generar reportes de inasistencias | Media | ⏳ No iniciado |
| HU-12 | Consultar asistencia como padre de familia | Baja | ⏳ No iniciado |

## Reglas de negocio que hay que tener presentes

- **Nadie puede autoasignarse el rol ADMIN** — ni al registrarse, ni cuando un admin aprueba a alguien pendiente. Es intencional, por seguridad.
- **Nada se borra de verdad** — usuarios, roles, permisos, estudiantes y apoderados solo se "desactivan", nunca se eliminan de la base de datos.
- **El código de un permiso y el nombre de un rol no se pueden cambiar** una vez creados (evita romper referencias). Los datos de estudiantes y apoderados sí son editables.
- **El registro público crea automáticamente el registro académico correspondiente**: registrarse como `ESTUDIANTE` crea un `Estudiante` vinculado; como `APODERADO`, crea un `Apoderado` vinculado. Ambos quedan con `usuario_id` asociado, permitiendo a futuro que esa persona consulte su propia información (notas, asistencia).
- **El campo `rolSolicitado`** queda guardado en el `Usuario` desde el registro, para que el admin sepa qué rol pidió la persona antes de aprobarla — sin necesidad de asignarlo automáticamente, aunque `aprobarUsuario` sí lo usa como default si no se especifica otro.
- **Un estudiante no puede tener 2 matrículas activas en el mismo periodo** — si lo intentas, el sistema lo rechaza.
- **Matricular exige al menos un apoderado** — no se puede dejar sin registrar. Es responsabilidad del frontend decidir cuántos pedir (el frontend propio de este proyecto pide solo 1, el que acompaña el trámite).
- **La relación estudiante-apoderado es permanente**, no depende del periodo de matrícula — vive en `Estudiante`/`Apoderado`, no en `Matricula`. Si un apoderado ya estaba vinculado a un estudiante de una matrícula anterior, el sistema lo reutiliza automáticamente sin duplicar ni dar error.
- **Los pagos (matrícula y pensiones) no están en este alcance todavía** — la idea a futuro es que se generen solos y queden "pendientes de pago", sin que alguien tenga que crearlos uno por uno a mano.

## Cosas técnicas que vale la pena saber

- El proyecto crea las tablas automáticamente al arrancar, pero **no modifica** columnas que ya existen si cambias algo — si tocas una entidad ya usada en producción, puede requerir un ajuste manual en la base de datos.
- Al validar el token JWT, el sistema a veces necesita traer datos relacionados (como el rol de un usuario) de una forma especial (`JOIN FETCH`) para evitar un error típico de Hibernate (`LazyInitializationException`).
- Gmail exige una "contraseña de aplicación" especial, no tu contraseña normal, para poder enviar correos desde el sistema.
- El archivo `.env` se carga solo, gracias a la librería `dotenv` — no hace falta configurar nada más en tu máquina.
- A veces, durante desarrollo, Spring Boot DevTools puede confundirse después de muchos cambios seguidos y tirar errores raros (clases o configuraciones "no encontradas" que sí existen). Si pasa: detén la app por completo, borra la carpeta `target`, y reconstruye el proyecto antes de volver a ejecutar.
- **`data.sql` y `ON CONFLICT DO NOTHING` solo protegen contra duplicados si la columna tiene una restricción `UNIQUE` real en la base de datos.** Si insertas datos de prueba en una tabla sin esa restricción (por ejemplo, en su momento pasó con `estudiante_apoderado` y con `matricula`), cada reinicio del backend vuelve a insertar las mismas filas, generando duplicados silenciosos que no dan error hasta que alguien nota los conteos raros. Por eso `data.sql` se mantiene mínimo: solo contiene inserts sobre columnas realmente únicas (`permiso.codigo`, `rol.nombre`, `usuario.username`). Cualquier dato de prueba adicional (estudiantes, matrículas, etc.) se crea navegando el sistema, no desde `data.sql`.

## Despliegue en producción (Railway)

El backend ya está desplegado y funcionando en Railway. Configuración clave:

- El proyecto usa Java 21, que coincide con la versión que Railway instala por defecto — no fue necesario ningún ajuste adicional para que el build funcione
- Swagger está deshabilitado en producción (`SWAGGER_ENABLED=false`)
- Todas las variables de entorno de la tabla de arriba están configuradas directamente en el panel de Railway, con valores propios de producción (distintos a los de desarrollo, especialmente `JWT_SECRET`)

## Frontend

Existe un frontend Angular propio de este proyecto (`colegio-oss-frontend-v2`), independiente del desarrollado por otro integrante del equipo. Cubre completamente HU-01 a HU-06:

- Login unificado (un solo formulario, el backend determina el rol)
- Registro con selector de rol y documento
- Recuperar / restablecer contraseña
- Gestión de usuarios (listar, aprobar, cambiar rol, activar/desactivar/bloquear)
- CRUD de Roles y Permisos, con asignación de permisos a roles
- CRUD de Estudiantes y Apoderados, con gestión de la relación entre ambos
- Matricular (flujo guiado de 3 pasos: estudiante, apoderado, datos académicos)
- Consultar y editar matrículas (con filtros, retirar, trasladar)

Construido con Angular 21 standalone components, señales (`signal`/`computed`), un componente de tabla reutilizable (búsqueda, orden, paginación, badges, responsive) y modales reutilizables (formulario genérico y selección).

## Pendientes conocidos

- **No existe un endpoint ni pantalla para que un admin cree un `Usuario` directamente** — hoy la única vía es el registro público. Si se necesita crear cuentas de `ADMINISTRATIVO` o `DOCENTE` sin pasar por auto-registro, hay que construir `POST /api/usuarios` (backend) y su formulario correspondiente (frontend).
- Módulo de Notas (HU-07, HU-08, HU-09) — no iniciado. Requiere definir entidades académicas nuevas (`Aula`, `Curso`, `AulaCurso`, `DocenteCurso`, `Evaluacion`, `CursoEvaluacion`, `Nota`).
- Módulo de Asistencia (HU-10, HU-11, HU-12) — no iniciado. Requiere `Clase`, `Asistencia`.
- Pruebas unitarias del módulo de Matrícula y de `UsuarioServiceImpl`/`RolServiceImpl`/`PermisoServiceImpl` — pendiente de ampliar cobertura (ver sección de pruebas).

## Pruebas automatizadas

```bash
mvn test
```

**Cobertura actual — todos los módulos de Autenticación y Matrícula tienen pruebas unitarias:**

| Archivo de test | Qué cubre |
|---|---|
| `AuthServiceImplTest` | Login, refresh, registro (incluyendo creación automática de Estudiante/Apoderado), recuperación de contraseña |
| `PermisoServiceImplTest` | CRUD de permisos |
| `RolServiceImplTest` | CRUD de roles, asignación de permisos |
| `UsuarioServiceImplTest` | Listar, aprobar, cambiar rol, cambiar estado |
| `EstudianteServiceImplTest` | CRUD de estudiantes |
| `ApoderadoServiceImplTest` | CRUD de apoderados |
| `EstudianteApoderadoServiceImplTest` | Asignar/quitar apoderados, marcar principal |
| `MatricularServiceImplTest` | Flujo transaccional de matricular (HU-04) |
| `MatriculaServiceImplTest` | Consultar y editar matrícula (HU-06) |
| `AuthControllerIntegrationTest` | Pruebas de integración con H2 en memoria |

En total, más de 90 pruebas unitarias entre todos los servicios.

**Lo que todavía no tiene pruebas (pendiente, para cuando se implementen):**
- Módulo de Notas (HU-07, HU-08, HU-09)
- Módulo de Asistencia (HU-10, HU-11, HU-12)

## Antes de subir cambios a Git

Revisa que `application.properties` no tenga ninguna contraseña real escrita, y que tu archivo `.env` nunca se suba (debe estar en `.gitignore`). Si alguna vez un secreto se llegó a subir por error, considéralo comprometido y genera uno nuevo — quitarlo del control de versiones no borra lo que ya quedó en el historial de Git.