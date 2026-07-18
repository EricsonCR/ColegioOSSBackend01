# ColegioOSSBackend01

Backend del sistema de gestión del colegio. Este documento explica qué hace el proyecto, cómo levantarlo en tu máquina, y en qué estado está cada funcionalidad.

## ¿Qué es este proyecto?

Un backend en Spring Boot para un sistema escolar, con 4 módulos:

1. **Autenticación** — login, recuperación de contraseña, roles y permisos ✅ completo
2. **Matrícula** — registro de estudiantes, apoderados y matrículas ✅ completo
3. **Notas** — registro de calificaciones (HU-07 completa; HU-08 y HU-09 pendientes)
4. **Asistencia** — control de asistencia diaria (HU-10 en construcción; HU-11 y HU-12 pendientes)

## Stack tecnológico

- **Java 21**, **Spring Boot 4.1.0**, **Maven**, **PostgreSQL**
- **Spring Security + JWT**
- **Swagger / OpenAPI**
- **Lombok**
- **Spring Mail**
- **springboot4-dotenv**

## Cómo levantar el proyecto

```bash
mvn spring-boot:run
```

Crea tú mismo la base de datos vacía (`CREATE DATABASE colegio_oss_db;`) — el resto se genera automáticamente.

## Usuario de prueba

```
usuario: admin
contraseña: Admin123!
```

`data.sql` es intencionalmente mínimo (permisos, roles, el admin) — el resto de datos se crean navegando el sistema. Ver la nota técnica al final sobre por qué no se agregan más datos de prueba directamente ahí.

## Variables de entorno

| Variable | Para qué es |
|---|---|
| `SPRING_DATASOURCE_URL` / `_USERNAME` / `_PASSWORD` | Conexión a PostgreSQL |
| `JWT_SECRET` | Clave para firmar tokens |
| `JWT_ACCESS_EXPIRATION_MS` / `JWT_REFRESH_EXPIRATION_MS` | Duración de tokens |
| `MAIL_HOST` / `_PORT` / `_USERNAME` / `_PASSWORD` | Envío de correos (Gmail) |
| `FRONTEND_RESET_URL` | URL del frontend para el enlace de recuperación |
| `CORS_ALLOWED_ORIGINS` | Orígenes permitidos, separados por coma |
| `SWAGGER_ENABLED` | `true` solo en local |
| `PORT` | Puerto del servidor |

## Documentación interactiva (Swagger)

Solo en local: `http://localhost:8080/swagger-ui/index.html`

## Endpoints disponibles

### Autenticación y accesos (HU-01 — completa)
| Método | Ruta | Qué hace |
|---|---|---|
| POST | `/api/auth/login` | Iniciar sesión |
| POST | `/api/auth/refresh` | Renovar el token de acceso |
| POST | `/api/auth/register` | Registrarse (ESTUDIANTE/APODERADO activo inmediato con vínculo automático; DOCENTE queda pendiente) |
| POST | `/api/auth/forgot-password` | Solicitar recuperación de contraseña |
| POST | `/api/auth/reset-password` | Restablecer contraseña con el token recibido |

### Usuarios y accesos (HU-03 — completa) — solo ADMIN
| Método | Ruta | Qué hace |
|---|---|---|
| GET | `/api/usuarios` | Listar (filtros `estado`, `rolId`) |
| GET | `/api/usuarios/pendientes` | Ver pendientes de aprobación |
| POST | `/api/usuarios` | Crear usuario directo, ya activo (no permite roles ESTUDIANTE/APODERADO — esos deben auto-registrarse) |
| PATCH | `/api/usuarios/{id}/aprobar` | Aprobar pendiente (usa `rolSolicitado` si no se envía `rolId`) |
| PATCH | `/api/usuarios/{id}/cambiar-rol` | Cambiar rol |
| PATCH | `/api/usuarios/{id}/activar` \| `/desactivar` \| `/bloquear` | Cambiar estado |

### Permisos y Roles — solo ADMIN
CRUD completo con soft delete + `PUT /api/roles/{id}/permisos`

### Estudiantes y Apoderados — ADMIN y ADMINISTRATIVO
CRUD completo + `POST/GET/PATCH /api/estudiantes/{id}/apoderados`

### Matrícula (HU-04 — completa)
| Método | Ruta | Qué hace |
|---|---|---|
| POST | `/api/matriculas` | Matricular (estudiante nuevo/existente + un apoderado + datos académicos, todo en una transacción) |
| GET | `/api/matriculas` | Listar (filtros: periodo, nivel, grado, estado) |
| GET | `/api/matriculas/{id}` | Ver detalle completo (estudiante + apoderados) |
| PUT | `/api/matriculas/{id}` | Editar nivel, grado, sección, tipo o fecha |
| PATCH | `/api/matriculas/{id}/retirar` | Marcar como retirada |
| PATCH | `/api/matriculas/{id}/trasladar` | Marcar como trasladada |

### Módulo académico (soporte para Notas y Asistencia) — ADMIN, algunas también DOCENTE
| Recurso | Endpoints |
|---|---|
| `/api/cursos` | CRUD completo (catálogo de materias) |
| `/api/evaluaciones` | CRUD completo (catálogo: Examen, Práctica, etc.) |
| `/api/aulas` | CRUD completo (periodo, nivel, grado, sección) |
| `/api/aula-cursos` | `POST`, `GET ?aulaId=` — asigna un curso a una aula, con horas semanales |
| `/api/docente-cursos` | `POST`, `GET ?usuarioId=`, `GET /mis-cursos`, `GET /por-aula-curso?aulaCursoId=` — asigna un docente a un curso-aula |
| `/api/curso-evaluaciones` | CRUD completo — componentes de evaluación por curso-aula y bimestre, con `porcentaje` (valida que la suma no supere 100% por bimestre) |

### Notas (HU-07 — completa)
| Método | Ruta | Qué hace |
|---|---|---|
| POST | `/api/notas` | Registrar o corregir una nota (si ya existe para ese estudiante+componente, la actualiza en vez de duplicar) |
| GET | `/api/notas/por-matricula?matriculaId=` | Notas de un estudiante |
| GET | `/api/notas/por-componente?cursoEvaluacionId=` | Notas de todos en un componente |
| GET | `/api/notas/consolidado?cursoEvaluacionId=` | **La vista clave**: lista TODOS los estudiantes matriculados en el aula de ese componente, tengan o no nota registrada — es lo que usa el frontend para que el docente vea a quién le falta calificar |

### Asistencia (HU-10 — en construcción)
| Método | Ruta | Qué hace |
|---|---|---|
| POST | `/api/clases` | Registrar una sesión de clase dictada (fecha, tema) |
| GET | `/api/clases?aulaCursoId=` | Listar clases de un curso |
| POST | `/api/asistencias` | Registrar o corregir la asistencia de un estudiante en una clase |
| GET | `/api/asistencias/consolidado?claseId=` | Lista todos los matriculados de esa aula con su estado de asistencia (mismo patrón que el consolidado de notas) |

## Todas las Historias de Usuario

### Módulo: Autenticación y accesos
| HU | Descripción | Estado |
|---|---|---|
| HU-01 | Iniciar sesión | ✅ Completa |
| HU-02 | Recuperar contraseña | ✅ Completa |
| HU-03 | Gestionar roles y permisos | ✅ Completa |

### Módulo: Matrícula
| HU | Descripción | Estado |
|---|---|---|
| HU-04 | Registrar matrícula | ✅ Completa |
| HU-05 | Registrar apoderado | ✅ Completa |
| HU-06 | Consultar y editar matrícula | ✅ Completa |

### Módulo: Notas
| HU | Descripción | Estado |
|---|---|---|
| HU-07 | Registrar notas por curso y periodo | ✅ Completa |
| HU-08 | Calcular automáticamente el promedio | ⏳ Pendiente |
| HU-09 | Consultar notas como padre de familia | ⏳ Pendiente |

### Módulo: Asistencia
| HU | Descripción | Estado |
|---|---|---|
| HU-10 | Registrar asistencia diaria | 🔶 Backend completo, frontend en construcción |
| HU-11 | Reportes de inasistencias | ⏳ Pendiente |
| HU-12 | Consultar asistencia como padre de familia | ⏳ Pendiente |

## Arquitectura de software

El sistema sigue una arquitectura **cliente-servidor desacoplada**: este backend expone una **API REST** que no sabe nada de cómo se ve la interfaz — cualquier cliente (el frontend Angular de este proyecto, el de otro integrante, una futura app móvil) puede consumirla de la misma forma, siempre que hable el mismo contrato JSON.

**Patrón de capas** (dentro del backend): `Controller → Service → Repository → Entity`, con `DTO`s como frontera entre lo que entra/sale por HTTP y lo que vive en base de datos — nunca se expone una `Entity` de JPA directamente en una respuesta.

**Autenticación stateless con JWT**: el servidor no guarda sesiones. Cada petición trae su propio token, que un filtro (`JwtAuthFilter`) valida antes de llegar a cualquier controller protegido. Esto permite que el backend escale horizontalmente sin depender de memoria compartida entre instancias.

**Autorización basada en roles y permisos (RBAC)**: cada endpoint declara con `@PreAuthorize` qué rol o permiso necesita, evaluado a partir de lo que ya viene codificado en el JWT (rol + lista de permisos), sin consultar la base de datos en cada request.

**Persistencia**: PostgreSQL + Hibernate/JPA, con generación automática de esquema (`ddl-auto=update`) y un `data.sql` mínimo e idempotente para los datos que el sistema necesita desde el primer arranque.

**Topología de despliegue**: backend en Railway (con su base de datos PostgreSQL en el mismo proveedor); el frontend puede desplegarse de forma independiente en cualquier hosting de archivos estáticos (ej. Vercel), apuntando a la URL pública del backend vía variable de entorno.

## Arquitectura y estructura de paquetes

El backend sigue una arquitectura en capas clásica de Spring Boot:

```
src/main/java/upc/colegioossbackend01/
├── config/          → Seguridad, JPA Auditing, Mail, OpenAPI/Swagger
├── controller/       → Reciben HTTP, delegan al service, no tienen lógica de negocio
├── dto/
│   ├── request/       → Lo que entra en cada endpoint
│   └── response/       → Lo que se devuelve (nunca se expone la entity directamente)
├── entity/           → Las clases JPA que representan las tablas
├── enums/            → Tipos cerrados reutilizados entre entidades (Nivel, EstadoMatricula, etc.)
├── exception/         → GlobalExceptionHandler + excepciones propias (BusinessException, ResourceNotFoundException)
├── mapper/           → Convierten entity ↔ DTO
├── repository/        → Interfaces de Spring Data JPA
├── security/          → JwtService, JwtAuthFilter
└── service/
    ├── (interfaces)
    └── impl/          → La lógica de negocio real vive aquí
```

**Flujo típico de una petición**: `Controller` recibe el request → valida con Bean Validation (`@Valid`) → llama al `Service` → el `Service` aplica las reglas de negocio y usa el `Repository` para leer/escribir en BD → el `Mapper` convierte la `Entity` a `Response` → el `Controller` la envuelve en `ControllerResponse` y la devuelve.

### Cómo se relacionan las entidades del módulo académico

Esta parte no es evidente solo mirando la lista de endpoints, así que vale la pena un diagrama:

```
Curso (catálogo global, ej. "Matemática")
  │
  └──> AulaCurso (el curso SE DICTA en una Aula específica, con horas)
          │
          ├──> DocenteCurso (QUIÉN lo enseña — apunta a Usuario con rol DOCENTE, sin tabla Docente aparte)
          │
          ├──> CursoEvaluacion (QUÉ se evalúa: bimestre, tipo, % — para Notas)
          │       └──> Nota (la calificación real de cada estudiante)
          │
          └──> Clase (sesión dictada en una fecha — para Asistencia)
                  └──> Asistencia (el estado real de cada estudiante en esa clase)
```

`Aula` guarda `periodo` (Integer), `nivel` (mismo enum que usa `Matricula`) y `grado` (Integer) como campos simples — **no** como catálogos con tabla propia, para mantener consistencia con cómo ya se modeló `Matricula` (decisión tomada para no migrar código ya funcionando).

Como no hay una FK directa entre `Matricula` y `Aula` (cada una guarda sus propios `periodo/nivel/grado/seccion`), los consolidados (de notas y de asistencia) resuelven "qué estudiantes están en esta aula" comparando esos 4 campos entre ambas tablas.

## Reglas de negocio del módulo académico

- **Un docente no tiene tabla propia** — es un `Usuario` con `rol = DOCENTE`. Cualquier relación que necesite "el docente de X" apunta directo a `usuario_id`.
- **Registrar nota/asistencia es "crear o corregir"**: si ya existe un registro para esa combinación (estudiante + componente, o estudiante + clase), se actualiza en vez de duplicar — evita que un docente cree registros repetidos por error.
- **La suma de porcentajes de `CursoEvaluacion`** para un mismo curso-aula-bimestre no puede superar 100% — se valida tanto al crear como al editar (excluyendo el propio registro que se edita).
- **El "consolidado"** (de notas y de asistencia) no es una tabla que se guarda — es una consulta calculada en el momento, que combina "quién está matriculado ahora" con "qué se ha registrado hasta ahora". Nada se pre-genera al matricular.

## Pendientes conocidos

- No existe endpoint para que un admin cree cuentas `ESTUDIANTE`/`APODERADO` directamente — solo vía auto-registro (decisión consciente, ver historial de diseño)
- No hay validación de duplicados entre "estudiante matriculado sin cuenta" y "estudiante que se auto-registra después" — caso borde identificado, no implementado (no lo pide ninguna HU)
- HU-08, HU-09, HU-11, HU-12 — pendientes
- Pruebas unitarias del módulo académico, Notas y Asistencia — pendientes (todo lo construido en esta fase se probó manualmente vía Swagger y frontend, no tiene tests automatizados todavía)

## Notas técnicas relevantes

- **`data.sql` se mantiene mínimo a propósito**: solo contiene inserts sobre columnas con restricción `UNIQUE` real (`permiso.codigo`, `rol.nombre`, `usuario.username`). En su momento, insertar datos de prueba en tablas sin esa restricción (`estudiante_apoderado`, `matricula`) causó duplicados masivos en cada reinicio del backend, porque `ON CONFLICT DO NOTHING` no tiene nada contra qué comparar sin una constraint única. La lección quedó aplicada también a las tablas nuevas del módulo académico: ninguna tiene datos semilla en `data.sql`, se crean todas navegando el sistema.
- Relaciones `LAZY` con `JOIN FETCH` donde se necesita evitar `LazyInitializationException`.
- Soft delete en todo — nada se borra físicamente.

## Despliegue en producción (Railway)

Backend desplegado y funcionando. Java 21 coincide con el default de Railpack, sin ajustes adicionales. Swagger deshabilitado en producción.

## Frontend

Existe un frontend Angular propio (`colegio-oss-frontend-v2`). Cubre HU-01 a HU-07 completas, más el módulo académico de administración (Cursos, Aulas, asignaciones). Ver README propio del frontend para más detalle.

## Pruebas automatizadas

```bash
mvn test
```

Más de 100 pruebas unitarias en total. Los archivos más relevantes:

- **`AuthServiceImplTest`** — login, refresh, registro (incluyendo la creación automática de Estudiante/Apoderado según el rol), recuperación de contraseña
- **`MatricularServiceImplTest`** — el flujo transaccional completo de matricular (resolución de estudiante/apoderado nuevo o existente, validación de "una matrícula activa por periodo")
- **`MatriculaServiceImplTest`** — consultar, editar, retirar y trasladar matrículas
- **`UsuarioServiceImplTest`** — listar, aprobar pendientes, cambiar rol, cambiar estado
- **`RolServiceImplTest`** / **`PermisoServiceImplTest`** — CRUD y asignación de permisos a roles
- **`EstudianteServiceImplTest`** / **`ApoderadoServiceImplTest`** / **`EstudianteApoderadoServiceImplTest`** — CRUD y gestión de la relación entre ambos
- **`AuthControllerIntegrationTest`** — pruebas de integración de extremo a extremo con H2 en memoria

El módulo académico, Notas y Asistencia todavía no tienen pruebas automatizadas — se probaron manualmente vía Swagger y el frontend.

## Antes de subir cambios a Git

Revisa que `application.properties` no tenga contraseñas reales, y que `.env` esté en `.gitignore`.