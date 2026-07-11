# ColegioOSSBackend01

Backend del sistema de gestión del colegio. Este documento explica qué hace el proyecto, cómo levantarlo en tu máquina, y en qué estado está cada funcionalidad — pensado para que cualquiera del equipo (o alguien nuevo que se sume) pueda entenderlo sin depender de que se lo expliquen en persona.

## ¿Qué es este proyecto?

Un backend en Spring Boot para un sistema escolar, con 4 módulos:

1. **Autenticación** — login, recuperación de contraseña, roles y permisos
2. **Matrícula** — registro de estudiantes, apoderados y matrículas
3. **Notas** — registro de calificaciones y promedios (próximamente)
4. **Asistencia** — control de asistencia diaria y reportes (próximamente)

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
| `FRONTEND_RESET_URL` | A dónde apunta el enlace de recuperar contraseña | `http://localhost:3000/reset-password` |
| `CORS_ALLOWED_ORIGINS` | Qué frontend(s) pueden llamar a esta API | `http://localhost:4200` |
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

## Qué datos vienen precargados

**Permisos:** `USUARIO_CREAR`, `USUARIO_EDITAR`, `USUARIO_ELIMINAR`, `USUARIO_VER`, `MATRICULAR`, `MATRICULA_VER`, `MATRICULA_EDITAR`

**Roles:**
| Rol | Qué puede hacer |
|---|---|
| `ADMIN` | Todo (automáticamente incluye cualquier permiso nuevo que se agregue) |
| `DOCENTE` | Ver y editar usuarios |
| `ALUMNO` | Solo ver usuarios |
| `ADMINISTRATIVO` | Matricular estudiantes y gestionar datos de matrícula |

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
| POST | `/api/auth/register` | Registrarse (alumno se activa al toque, docente queda esperando aprobación) |
| POST | `/api/auth/forgot-password` | Pedir recuperar contraseña |
| POST | `/api/auth/reset-password` | Cambiar la contraseña con el enlace recibido |

### Permisos y Roles — solo ADMIN
- `/api/permisos` y `/api/roles`: crear, listar, editar, activar/desactivar (nunca se borran físicamente)
- `PUT /api/roles/{id}/permisos`: definir qué permisos tiene un rol

### Usuarios — solo ADMIN
| Método | Ruta | Qué hace |
|---|---|---|
| GET | `/api/usuarios/pendientes` | Ver quién está esperando aprobación (ej. docentes recién registrados) |
| PATCH | `/api/usuarios/{id}/aprobar` | Asignarle un rol y activar su cuenta |

### Estudiantes y Apoderados — para ADMIN y ADMINISTRATIVO
- `/api/estudiantes` y `/api/apoderados`: crear, listar, buscar por documento, editar, activar/desactivar
- `/api/estudiantes/{id}/apoderados`: asignar o quitar apoderados de un estudiante

### Matrícula
| Método | Ruta | Qué hace |
|---|---|---|
| POST | `/api/matriculas` | Matricular a un estudiante (nuevo o existente) junto con sus apoderados, todo en un solo paso |

*(Consultar y editar una matrícula ya existente todavía no está implementado — ver la lista de pendientes más abajo)*

## Todas las Historias de Usuario del proyecto

El proyecto completo tiene 12 historias de usuario, repartidas en 4 módulos y 4 personas del equipo. Esta tabla es el mapa general — de acá se desprende todo lo demás.

### Módulo: Autenticación y accesos

| HU | Descripción | Prioridad | Estado |
|---|---|---|---|
| HU-01 | Iniciar sesión con usuario y contraseña | Alta | ✅ Completa |
| HU-02 | Recuperar contraseña en caso de olvido | Media | ✅ Completa |
| HU-03 | Gestionar roles y permisos de los usuarios | Alta | 🔶 En progreso (ver detalle abajo) |

### Módulo: Matrícula

| HU | Descripción | Prioridad | Estado |
|---|---|---|---|
| HU-04 | Registrar la matrícula de un nuevo estudiante | Alta | ✅ Completa |
| HU-05 | Registrar los datos del apoderado del estudiante | Media | ✅ Completa |
| HU-06 | Consultar y editar la información de matrícula | Media | ⏳ Pendiente |

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

### Detalle de HU-03 (la única parcialmente terminada)

| # | Qué falta | Estado |
|---|---|---|
| 1 | CRUD de Permisos | ✅ |
| 2 | CRUD de Roles | ✅ |
| 3 | Asignar/quitar permisos a un rol | ✅ |
| 4 | Listar usuarios con filtros generales | ⏳ Pendiente |
| 5 | Aprobar usuarios pendientes (asignar rol y activar) | ✅ |
| 6 | Cambiar el rol de un usuario ya activo | ⏳ Pendiente |
| 7 | Activar/desactivar/bloquear usuarios | ⏳ Pendiente |

## Reglas de negocio que hay que tener presentes

- **Nadie puede autoasignarse el rol ADMIN** — ni al registrarse, ni cuando un admin aprueba a alguien pendiente. Es intencional, por seguridad.
- **Nada se borra de verdad** — usuarios, roles, permisos, estudiantes y apoderados solo se "desactivan", nunca se eliminan de la base de datos.
- **El código de un permiso y el nombre de un rol no se pueden cambiar** una vez creados (evita romper referencias). Los datos de estudiantes y apoderados sí son editables.
- **Un estudiante no puede tener 2 matrículas activas en el mismo periodo** — si lo intentas, el sistema lo rechaza.
- **Matricular exige al menos un apoderado** — no se puede dejar sin registrar.
- **Si un apoderado ya estaba vinculado a un estudiante de una matrícula anterior**, el sistema lo reutiliza automáticamente sin duplicar ni dar error.
- **Los pagos (matrícula y pensiones) no están en este alcance todavía** — la idea a futuro es que se generen solos y queden "pendientes de pago", sin que alguien tenga que crearlos uno por uno a mano.

## Cosas técnicas que vale la pena saber

- El proyecto crea las tablas automáticamente al arrancar, pero **no modifica** columnas que ya existen si cambias algo — si tocas una entidad ya usada en producción, puede requerir un ajuste manual en la base de datos.
- Al validar el token JWT, el sistema a veces necesita traer datos relacionados (como el rol de un usuario) de una forma especial (`JOIN FETCH`) para evitar un error típico de Hibernate (`LazyInitializationException`).
- Gmail exige una "contraseña de aplicación" especial, no tu contraseña normal, para poder enviar correos desde el sistema.
- El archivo `.env` se carga solo, gracias a la librería `dotenv` — no hace falta configurar nada más en tu máquina.
- A veces, durante desarrollo, Spring Boot DevTools puede confundirse después de muchos cambios seguidos y tirar errores raros (clases o configuraciones "no encontradas" que sí existen). Si pasa: detén la app por completo, borra la carpeta `target`, y reconstruye el proyecto antes de volver a ejecutar.

## Despliegue en producción (Railway)

El backend ya está desplegado y funcionando en Railway. Configuración clave:

- El proyecto usa Java 21, que coincide con la versión que Railway instala por defecto — no fue necesario ningún ajuste adicional para que el build funcione
- Swagger está deshabilitado en producción (`SWAGGER_ENABLED=false`)
- Todas las variables de entorno de la tabla de arriba están configuradas directamente en el panel de Railway, con valores propios de producción (distintos a los de desarrollo, especialmente `JWT_SECRET`)

## Pruebas automatizadas

```bash
mvn test
```

**Lo que ya tiene pruebas:**
- `AuthServiceImplTest`: pruebas unitarias con Mockito (login, refresh, registro, recuperación de contraseña) — no necesitan base de datos
- `AuthControllerIntegrationTest`: pruebas de integración con una base de datos H2 en memoria (separada de tu base de datos real), probando el flujo completo a través del controller

**Lo que todavía no tiene pruebas (pendiente):**
- Módulo de Matrícula completo (`EstudianteService`, `ApoderadoService`, `EstudianteApoderadoService`, `MatricularService`)
- CRUD de Permisos y Roles
- Aprobación de usuarios pendientes

Mi recomendación: antes de seguir sumando funcionalidad nueva (Notas, Asistencia), conviene cerrar esta brecha con al menos pruebas unitarias del módulo de Matrícula — es la parte más compleja del proyecto hasta ahora (transacciones, validaciones de reglas de negocio como "una matrícula activa por periodo"), y es justo donde más fácil se cuela un bug si se modifica algo después sin darse cuenta.

## Antes de subir cambios a Git

Revisa que `application.properties` no tenga ninguna contraseña real escrita, y que tu archivo `.env` nunca se suba (debe estar en `.gitignore`). Si alguna vez un secreto se llegó a subir por error, considéralo comprometido y genera uno nuevo — quitarlo del control de versiones no borra lo que ya quedó en el historial de Git.