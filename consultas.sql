select * from usuario;
select * from rol;
select * from password_reset_token;
select * from permiso;
select * from rol_permiso;
SELECT * FROM estudiante_apoderado;

ALTER TABLE usuario ALTER COLUMN rol_id DROP NOT NULL;
ALTER TABLE usuario DROP CONSTRAINT usuario_estado_check;

ALTER TABLE usuario ADD CONSTRAINT usuario_estado_check
    CHECK (estado IN ('ACTIVO', 'INACTIVO', 'BLOQUEADO', 'PENDIENTE'));

-- Paso 1: agregar la columna, nullable temporalmente
ALTER TABLE usuario ADD COLUMN email VARCHAR(150);

-- Paso 2: asignar un email temporal único a los usuarios existentes que no lo tienen
-- (usamos el id para garantizar unicidad)
UPDATE usuario SET email = username || '_' || id || '@temporal.com' WHERE email IS NULL;

-- Paso 3: ahora sí, aplicar las restricciones NOT NULL y UNIQUE
ALTER TABLE usuario ALTER COLUMN email SET NOT NULL;
ALTER TABLE usuario ADD CONSTRAINT usuario_email_key UNIQUE (email);

UPDATE usuario SET email = 'admin@colegio.edu.pe' WHERE username = 'admin';
update usuario set username='ericson', email='ericsonupc@gmail.com' where id=26;

ALTER TABLE permiso ADD COLUMN IF NOT EXISTS activo BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE rol ADD COLUMN IF NOT EXISTS activo BOOLEAN NOT NULL DEFAULT true;

SELECT * FROM permiso WHERE codigo LIKE 'MATRICULA%';
SELECT * FROM rol WHERE nombre = 'ADMINISTRATIVO';
SELECT r.nombre, p.codigo FROM rol_permiso rp
                                   JOIN rol r ON rp.rol_id = r.id
                                   JOIN permiso p ON rp.permiso_id = p.id
WHERE r.nombre IN ('ADMIN', 'ADMINISTRATIVO');

select r.nombre,p.codigo, u.email, u.username, u.estado from rol_permiso rp
    inner join permiso p on p.id = rp.permiso_id
    inner join rol r on r.id = rp.rol_id
    inner join usuario u on u.rol_id = r.id
where p.codigo like  '%MATRICULA%';

