select * from usuario;
select * from rol;
select * from password_reset_token;
select * from permiso;
select * from rol_permiso;

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
update usuario set username='ericson', email='ericsonupc@gmail.com' where id=26

ALTER TABLE permiso ADD COLUMN IF NOT EXISTS activo BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE rol ADD COLUMN IF NOT EXISTS activo BOOLEAN NOT NULL DEFAULT true;