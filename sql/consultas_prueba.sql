-- Consultas para revisar la base en phpMyAdmin (http://localhost/phpmyadmin)
-- Primero ejecuta la API una vez: ella crea la base bd_activacorp, las tablas y los datos de prueba.
USE bd_activacorp;

-- 1. Cantidad de registros por tabla
SELECT 'usuarios' AS tabla, COUNT(*) AS total FROM usuarios
UNION ALL SELECT 'organizaciones', COUNT(*) FROM organizaciones
UNION ALL SELECT 'contactos', COUNT(*) FROM contactos
UNION ALL SELECT 'oportunidades', COUNT(*) FROM oportunidades
UNION ALL SELECT 'seguimientos', COUNT(*) FROM seguimientos;

-- 2. Oportunidades con su organización y responsable (equivale a OportunidadConDatos de la app)
SELECT o.id_oportunidad, o.nombre, o.fecha_evento, o.prioridad, o.estado,
       org.nombre AS organizacion,
       u.nombre   AS responsable
FROM oportunidades o
INNER JOIN organizaciones org ON org.id_organizacion = o.id_organizacion
LEFT JOIN usuarios u ON u.id_usuario = o.id_responsable
ORDER BY o.fecha_evento;

-- 3. Oportunidades sin responsable (las que puede asignar el supervisor)
SELECT id_oportunidad, nombre, estado FROM oportunidades WHERE id_responsable IS NULL;

-- 4. Organizaciones con la cantidad de contactos (equivale a OrganizacionResumen)
SELECT org.id_organizacion, org.nombre, org.tipo, org.comuna, COUNT(c.id_contacto) AS total_contactos
FROM organizaciones org
LEFT JOIN contactos c ON c.id_organizacion = org.id_organizacion
GROUP BY org.id_organizacion, org.nombre, org.tipo, org.comuna
ORDER BY org.nombre;

-- 5. Historial de seguimientos (equivale a SeguimientoDetalle)
SELECT s.id_seguimiento, s.id_oportunidad, s.fecha, s.accion, s.descripcion,
       s.estado_anterior, s.estado_nuevo, u.nombre AS usuario
FROM seguimientos s
INNER JOIN usuarios u ON u.id_usuario = s.id_usuario
ORDER BY s.id_seguimiento DESC;

-- 6. Oportunidades por estado
SELECT estado, COUNT(*) AS total FROM oportunidades GROUP BY estado;
