# ActivaCorp API

API REST de ActivaCorp (gestión de oportunidades comerciales), construida con la misma estructura de la API de clases:
**Spring Boot 4**, **Java 21**, **Spring Data JPA**, **MySQL (XAMPP)**, **Lombok** y **Maven**.

Es el equivalente en servidor de la base Room de la app Android: las mismas 5 tablas, los mismos roles y las mismas reglas de negocio.

## Estructura

```
cl.duoc.activacorp
├── config/        DataInitializer (datos de prueba, igual que DatosIniciales.kt)
├── controller/    Usuario, Organizacion, Contacto, Oportunidad
├── dto/           Cuerpos de las peticiones (login, estado, asignación, etc.)
├── model/         Entidades JPA y enums (Rol, Prioridad, EstadoOportunidad)
├── repository/    Interfaces JpaRepository
├── service/       Lógica de negocio y validaciones
└── util/          Permisos (misma máquina de estados de Permisos.kt)
```

## Probar la base de datos en XAMPP

1. Abre el **XAMPP Control Panel** y presiona **Start** en **MySQL**. Para usar phpMyAdmin inicia también **Apache**.
2. No necesitas crear la base: la URL incluye `createDatabaseIfNotExist=true`, así que `bd_activacorp` se crea sola.
3. Inicia la API:
   - Desde el IDE: ejecuta `ActivacorpApplication`, o
   - Desde la terminal: `mvnw.cmd spring-boot:run` (Windows) o `./mvnw spring-boot:run` (Linux/Mac).
4. Abre `http://localhost/phpmyadmin` y entra a la base `bd_activacorp`. Deberías ver las tablas `usuarios`, `organizaciones`, `contactos`, `oportunidades` y `seguimientos` con datos.
5. En la pestaña **SQL** de phpMyAdmin puedes ejecutar las consultas de `sql/consultas_prueba.sql`.
6. Prueba los endpoints con `pruebas.http` (IntelliJ o la extensión REST Client de VS Code), Postman o el navegador (`http://localhost:8082/oportunidades?usuarioId=4`).

La conexión usa `root` sin contraseña (valores por defecto de XAMPP) en el puerto `3306`. Si cambiaste algo, edita `src/main/resources/application.properties`.

Para empezar de cero: en phpMyAdmin elimina la base `bd_activacorp` y vuelve a iniciar la API.

## Usuarios de prueba

| Id | Nombre | Correo | Clave | Rol |
|---|---|---|---|---|
| 1 | Camila Rojas | camila.rojas@agencia.cl | ejecutivo123 | EJECUTIVO |
| 2 | Diego Soto | diego.soto@agencia.cl | ejecutivo123 | EJECUTIVO |
| 3 | Valentina Pérez | valentina.perez@agencia.cl | ejecutivo123 | EJECUTIVO |
| 4 | Marcos Fuentes | marcos.fuentes@agencia.cl | supervisor123 | SUPERVISOR |

## Endpoints

La app no maneja tokens: igual que la clase `Sesion` de la app, cada petición que modifica datos envía el `usuarioId` de quien la realiza.

| Método | Endpoint | Descripción | Respuestas |
|---|---|---|---|
| POST | `/usuarios/login` | Inicia sesión (`correo`, `clave`) | 200 / 401 |
| GET | `/usuarios` | Lista usuarios (sin clave) | 200 |
| GET | `/usuarios/ejecutivos` | Lista solo ejecutivos | 200 |
| GET | `/usuarios/{id}` | Usuario por id | 200 / 404 |
| GET | `/organizaciones` | Organizaciones con `totalContactos` | 200 |
| GET | `/organizaciones/{id}` | Organización por id | 200 / 404 |
| POST | `/organizaciones` | Crea organización | 201 / 400 / 409 |
| GET | `/organizaciones/{id}/contactos` | Contactos de la organización | 200 / 404 |
| POST | `/organizaciones/{id}/contactos` | Crea contacto | 201 / 400 / 404 / 409 |
| GET | `/oportunidades?usuarioId=` | Supervisor: todas. Ejecutivo: las suyas | 200 |
| GET | `/oportunidades/{id}` | Detalle | 200 / 404 |
| GET | `/oportunidades/{id}/seguimientos` | Historial (el más reciente primero) | 200 / 404 |
| POST | `/oportunidades` | Registra (solo ejecutivos) | 201 / 400 / 403 |
| PUT | `/oportunidades/{id}` | Edita datos y prioridad | 200 / 400 / 403 |
| PUT | `/oportunidades/{id}/estado` | Cambia estado (valida rol y transición) | 200 / 403 / 409 |
| PUT | `/oportunidades/{id}/asignacion` | Asigna o reasigna (solo supervisor) | 200 / 403 / 409 |

Los errores devuelven un JSON con el campo `message` explicando el motivo.

### Reglas de negocio (iguales a las de la app)

- Solo los ejecutivos registran oportunidades; quien la crea queda como responsable.
- El ejecutivo avanza un estado a la vez y solo en lo que tiene asignado; no puede confirmar ni descartar.
- El supervisor puede confirmar (solo desde `EN_ESPERA_DE_RESPUESTA`) o descartar.
- Solo el supervisor asigna o reasigna, y no sobre oportunidades cerradas.
- Cada registro, edición, cambio de estado y asignación genera un seguimiento.

## Pruebas unitarias

`mvnw.cmd test` ejecuta `PermisosTest`, que valida la máquina de estados sin necesitar la base de datos.

## Si la conexión falla

- `Communications link failure`: MySQL no está iniciado en XAMPP, o el puerto 3306 está ocupado por otro servicio.
- `Access denied for user 'root'`: tu root tiene contraseña; ponla en `spring.datasource.password`.
