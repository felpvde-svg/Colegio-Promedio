#  Implementación del Módulo de Gestión de Promedios

##  Descripción General

Este documento detalla la implementación completa del módulo de **Gestión de Promedios** desarrollado para el sistema académico del Colegio Quipux. El proyecto abarca tanto el desarrollo del backend (Java) como del frontend (Angular), implementando un CRUD completo con cálculo automático de promedios académicos.

---

## 🔧 BACKEND - Implementación Java

### 1. Modelo de Datos: `Promedio.java`

**Ubicación:** `co.com.model.Promedio`

#### Atributos de la Clase

```java
public class Promedio {
    private int idPromedio;           // ID autogenerado
    private int idEstudiante;         // FK a tabla Estudiantes
    private int idMateria;            // FK a tabla Materias
    private double nota1;             // Primera nota
    private double nota2;             // Segunda nota
    private double nota3;             // Tercera nota
    private double promedio;          // Promedio calculado
    
    // Campos descriptivos (resultado del JOIN)
    private String nombreEstudiante;
    private String nombreMateria;
}
```

#### Constructores Implementados

**1. Constructor Vacío:**
```java
public Promedio() {}
```
- Necesario para la instanciación dinámica en el Manager

**2. Constructor Completo (con IDs):**
```java
public Promedio(int idPromedio, int idEstudiante, int idMateria,
                double nota1, double nota2, double nota3, double promedio)
```
- Usado al recuperar datos de la base de datos
- Inicializa todos los campos principales

**3. Constructor para JOIN (con nombres descriptivos):**
```java
public Promedio(int idPromedio, String nombreEstudiante, String nombreMateria,
                double nota1, double nota2, double nota3, double promedio)
```
- **Propósito:** Mapear resultados de consultas SQL con INNER JOIN
- Incluye nombres legibles en lugar de IDs para mostrar en el frontend
- Este es el constructor usado por `PromediosManager.getPromedios()`

#### Métodos Getter y Setter

Se implementaron todos los métodos de acceso para cada atributo:
- `getIdPromedio()` / `setIdPromedio(int)`
- `getIdEstudiante()` / `setIdEstudiante(int)`
- `getIdMateria()` / `setIdMateria(int)`
- `getNota1()`, `getNota2()`, `getNota3()` y sus setters
- `getPromedio()` / `setPromedio(double)`
- `getNombreEstudiante()` / `setNombreEstudiante(String)`
- `getNombreMateria()` / `setNombreMateria(String)`

---

### 2. Capa de Lógica de Negocio: `PromediosManager.java`

**Ubicación:** `co.com.manager.PromediosManager`

Esta clase implementa toda la lógica CRUD y el cálculo de promedios.

#### Método 1: `getPromedios()`

**Propósito:** Obtener lista completa de promedios con información descriptiva

```java
public List<Promedio> getPromedios() throws SQLException
```

**Consulta SQL Implementada:**
```sql
SELECT 
    p.idPromedio,
    e.nombre AS nombreEstudiante,
    m.nombre AS nombreMateria,
    p.nota1,
    p.nota2,
    p.nota3,
    p.promedio
FROM Promedios p
INNER JOIN Estudiantes e ON p.idEstudiante = e.idEstudiante
INNER JOIN Materias m ON p.idMateria = m.idMateria
```

**Proceso:**
1. Establece conexión a BD mediante `BaseDatos.getConnection()`
2. Prepara y ejecuta la consulta SQL
3. Itera sobre el `ResultSet`
4. Por cada fila, crea un objeto `Promedio` usando el constructor con nombres
5. Agrega cada objeto a una `ArrayList<Promedio>`
6. Retorna la lista completa

**Retorno:** `List<Promedio>` - Lista con todos los promedios y nombres descriptivos

---

#### Método 2: `insertPromedio(Promedio p)`

**Propósito:** Insertar un nuevo promedio calculándolo automáticamente

```java
public int insertPromedio(Promedio p) throws SQLException
```

**SQL Implementado:**
```sql
INSERT INTO Promedios (idEstudiante, idMateria, nota1, nota2, nota3, promedio)
VALUES (?, ?, ?, ?, ?, ?)
```

**Proceso Detallado:**

1. **Conexión:** Obtiene conexión con `BaseDatos.getConnection()`

2. **Preparación del Statement:**
   ```java
   PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
   ```
   - Usa `RETURN_GENERATED_KEYS` para obtener el ID autogenerado

3. **Cálculo del Promedio:**
   ```java
   double prom = (p.getNota1() + p.getNota2() + p.getNota3()) / 3.0;
   ```
   - Suma las 3 notas y divide entre 3
   - Usa `3.0` (double) para evitar división entera

4. **Asignación de Parámetros:**
   ```java
   ps.setInt(1, p.getIdEstudiante());
   ps.setInt(2, p.getIdMateria());
   ps.setDouble(3, p.getNota1());
   ps.setDouble(4, p.getNota2());
   ps.setDouble(5, p.getNota3());
   ps.setDouble(6, prom);
   ```

5. **Ejecución:** `ps.executeUpdate();`

6. **Obtención del ID Generado:**
   ```java
   ResultSet gk = ps.getGeneratedKeys();
   if (gk.next()) return gk.getInt(1);
   ```

**Retorno:** `int` - El ID del promedio recién insertado, o `-1` si falló

---

#### Método 3: `updatePromedio(Promedio p)`

**Propósito:** Actualizar un promedio existente recalculando el valor

```java
public void updatePromedio(Promedio p) throws SQLException
```

**SQL Implementado:**
```sql
UPDATE Promedios 
SET idEstudiante=?, idMateria=?, nota1=?, nota2=?, nota3=?, promedio=?
WHERE idPromedio=?
```

**Proceso:**

1. **Recálculo del Promedio:**
   ```java
   double prom = (p.getNota1() + p.getNota2() + p.getNota3()) / 3.0;
   ```
   - Mismo algoritmo que en insert

2. **Actualización de Campos:**
   ```java
   ps.setInt(1, p.getIdEstudiante());
   ps.setInt(2, p.getIdMateria());
   ps.setDouble(3, p.getNota1());
   ps.setDouble(4, p.getNota2());
   ps.setDouble(5, p.getNota3());
   ps.setDouble(6, prom);
   ps.setInt(7, p.getIdPromedio());  // WHERE clause
   ```

3. **Ejecución:** `ps.executeUpdate();`

**Nota:** El campo `idPromedio` en la posición 7 es el criterio WHERE para identificar qué registro actualizar.

---

#### Método 4: `deletePromedio(int idPromedio)`

**Propósito:** Eliminar un promedio por su ID

```java
public void deletePromedio(int idPromedio) throws SQLException
```

**SQL Implementado:**
```sql
DELETE FROM Promedios WHERE idPromedio=?
```

**Proceso:**
1. Conecta a la base de datos
2. Prepara el statement con el ID como parámetro
3. Ejecuta la eliminación
4. Cierra recursos

**Parámetro:** `int idPromedio` - ID del registro a eliminar

---

### 3. Capa de Servicios REST: `WsColegio.java`

**Ubicación:** Web Services REST

Esta clase expone los endpoints HTTP para que el frontend Angular pueda consumirlos.

#### Endpoint 1: GET - Listar Promedios

```java
@GET
@Path("getPromedios")
@Produces(MediaType.APPLICATION_JSON)
public Response getPromedios()
```

**Funcionalidad:**
1. Instancia `PromediosManager`
2. Llama a `manager.getPromedios()`
3. Convierte la lista a JSON
4. Retorna `Response.ok(lista).build()`
5. En caso de error, retorna status 500 con mensaje de error

**URL:** `http://localhost:8081/servicesRest/WsColegio/getPromedios`

**Response exitoso:**
```json
[
  {
    "idPromedio": 1,
    "nombreEstudiante": "Juan Pérez",
    "nombreMateria": "Matemáticas",
    "nota1": 4.5,
    "nota2": 4.0,
    "nota3": 4.2,
    "promedio": 4.23
  }
]
```

---

#### Endpoint 2: POST - Insertar Promedio

```java
@POST
@Path("insertPromedios")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response insertPromedios(Promedio p)
```

**Funcionalidad:**
1. Recibe objeto `Promedio` desde JSON del body
2. Llama a `manager.insertPromedio(p)`
3. Obtiene el ID generado
4. Retorna JSON con el nuevo ID: `{"idPromedio": 5}`

**Request Body:**
```json
{
  "idEstudiante": 1,
  "idMateria": 2,
  "nota1": 4.5,
  "nota2": 4.0,
  "nota3": 4.2
}
```

**Nota:** NO se envía `promedio`, se calcula automáticamente en el backend

---

#### Endpoint 3: PUT - Actualizar Promedio

```java
@PUT
@Path("updatePromedios")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response updatePromedios(Promedio p)
```

**Funcionalidad:**
1. Recibe promedio completo con `idPromedio`
2. Recalcula el promedio
3. Actualiza en BD
4. Retorna mensaje de éxito

**Request Body:**
```json
{
  "idPromedio": 5,
  "idEstudiante": 1,
  "idMateria": 2,
  "nota1": 5.0,
  "nota2": 4.5,
  "nota3": 4.8
}
```

---

#### Endpoint 4: DELETE - Eliminar Promedio

```java
@DELETE
@Path("deletePromedios/{id}")
public Response deletePromedios(@PathParam("id") int id)
```

**Funcionalidad:**
1. Recibe ID por URL path parameter
2. Llama a `manager.deletePromedio(id)`
3. Retorna mensaje de confirmación

**URL Ejemplo:** `DELETE /servicesRest/WsColegio/deletePromedios/5`

---

## 🎨 FRONTEND - Implementación Angular

### 1. Estructura de Archivos Frontend

```
frontend/Semillero/src/app/
├── _models/
│   └── promedios.ts               # Interface TypeScript
├── _services/
│   └── promedios.service.ts       # Servicio HTTP
└── components/
    ├── menu-left/
    │   ├── menu-left.component.html  # Menú de navegación
    │   └── menu-left.component.ts
    └── promedios/
        ├── promedios.component.ts      # Lógica del componente
        ├── promedios.component.html    # Vista/Template
        ├── promedios.component.css     # Estilos
        └── promedios.component.spec.ts # Tests unitarios
```

---

### 2. Modelo de Datos: `promedios.ts`

**Ubicación:** `src/app/_models/promedios.ts`

```typescript
export interface Promedios {
  idPromedio?: number;      // Opcional, generado por BD
  idEstudiante: number;     // FK - Requerido
  idMateria: number;        // FK - Requerido
  nota1: number;            // Primera calificación
  nota2: number;            // Segunda calificación
  nota3: number;            // Tercera calificación
  promedio?: number;        // Opcional, calculado en backend
}
```

#### ¿Por qué usar interfaces en TypeScript?

**Ventajas implementadas:**

1. **Type Safety (Seguridad de tipos):**
   ```typescript
   // ✅ CORRECTO - TypeScript valida los tipos
   const promedio: Promedios = {
     idEstudiante: 1,
     idMateria: 2,
     nota1: 4.5,
     nota2: 4.0,
     nota3: 4.2
   };
   
   // ❌ ERROR - TypeScript detecta el problema
   const invalido: Promedios = {
     idEstudiante: "uno",  // Error: debe ser number
     nota1: 4.5
   };
   ```

2. **Autocompletado en IDE:**
   - Al escribir `promedio.`, VSCode sugiere automáticamente todas las propiedades
   - Previene errores de escritura

3. **Documentación implícita:**
   - La interface documenta la estructura esperada
   - Otros desarrolladores entienden qué datos manejar

4. **Campos opcionales (`?`):**
   - `idPromedio?`: No se envía al crear, se recibe del backend
   - `promedio?`: Calculado automáticamente, no lo envía el usuario

---

### 3. Capa de Servicios: `promedios.service.ts`

**Ubicación:** `src/app/_services/promedios.service.ts`

#### Decorador y Configuración

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Promedios } from '../_models/promedios';

@Injectable({
  providedIn: 'root'  // Singleton a nivel de aplicación
})
export class PromediosService {
  private apiUrl = 'http://localhost:8081/servicesRest/WsColegio';

  constructor(private http: HttpClient) {}
```

**Explicación técnica:**

- **`@Injectable({ providedIn: 'root' })`:**
  - Angular crea UNA SOLA instancia del servicio
  - Se comparte entre todos los componentes
  - No necesitas declararlo en `providers`

- **Inyección de dependencias:**
  - `private http: HttpClient` se inyecta automáticamente
  - Angular resuelve la dependencia en tiempo de ejecución

- **Variable privada `apiUrl`:**
  - Centraliza la URL base del API
  - Facilita cambios (desarrollo → producción)

---

#### Método 1: `getPromedios()` - Listar todos

```typescript
getPromedios(): Observable<Promedios[]> {
  return this.http.get<Promedios[]>(`${this.apiUrl}/getPromedios`);
}
```

**Desglose técnico:**

1. **Tipo de retorno:** `Observable<Promedios[]>`
   - `Observable`: Stream reactivo de RxJS
   - `Promedios[]`: Array de objetos tipados
   - Permite suscripción asíncrona

2. **Template string:** `` `${this.apiUrl}/getPromedios` ``
   - Concatena URL base + endpoint
   - Resultado: `http://localhost:8081/servicesRest/WsColegio/getPromedios`

3. **Generic type:** `get<Promedios[]>`
   - TypeScript conoce el tipo de respuesta
   - Autocompleta propiedades del array

4. **Flujo de datos:**
   ```
   Servicio → HTTP GET → Backend → JSON Response → 
   Observable → subscribe() → Componente recibe Promedios[]
   ```

---

#### Método 2: `insertPromedios()` - Crear nuevo

```typescript
insertPromedios(promedio: Promedios): Observable<any> {
  return this.http.post(`${this.apiUrl}/insertPromedios`, promedio);
}
```

**Desglose técnico:**

1. **Parámetro:** `promedio: Promedios`
   - Valida que el objeto tenga la estructura correcta
   - TypeScript verifica campos requeridos

2. **Serialización automática:**
   - `HttpClient` convierte el objeto a JSON
   - Headers `Content-Type: application/json` automáticos

3. **Request Body generado:**
   ```json
   {
     "idEstudiante": 1,
     "idMateria": 2,
     "nota1": 4.5,
     "nota2": 4.0,
     "nota3": 4.2
   }
   ```

4. **Response esperado:**
   ```json
   {
     "idPromedio": 5
   }
   ```

---

#### Método 3: `updatePromedios()` - Actualizar existente

```typescript
updatePromedios(promedio: Promedios): Observable<any> {
  return this.http.put(`${this.apiUrl}/updatePromedios`, promedio);
}
```

**Diferencias con POST:**
- Método HTTP: `PUT` (actualización)
- El objeto `promedio` DEBE incluir `idPromedio`
- Backend usa el ID para identificar qué registro modificar

---

#### Método 4: `deletePromedios()` - Eliminar por ID

```typescript
deletePromedios(id: number): Observable<any> {
  return this.http.delete(`${this.apiUrl}/deletePromedios/${id}`);
}
```

**Características:**

1. **Path parameter:** `/${id}`
   - El ID va en la URL, no en el body
   - Ejemplo: `DELETE /deletePromedios/5`

2. **Sin body:**
   - Los DELETE requests no llevan cuerpo
   - Solo necesita el ID para identificar el registro

3. **Retorno:** `Observable<any>`
   - Backend devuelve mensaje de confirmación
   - No necesita tipo específico

---

### 4. Componente Principal: `promedios.component.ts`

**Ubicación:** `src/app/components/promedios/promedios.component.ts`

#### Decorador y Metadatos

```typescript
import { Component, OnInit } from '@angular/core';
import { Promedios } from 'src/app/_models/promedios';
import { PromediosService } from 'src/app/_services/promedios.service';

@Component({
  selector: 'app-promedios',
  templateUrl: './promedios.component.html',
  styleUrls: ['./promedios.component.css']
})
export class PromediosComponent implements OnInit {
```

**Explicación del decorador:**

- **`selector: 'app-promedios'`:**
  - Nombre del tag HTML: `<app-promedios></app-promedios>`
  - Se usa en el routing o en otros componentes

- **`templateUrl`:**
  - Ruta al archivo HTML del template
  - Separa la vista de la lógica (buena práctica)

- **`styleUrls`:**
  - Estilos CSS encapsulados solo para este componente
  - No afectan otros componentes (CSS scoping)

- **`implements OnInit`:**
  - Contrato que obliga a implementar `ngOnInit()`
  - Hook del ciclo de vida de Angular

---

#### Propiedades del Componente

```typescript
promedios: Promedios[] = [];

nuevoPromedio: Promedios = { 
  idEstudiante: 0, 
  idMateria: 0, 
  nota1: 0, 
  nota2: 0, 
  nota3: 0 
};

editando: boolean = false;

constructor(private promediosService: PromediosService) {}
```

**Análisis de cada propiedad:**

1. **`promedios: Promedios[] = []`:**
   - Array que almacena la lista completa
   - Inicializado vacío
   - Se llena con datos del backend en `ngOnInit()`
   - Angular re-renderiza la tabla cuando cambia

2. **`nuevoPromedio: Promedios`:**
   - Modelo del formulario (two-way binding)
   - Representa el promedio en edición/creación
   - Se resetea después de guardar

3. **`editando: boolean = false`:**
   - Flag que controla el modo del formulario
   - `false`: Modo "Agregar" → POST
   - `true`: Modo "Actualizar" → PUT
   - Cambia el texto del botón dinámicamente

4. **Constructor con DI:**
   - Inyecta `PromediosService` automáticamente
   - `private` crea la propiedad y la asigna
   - No se ejecuta lógica de negocio aquí

---

#### Hook de Inicialización

```typescript
ngOnInit(): void {
  this.listarPromedios();
}
```

**Ciclo de vida de Angular:**

```
1. Constructor ejecutado
   ↓
2. ngOnInit() ejecutado (una sola vez)
   ↓
3. this.listarPromedios() llamado
   ↓
4. Componente renderizado con datos
```

**¿Por qué en `ngOnInit()` y no en el constructor?**
- El constructor es para inicialización simple
- `ngOnInit()` garantiza que el componente está listo
- Las propiedades `@Input` ya están disponibles

---

#### Método: `listarPromedios()`

```typescript
listarPromedios() {
  this.promediosService.getPromedios().subscribe(data => {
    this.promedios = data;
  });
}
```

**Flujo detallado:**

```
1. Llamada al servicio
   this.promediosService.getPromedios()
   ↓
2. Servicio hace HTTP GET
   Observable<Promedios[]> creado
   ↓
3. Backend procesa y responde
   JSON array enviado
   ↓
4. Observable emite los datos
   subscribe() captura la emisión
   ↓
5. Callback ejecutado
   data => { this.promedios = data; }
   ↓
6. Angular detecta cambio (Change Detection)
   ↓
7. Template se re-renderiza
   Tabla actualizada automáticamente
```

**Concepto clave: Programación reactiva**
- No esperamos la respuesta bloqueando el código
- El subscribe "escucha" cuando lleguen los datos
- La UI permanece responsive durante la petición

---

#### Método: `guardarPromedios()`

```typescript
guardarPromedios() {
  if (this.editando) {
    this.promediosService.updatePromedios(this.nuevoPromedio).subscribe(() => {
      this.cancelarEdicion();
      this.listarPromedios();
    });
  } else {
    this.promediosService.insertPromedios(this.nuevoPromedio).subscribe(() => {
      this.nuevoPromedio = { idEstudiante: 0, idMateria: 0, nota1: 0, nota2: 0, nota3: 0 };
      this.listarPromedios();
    });
  }
}
```

**Lógica de decisión:**

**Caso 1: Modo EDICIÓN (`editando === true`)**
```
1. Usuario editó un promedio existente
2. this.nuevoPromedio contiene el idPromedio
3. Llamada: updatePromedios(this.nuevoPromedio)
4. Backend ejecuta UPDATE SQL
5. Callback éxito:
   a. cancelarEdicion() → editando = false, resetea formulario
   b. listarPromedios() → recarga tabla actualizada
```

**Caso 2: Modo CREACIÓN (`editando === false`)**
```
1. Usuario llenó formulario para nuevo promedio
2. this.nuevoPromedio NO tiene idPromedio
3. Llamada: insertPromedios(this.nuevoPromedio)
4. Backend ejecuta INSERT SQL, calcula promedio
5. Callback éxito:
   a. Resetea nuevoPromedio a valores iniciales
   b. listarPromedios() → recarga tabla con nuevo registro
```

**Patrón implementado: Optimistic UI**
- No validamos si falló (simplificación)
- En producción, agregar manejo de errores:
  ```typescript
  .subscribe({
    next: () => { /* éxito */ },
    error: (err) => alert('Error: ' + err.message)
  });
  ```

---

#### Método: `editarPromedios()`

```typescript
editarPromedios(p: Promedios) {
  this.editando = true;
  this.nuevoPromedio = { ...p };
}
```

**Análisis línea por línea:**

1. **Activar modo edición:**
   ```typescript
   this.editando = true;
   ```
   - El botón cambia de "Agregar" a "Actualizar"
   - Aparece botón "Cancelar"

2. **Copiar objeto con spread operator:**
   ```typescript
   this.nuevoPromedio = { ...p };
   ```
   
   **¿Por qué spread operator `{...p}`?**
   
   **❌ INCORRECTO:**
   ```typescript
   this.nuevoPromedio = p;  // Referencia al mismo objeto
   ```
   - Modificar el formulario alteraría directamente la tabla
   - Cambios visibles antes de guardar
   
   **✅ CORRECTO:**
   ```typescript
   this.nuevoPromedio = { ...p };  // Copia superficial
   ```
   - Crea un nuevo objeto independiente
   - Cambios en formulario no afectan la tabla
   - Permite cancelar sin efectos secundarios

3. **Resultado visual:**
   - Los inputs se llenan con los valores existentes
   - Usuario puede modificar y guardar cambios
   - O cancelar y descartar ediciones

---

#### Método: `eliminarPromedios()`

```typescript
eliminarPromedios(id: number | undefined) {
  if (id) {
    this.promediosService.deletePromedios(id).subscribe(() => {
      this.listarPromedios();
    });
  }
}
```

**Validación de seguridad:**

1. **`id: number | undefined`:**
   - TypeScript permite que el ID sea undefined
   - Coincide con `idPromedio?` del modelo

2. **Guardia `if (id)`:**
   ```typescript
   if (id) { // Solo ejecuta si id tiene valor
   ```
   - Previene llamadas al backend con `undefined`
   - Evita errores 404 o 400

3. **Flujo de eliminación:**
   ```
   Usuario click "Eliminar"
   ↓
   eliminarPromedios(5) ejecutado
   ↓
   Servicio: DELETE /deletePromedios/5
   ↓
   Backend elimina registro
   ↓
   Callback: listarPromedios()
   ↓
   Tabla actualizada sin el registro
   ```

**Mejora pendiente (buena práctica):**
```typescript
eliminarPromedios(id: number | undefined) {
  if (id && confirm('¿Eliminar este promedio?')) {
    this.promediosService.deletePromedios(id).subscribe(() => {
      this.listarPromedios();
    });
  }
}
```

---

### 5. Template HTML: `promedios.component.html`

#### Estructura General

```html
<div class="container mt-4">
  <h2 class="mb-3">Gestión de Promedios</h2>
  
  <!-- SECCIÓN 1: Formulario -->
  <form (ngSubmit)="guardarPromedios()" class="mb-4">
    ...
  </form>
  
  <!-- SECCIÓN 2: Tabla de datos -->
  <table class="table table-striped">
    ...
  </table>
</div>
```

---

#### Sección 1: Formulario con Data Binding

```html
<form (ngSubmit)="guardarPromedios()" class="mb-4">
  <div class="row g-2">
    <div class="col">
      <input type="number" 
             [(ngModel)]="nuevoPromedio.idEstudiante" 
             name="idEstudiante"
             class="form-control"
             placeholder="ID Estudiante" />
    </div>
    <div class="col">
      <input type="number" 
             [(ngModel)]="nuevoPromedio.idMateria" 
             name="idMateria"
             class="form-control"
             placeholder="ID Materia" />
    </div>
    <!-- Inputs para nota1, nota2, nota3 -->
    <div class="col">
      <button class="btn btn-primary" type="submit">
        {{ editando ? 'Actualizar' : 'Agregar' }}
      </button>
      <button *ngIf="editando" 
              class="btn btn-secondary ms-2" 
              type="button"
              (click)="cancelarEdicion()">
        Cancelar
      </button>
    </div>
  </div>
</form>
```

**Conceptos Angular implementados:**

1. **Event Binding:** `(ngSubmit)="guardarPromedios()"`
   ```
   Evento HTML    Angular Expression
      ↓                  ↓
   (ngSubmit) = "guardarPromedios()"
   ```
   - Se ejecuta al presionar Enter o click en submit
   - Previene el comportamiento default del form

2. **Two-Way Data Binding:** `[(ngModel)]`
   ```typescript
   [(ngModel)]="nuevoPromedio.idEstudiante"
   ```
   
   **Equivalente a:**
   ```html
   [value]="nuevoPromedio.idEstudiante"
   (input)="nuevoPromedio.idEstudiante = $event.target.value"
   ```
   
   **Flujo de datos bidireccional:**
   ```
   Component → View (inicial)
   ↓
   Usuario escribe en input
   ↓
   View → Component (actualización automática)
   ↓
   this.nuevoPromedio.idEstudiante cambia en tiempo real
   ```

3. **Atributo `name` requerido:**
   ```html
   name="idEstudiante"
   ```
   - Necesario para ngModel en formularios
   - Angular lo usa para trackear el campo

4. **Interpolación:** `{{ editando ? 'Actualizar' : 'Agregar' }}`
   ```
   Si editando === true  → Texto: "Actualizar"
   Si editando === false → Texto: "Agregar"
   ```
   - Expresión JavaScript evaluada
   - Se actualiza reactivamente cuando cambia `editando`

5. **Directiva estructural:** `*ngIf="editando"`
   ```html
   <button *ngIf="editando">Cancelar</button>
   ```
   - **Si `editando === true`:** Botón existe en el DOM
   - **Si `editando === false`:** Botón NO existe (no solo oculto)
   - El `*` es azúcar sintáctica de Angular

---

#### Sección 2: Tabla Dinámica

```html
<table class="table table-striped">
  <thead>
    <tr>
      <th>ID</th>
      <th>IDEstudiante</th>
      <th>IDMateria</th>
      <th>Nota 1</th>
      <th>Nota 2</th>
      <th>Nota 3</th>
      <th>Promedio</th>
      <th>Acciones</th>
    </tr>
  </thead>
  <tbody>
    <tr *ngFor="let p of promedios">
      <td>{{ p.idPromedio }}</td>
      <td>{{ p.idEstudiante }}</td>
      <td>{{ p.idMateria }}</td>
      <td>{{ p.nota1 }}</td>
      <td>{{ p.nota2 }}</td>
      <td>{{ p.nota3 }}</td>
      <td>{{ (p.nota1 + p.nota2 + p.nota3) / 3 | number:'1.2-2' }}</td>
      <td>
        <button class="btn btn-sm btn-warning me-1" 
                (click)="editarPromedios(p)">
          Editar
        </button>
        <button class="btn btn-sm btn-danger" 
                (click)="eliminarPromedios(p.idPromedio)">
          Eliminar
        </button>
      </td>
    </tr>
  </tbody>
</table

---

##  Flujo de Datos Completo

### Escenario 1: Listar Promedios

```
1. Usuario navega a /promedios
   ↓
2. Angular ejecuta ngOnInit()
   ↓
3. Componente llama a promediosService.getPromedios()
   ↓
4. Servicio hace HTTP GET a backend
   ↓
5. Backend ejecuta WsColegio.getPromedios()
   ↓
6. Manager ejecuta SQL con INNER JOIN
   ↓
7. Base de datos retorna ResultSet
   ↓
8. Manager convierte ResultSet a List<Promedio>
   ↓
9. Backend serializa a JSON y envía Response
   ↓
10. Servicio recibe JSON y lo convierte a Promedios[]
   ↓
11. Observable emite el array
   ↓
12. Componente actualiza this.promedios
   ↓
13. Angular detecta cambio y re-renderiza tabla
```

---

### Escenario 2: Crear Promedio

```
1. Usuario llena formulario y click en "Agregar"
   ↓
2. ngSubmit ejecuta guardarPromedios()
   ↓
3. Componente verifica editando=false
   ↓
4. Llama a promediosService.insertPromedios(nuevoPromedio)
   ↓
5. Servicio hace HTTP POST con body JSON
   ↓
6. Backend recibe en WsColegio.insertPromedios(Promedio p)
   ↓
7. Manager.insertPromedio() calcula: prom = (nota1+nota2+nota3)/3.0
   ↓
8. Ejecuta INSERT INTO con 6 valores
   ↓
9. Base de datos genera idPromedio automático
   ↓
10. Manager obtiene ID generado con getGeneratedKeys()
   ↓
11. Backend retorna {"idPromedio": 5}
   ↓
12. Componente resetea formulario
   ↓
13. Llama a listarPromedios() para actualizar tabla
```

---

##  Manejo de Errores

### Backend
```java
try {
    // Operación con BD
} catch (Exception e) {
    e.printStackTrace();
    return Response.status(500).entity("Error al...").build();
}
```

### Frontend
```typescript
.subscribe({
  next: (data) => { /* éxito */ },
  error: (error) => console.error('Error:', error)
});
```

---

##  Logros Implementados

1. **Modelo de datos robusto** con 3 constructores para diferentes usos
2. **Cálculo automático de promedios** en backend (lógica centralizada)
3. **Consultas SQL optimizadas** con INNER JOIN para eficiencia
4. **API REST completa** con 4 endpoints (GET, POST, PUT, DELETE)
5. **Servicio Angular reactivo** con Observables
6. **Componente con estado** (modo crear/editar dinámico)
7.  **Formulario reactivo** con two-way data binding
8.  **Tabla interactiva** con botones de acción
9.  **Actualización automática** de la UI tras cada operación
10. **Validación de tipos** con TypeScript interfaces

---

**Proyecto desarrollado como parte del Semillero de formación - Colegio Quipux**