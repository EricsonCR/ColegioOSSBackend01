-- ==============================
-- Permisos base
-- ==============================
INSERT INTO permiso (codigo, descripcion, activo, fecha_creacion, usuario_crea)
VALUES
    ('USUARIO_CREAR', 'Permite crear usuarios', true, now(), 'system'),
    ('USUARIO_EDITAR', 'Permite editar usuarios', true, now(), 'system'),
    ('USUARIO_ELIMINAR', 'Permite eliminar usuarios', true, now(), 'system'),
    ('USUARIO_VER', 'Permite ver usuarios', true, now(), 'system')
    ON CONFLICT (codigo) DO NOTHING;
-- ==============================
-- Permisos de Matrícula
-- ==============================
INSERT INTO permiso (codigo, descripcion, activo, fecha_creacion, usuario_crea)
VALUES
    ('MATRICULAR', 'Permite registrar matrículas nuevas', true, now(), 'system'),
    ('MATRICULA_VER', 'Permite consultar matrículas, estudiantes y apoderados', true, now(), 'system'),
    ('MATRICULA_EDITAR', 'Permite editar matrícula, estudiante o apoderado existente', true, now(), 'system')
    ON CONFLICT (codigo) DO NOTHING;

-- ==============================
-- Roles base
-- ==============================
INSERT INTO rol (nombre, descripcion, activo, fecha_creacion, usuario_crea)
VALUES
    ('ADMIN', 'Administrador del sistema', true, now(), 'system'),
    ('DOCENTE', 'Docente del colegio', true, now(), 'system'),
    ('ALUMNO', 'Alumno del colegio', true, now(), 'system')
    ON CONFLICT (nombre) DO NOTHING;
-- ==============================
-- Rol ADMINISTRATIVO
-- ==============================
INSERT INTO rol (nombre, descripcion, activo, fecha_creacion, usuario_crea)
VALUES
    ('ADMINISTRATIVO', 'Personal administrativo con permisos de matrícula', true, now(), 'system')
    ON CONFLICT (nombre) DO NOTHING;

-- ==============================
-- Asignación de permisos al rol ADMIN (todos los permisos)
-- ==============================
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id
FROM rol r, permiso p
WHERE r.nombre = 'ADMIN'
    ON CONFLICT DO NOTHING;

-- ==============================
-- Asignación de permisos al rol DOCENTE (solo ver y editar)
-- ==============================
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id
FROM rol r, permiso p
WHERE r.nombre = 'DOCENTE'
  AND p.codigo IN ('USUARIO_VER', 'USUARIO_EDITAR')
    ON CONFLICT DO NOTHING;

-- ==============================
-- Asignación de permisos al rol ALUMNO (solo ver)
-- ==============================
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id
FROM rol r, permiso p
WHERE r.nombre = 'ALUMNO'
  AND p.codigo = 'USUARIO_VER'
    ON CONFLICT DO NOTHING;

-- ==============================
-- Usuario administrador semilla
-- password en texto plano: Admin123!  (ya hasheado con BCrypt abajo)
-- ==============================
INSERT INTO usuario (username, email, password, nombre_completo, estado, rol_id, fecha_creacion, usuario_crea)
SELECT 'admin', 'admin@colegio.edu.pe', '$2b$10$Km/v8ZcTvdslEALHK2qpYOAzuN716B4XeQQw0CZqhvFdzm0IZEQ2S', 'Administrador del Sistema', 'ACTIVO', r.id, now(), 'system'
FROM rol r
WHERE r.nombre = 'ADMIN'
    ON CONFLICT (username) DO NOTHING;

-- ==============================
-- Asignación de permisos al rol ADMINISTRATIVO
-- ==============================
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id
FROM rol r, permiso p
WHERE r.nombre = 'ADMINISTRATIVO'
  AND p.codigo IN ('MATRICULAR', 'MATRICULA_VER', 'MATRICULA_EDITAR')
    ON CONFLICT DO NOTHING;