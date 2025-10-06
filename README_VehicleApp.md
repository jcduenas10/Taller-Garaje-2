# VehicleApp (Servlet + EJB/Jakarta + JDBC)

Aplicación web simple para gestión de vehículos con arquitectura en capas:
**JSP (vista) → Servlet (controlador) → Facade (reglas de negocio) → DAO (persistencia) → MySQL**.

## Arquitectura y capas
- **Vista (JSP)**: `vehicles.jsp` (lista), `formVehiculo.jsp` (crear/editar), `index.jsp`.
- **Controlador (Servlet)**: `VehiculoServlet` orquesta acciones y maneja errores.
- **Fachada (EJB Stateless)**: `VehiculoFacade` aplica validaciones/reglas de negocio.
- **DAO (JDBC)**: `VehiculoDAO` hace CRUD con `PreparedStatement`.
- **Modelo**: `Vehiculo`.

## Reglas de negocio (en Facade)
1. Placa no duplicada.  
2. Propietario con **≥ 5** caracteres.  
3. Marca, modelo y placa con **≥ 3** caracteres.  
4. Color ∈ {Rojo, Blanco, Negro, Azul, Gris}.  
5. Antigüedad ≤ 20 años (modelo como año).  
6. No eliminar si `propietario == "Administrador"`.  
7. Solo actualizar si el vehículo existe.  
8. Validación “anti-injection” simple (además de PreparedStatement).  
9. Notificación en log si `marca == "Ferrari"`.

## Convenciones de nombres
- **Paquetes**: `com.uts.taller1.vehicleapp.[controller|facade|persistence|model]`
- **Clases**: PascalCase (`VehiculoServlet`, `VehiculoFacade`, `VehiculoDAO`).
- **Métodos y campos**: camelCase (`buscarPorId`, `existePlaca`).
- **Vistas**: snake/flat (`vehicles.jsp`, `formVehiculo.jsp`).
- **Rutas**: `/vehicles` con `action=[edit|delete]` e `id` como query param.

## Requisitos
- Java 21+ (o el JDK que estés usando).
- Maven 3.8+.
- Eclipse GlassFish 7.x (o Payara) corriendo en `:8080` y admin en `:4848`.
- MySQL 8.x.

## Base de datos
```sql
CREATE DATABASE garageDB;
USE garageDB;

CREATE TABLE vehiculos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  placa VARCHAR(20) NOT NULL UNIQUE,
  marca VARCHAR(30) NOT NULL,
  modelo VARCHAR(30) NOT NULL,
  color VARCHAR(20) NOT NULL,
  propietario VARCHAR(50) NOT NULL
);
```

Datos de prueba:
```sql
INSERT INTO vehiculos (placa, marca, modelo, color, propietario) VALUES
('ABC123','Toyota','2018','Rojo','Juan Perez'),
('DEF321','Nissan','2010','Gris','Administrador'); -- probar regla de no eliminar
```

## Configuración JNDI (GlassFish)
1. Admin Console → **JDBC Connection Pools** → New  
   - Name: `GaragePool`  
   - Resource Type: `javax.sql.DataSource`  
   - Vendor: `MySQL`  
   - Props: `User=root`, `Password=***`, `DatabaseName=garageDB`, `ServerName=localhost`, `PortNumber=3306`  
   - **Ping**: Succeeded
2. **JDBC Resources** → New  
   - JNDI Name: `jdbc/myPool`  
   - Pool: `GaragePool`

La fachada inyecta:
```java
@Resource(lookup = "jdbc/myPool")
private DataSource ds;
```

## Cómo ejecutar
- Inicia GlassFish y asegúrate del recurso JNDI.
- `mvn clean package`
- Desde NetBeans: Run/Deploy en GlassFish (WAR).
- URLs:
  - Lista: `http://localhost:8080/<context>/vehicles`
  - Form: `http://localhost:8080/<context>/formVehiculo.jsp`

> `<context>` suele ser `VehicleApp-1.0-SNAPSHOT` salvo que configures el contexto en el servidor.

## Manejo de errores
- **Reglas de negocio** → se muestran exactamente al usuario (ej: “La placa ya existe…”).
- **Errores de BD / inesperados** → mensaje genérico (“Ocurrió un problema…”).
- El Servlet desempaca `EJBException` para obtener la causa real y decidir qué mostrar.

## Uso de GIT (flujo recomendado)
- Ramas:
  - `main`: estable.
  - `dev`: integración.
  - `feat/<nombre>`: funcionalidades (ej. `feat/validacion-color`).
- Commits:
  - Prefijo: `feat:`, `fix:`, `docs:`, `refactor:`, `test:`.
  - Ejemplo: `feat: validar placa duplicada en VehiculoFacade`
- Pasos típicos:
  ```bash
  git clone <repo>
  git checkout -b feat/algo
  # cambios...
  git add .
  git commit -m "feat: algo"
  git push origin feat/algo
  # MR/PR desde feat/algo → dev → main
  ```
