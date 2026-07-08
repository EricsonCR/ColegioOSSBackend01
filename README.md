# ColegioOSSBackend01

Backend del sistema de gestión del colegio — módulo de autenticación, autorización y gestión de roles/permisos.

## Stack tecnológico

- **Java 26**
- **Spring Boot 4.1.0**
- **Maven**
- **PostgreSQL**
- **Spring Security + JWT** (autenticación stateless)
- **Swagger / OpenAPI** (documentación de la API)
- **Lombok**
- **Spring Mail** (Gmail SMTP, para recuperación de contraseña)

## Dependencias principales

| Dependencia | Uso |
|---|---|
| `spring-boot-starter-data-jpa` | Persistencia con JPA/Hibernate |
| `spring-boot-starter-webmvc` | API REST (equivalente a `spring-boot-starter-web` en versiones anteriores) |
| `spring-boot-starter-security` | Autenticación y autorización |
| `spring-boot-starter-validation` | Validaciones con Bean Validation (`@NotBlank`, `@Email`, etc.) |
| `spring-boot-starter-mail` | Envío de correos (recuperación de contraseña) |
| `spring-boot-devtools` | Recarga automática en desarrollo |
| `postgresql` | Driver JDBC de PostgreSQL |
| `lombok` | Reduce boilerplate (getters/setters/builders) |
| `io.jsonwebtoken:jjwt-api` / `jjwt-impl` / `jjwt-jackson` | Generación y validación de JWT |
| `org.springdoc:springdoc-openapi-starter-webmvc-ui` | Documentación Swagger/OpenAPI |
| `spring-boot-starter-webmvc-test` | Testing (JUnit, Mockito, MockMvc) |
| `h2` (scope test) | Base de datos en memoria para pruebas de integración |

Todas las versiones están gestionadas por el `spring-boot-starter-parent` (BOM), excepto `jjwt-*` y `springdoc-openapi`, que requieren versión explícita en el `pom.xml`.

## Requisitos previos

Antes de levantar el proyecto, asegúrate de tener instalado:

- **JDK 26**
- **Maven** (o usar el wrapper incluido, si aplica)
- **PostgreSQL** corriendo localmente (o acceso a una instancia remota, ej. Railway)
- **Cuenta de Gmail** con verificación en 2 pasos activada, si vas a probar el flujo de recuperación de contraseña (ver sección [Configurar Gmail](#configurar-gmail-opcional))

## Configuración inicial

### 1. Crear la base de datos

Crea una base de datos PostgreSQL **vacía** (sin tablas):

```sql
CREATE DATABASE colegio_oss_db;
```

No necesitas crear tablas manualmente — Hibernate las genera automáticamente al levantar el proyecto (`spring.jpa.hibernate.ddl-auto=update`), y el archivo `data.sql` inserta los datos base (roles, permisos, usuario administrador).

### 2. Configurar variables de entorno (o editar `application.properties` directamente)

El proyecto usa variables de entorno con valores por defecto para desarrollo local. Puedes definirlas como variables de entorno de tu sistema/IDE, o simplemente editar los valores por defecto directamente en `src/main/resources/application.properties`.

| Variable | Descripción | Valor por defecto (local) |
|---|---|---|
| `SPRING_DATASOURCE_URL` | URL JDBC de PostgreSQL | `jdbc:postgresql://localhost:5432/colegio_oss_db` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de PostgreSQL | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de PostgreSQL | `postgres` |
| `JWT_SECRET` | Secreto para firmar los JWT (mínimo 256 bits) | placeholder de desarrollo, **cambiar en producción** |
| `MAIL_USERNAME` | Correo Gmail remitente | *(requerido para probar recuperación de contraseña)* |
| `MAIL_PASSWORD` | Contraseña de aplicación de Gmail (no la contraseña normal) | *(requerido para probar recuperación de contraseña)* |
| `FRONTEND_RESET_URL` | URL del frontend donde se arma el enlace de recuperación | `http://localhost:3000/reset-password` |
| `PORT` | Puerto del servidor | `8080` |

### 3. Configurar Gmail (opcional)

Solo necesario si vas a probar el flujo de recuperación de contraseña (envío real de correos):

1. Activa la verificación en 2 pasos en tu cuenta de Gmail: [https://myaccount.google.com/security](https://myaccount.google.com/security)
2. Genera una contraseña de aplicación: [https://myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords)
3. Usa esa contraseña de 16 caracteres (sin espacios) como `MAIL_PASSWORD`

Si no configuras esto, el resto del proyecto funciona con normalidad — solo el envío de correos fallará silenciosamente (queda registrado en el log, no interrumpe el flujo del usuario).

## Cómo ejecutar el proyecto

**Desde IntelliJ IDEA:** ejecuta la clase `ColegioOssBackend01Application`.

**Desde terminal (Maven):**
```bash
mvn spring-boot:run
```

Al arrancar, Hibernate crea las tablas y el `data.sql` inserta los datos base automáticamente. La aplicación queda disponible en `http://localhost:8080`.

## Datos iniciales (`data.sql`)

Al levantar el proyecto por primera vez, `src/main/resources/data.sql` inserta automáticamente:

**Permisos:**
- `USUARIO_CREAR`, `USUARIO_EDITAR`, `USUARIO_ELIMINAR`, `USUARIO_VER`

**Roles y sus permisos:**
| Rol | Permisos asignados |
|---|---|
| `ADMIN` | Todos |
| `DOCENTE` | `USUARIO_VER`, `USUARIO_EDITAR` |
| `ALUMNO` | `USUARIO_VER` |

**Usuario administrador semilla:**
| Campo | Valor |
|---|---|
| `username` | `admin` |
| `email` | `admin@colegio.edu.pe` |
| `password` | `Admin123!` |
| `nombreCompleto` | Administrador del Sistema |
| `estado` | `ACTIVO` |
| `rol` | `ADMIN` |

El script es **idempotente**: usa `ON CONFLICT DO NOTHING`, por lo que se puede ejecutar múltiples veces (por ejemplo, en cada arranque de la aplicación) sin duplicar datos ni lanzar errores. Si necesitas agregar más datos semilla (otros roles, permisos, usuarios de prueba), edita este archivo siguiendo el mismo patrón.

## Documentación de la API (Swagger)

Con el proyecto corriendo, accede a:

```
http://localhost:8080/swagger-ui/index.html
```

Para probar endpoints protegidos: haz login (`POST /api/auth/login`), copia el `token` de la respuesta, haz clic en **"Authorize"** en la parte superior de Swagger, pega el token (sin necesidad de escribir "Bearer ", se agrega automáticamente) y confirma.

## Endpoints — referencia rápida

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/auth/login` | Público | Inicio de sesión |
| POST | `/api/auth/refresh` | Público | Renovar access token |
| POST | `/api/auth/register` | Público | Registro (ALUMNO/DOCENTE) |
| POST | `/api/auth/forgot-password` | Público | Solicitar recuperación de contraseña |
| POST | `/api/auth/reset-password` | Público | Restablecer contraseña con token |
| POST | `/api/permisos` | ADMIN | Crear permiso |
| GET | `/api/permisos` | ADMIN | Listar permisos (`?incluirInactivos=true` opcional) |
| GET | `/api/permisos/{id}` | ADMIN | Obtener permiso por ID |
| PUT | `/api/permisos/{id}` | ADMIN | Actualizar descripción |
| PATCH | `/api/permisos/{id}/desactivar` | ADMIN | Desactivar permiso |
| PATCH | `/api/permisos/{id}/activar` | ADMIN | Activar permiso |
| POST | `/api/roles` | ADMIN | Crear rol |
| GET | `/api/roles` | ADMIN | Listar roles (`?incluirInactivos=true` opcional) |
| GET | `/api/roles/{id}` | ADMIN | Obtener rol por ID (incluye sus permisos) |
| PUT | `/api/roles/{id}` | ADMIN | Actualizar descripción |
| PATCH | `/api/roles/{id}/desactivar` | ADMIN | Desactivar rol |
| PATCH | `/api/roles/{id}/activar` | ADMIN | Activar rol |
| PUT | `/api/roles/{id}/permisos` | ADMIN | Asignar/reemplazar permisos del rol |

## Reglas de negocio importantes

- **Registro de usuarios**: el endpoint público `/api/auth/register` solo permite solicitar rol `ALUMNO` (activación inmediata) o `DOCENTE` (queda en estado `PENDIENTE`, sin rol asignado, hasta que un administrador lo apruebe). **Nunca** se puede solicitar `ADMIN` desde el registro público — esto es intencional, para evitar que cualquiera se autoasigne privilegios de administrador.
- **Soft delete**: ninguna entidad (`Usuario`, `Rol`, `Permiso`) se elimina físicamente de la base de datos. En su lugar, se usan campos de estado (`estado` en `Usuario`, `activo` en `Rol` y `Permiso`) para activar/desactivar.
- **Inmutabilidad de identificadores**: el `codigo` de un `Permiso` y el `nombre` de un `Rol` no pueden modificarse una vez creados — solo su descripción.
- **Asignación de permisos a roles**: el endpoint `PUT /api/roles/{id}/permisos` reemplaza el conjunto completo de permisos del rol con la lista enviada (no es un "agregar" o "quitar" incremental). Solo se pueden asignar permisos que estén `activo = true`.

## Historias de Usuario — estado y avance

### HU-01 — Login
> Como usuario del sistema, quiero iniciar sesión con mi usuario y contraseña, para acceder a las funcionalidades según mi rol.

**Estado: ✅ Completa**

- Login con JWT (access token 5 min + refresh token 7 días)
- Renovación de access token vía refresh token (`POST /api/auth/refresh`)
- Registro público de usuarios (`POST /api/auth/register`): rol `ALUMNO` se activa de inmediato, rol `DOCENTE` queda `PENDIENTE` de aprobación
- Seguridad: contraseñas hasheadas con BCrypt, rutas protegidas con Spring Security, manejo centralizado de errores (`GlobalExceptionHandler`)
- Documentación Swagger con esquema Bearer
- Pruebas unitarias del `AuthServiceImpl` (login, refresh, register)

### HU-02 — Recuperación de contraseña
> Como usuario, quiero recuperar mi contraseña en caso de olvido, para no perder el acceso al sistema.

**Estado: ✅ Completa**

- `POST /api/auth/forgot-password`: genera un token de recuperación (UUID, expira en 30 min, uso único) y envía un enlace por correo (Gmail SMTP)
- `POST /api/auth/reset-password`: valida el token (existe, no expirado, no usado) y actualiza la contraseña
- Por seguridad, `forgot-password` responde con el mismo mensaje genérico exista o no el correo en el sistema (no revela qué emails están registrados)
- Pruebas unitarias de `forgotPassword` y `resetPassword`

### HU-03 — Gestión de roles y permisos
> Como administrador, quiero gestionar los roles y permisos de los usuarios, para controlar el acceso a la información.

**Estado: 🔶 En progreso** — se dividió en 7 puntos, implementados de forma incremental:

| # | Funcionalidad | Estado |
|---|---|---|
| 1 | CRUD de Permisos (`/api/permisos`) | ✅ Completo |
| 2 | CRUD de Roles (`/api/roles`) | ✅ Completo |
| 3 | Asignar/quitar permisos a un rol (`PUT /api/roles/{id}/permisos`) | ✅ Completo |
| 4 | Listar usuarios (con filtros por estado/rol) | ⏳ Pendiente |
| 5 | Aprobar usuarios `PENDIENTE` (asignar rol y activar) | ⏳ Pendiente |
| 6 | Cambiar el rol de un usuario ya activo | ⏳ Pendiente |
| 7 | Activar/desactivar/bloquear usuarios | ⏳ Pendiente |

Los puntos 1-3 incluyen: soft delete (activar/desactivar en vez de eliminar), inmutabilidad de `codigo`/`nombre`, protección con `@PreAuthorize("hasRole('ADMIN')")`, y solo permite asignar permisos activos a un rol.

Sin los puntos 4-5, los usuarios que se registran como `DOCENTE` quedan indefinidamente en estado `PENDIENTE`, sin forma de ser aprobados desde la API todavía.

## Pendiente de definir

- Qué debe pasar si se desactiva un `Permiso` que ya está asignado a uno o más `Rol` — actualmente no se bloquea la desactivación, y el permiso permanece en la relación del rol (falta decidir si un permiso inactivo debe dejar de otorgar acceso real aunque el rol lo tenga asignado).
- **Migraciones de base de datos**: el proyecto usa `ddl-auto=update` en vez de una herramienta de migraciones versionadas (Flyway/Liquibase). Es válido para una base de datos nueva, pero cualquier cambio de esquema sobre una base de datos con datos existentes puede requerir ajustes manuales (ver nota técnica abajo).

## Notas técnicas relevantes

- **`ddl-auto=update`** solo agrega tablas/columnas nuevas — no modifica ni elimina constraints existentes (ej. `NOT NULL`, `CHECK`). Esto no es un problema en una base de datos nueva (como al clonar este proyecto por primera vez), pero si alguna vez se modifica una entidad ya desplegada con datos reales, puede requerir un `ALTER TABLE` manual o migrar a una herramienta de migraciones versionadas (Flyway/Liquibase).
- **Relaciones `LAZY`**: las consultas que necesitan acceder a relaciones lazy fuera del ciclo normal de una petición HTTP (por ejemplo, dentro del filtro de autenticación JWT) usan `JOIN FETCH` explícito en el repositorio para evitar `LazyInitializationException`.
- **Contraseñas de Gmail**: Google exige "contraseñas de aplicación" (16 caracteres) para conexiones SMTP externas — la contraseña normal de la cuenta no funciona y Google la rechaza con error de autenticación.
- **Jackson 2 vs 3**: Spring Boot 4 autoconfigura Jackson 3 por defecto. En los pocos lugares donde se serializa JSON manualmente fuera del flujo estándar de Spring MVC (filtros de seguridad), se evita depender de un `ObjectMapper` de Jackson 2 (que no está autoconfigurado como bean) construyendo el JSON de forma simple y directa.

## Cómo correr las pruebas

```bash
mvn test
```

Las pruebas unitarias (`AuthServiceImplTest`) usan Mockito y no requieren base de datos. Las pruebas de integración (`AuthControllerIntegrationTest`) usan una base de datos H2 en memoria (perfil `test`), independiente de la base de datos de desarrollo.

## Seguridad al contribuir

Antes de hacer commit, verifica que `application.properties` no contenga credenciales reales (contraseña de Gmail, credenciales de base de datos de producción, JWT secret real). Este proyecto usa variables de entorno con valores por defecto de desarrollo — mantén esa convención para no exponer secretos en el repositorio.