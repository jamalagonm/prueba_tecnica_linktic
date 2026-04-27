# Prueba Técnica — Linktic

Proyecto de microservicios desarrollado en Spring Boot, orquestado con Docker Compose. Expone sus endpoints a través de un API Gateway que requiere autenticación por `X-API-KEY`.

---

## Despliegue

### 1. Crear el archivo `.env`

En la raíz del proyecto (junto a `docker-compose.yml`) crea un archivo llamado `.env` con el siguiente contenido:

```env
# Base de datos
DB_HOST=postgres-db
DB_NAME=name_db
DB_PORT=5432
DB_USERNAME=postgres
DB_PASSWORD=postgres

# Seguridad
API_KEY=my_secure_api_key

# Puertos de servicios
PRODUCTO_SERVICE_PORT=8090
INVENTARIO_SERVICE_PORT=8091
GATEWAY_PORT=8080

# URLs internas de Docker
PRODUCTO_SERVICE_URL=http://producto-service:8090
INVENTARIO_SERVICE_URL=http://inventario-service:8091

PRODUCTO_BASE_URL=http://producto-service:8090
INVENTARIO_BASE_URL=http://inventario-service:8091
```

### 2. Requisitos previos

- Docker instalado en el equipo donde se desplegará el proyecto.

### 3. Levantar el proyecto

Desde la ruta donde se encuentra `docker-compose.yml`:

```bash
docker-compose up -d --build
```

### 4. Bajar el proyecto

```bash
docker-compose down
```

---

## Decisiones arquitectónicas

- La base de datos se dockeriza junto al proyecto, lo que permite que funcione de forma inmediata al seguir las instrucciones de despliegue.
- Un API Gateway centraliza todas las peticiones y añade una capa de seguridad mediante `X-API-KEY`.
- Cada microservicio sigue una arquitectura por capas: `Controller → Service → Repository`, con manejo de excepciones propio e interfaces que facilitan la escalabilidad.
- El servicio de compras se integra dentro del microservicio `inventario`, ya que está directamente ligado al stock disponible del producto, simplificando su interacción.

---

## Diagrama de interacción entre servicios

### Arquitectura general

```
                         ┌───────────────┐
                         │    Cliente    │
                         │ (Postman/App) │
                         └───────┬───────┘
                                 │ X-API-KEY
                                 ▼
                         ┌───────────────┐
                         │  API Gateway  │
                         │    :8080      │
                         └───┬───────┬───┘
                             │       │
          /api/v1/productos  │       │  /api/v1/inventario
                             │       │  /api/v1/compra
                             ▼       ▼
                 ┌──────────────┐  ┌─────────────-─┐
                 │ MS Producto  │  │ MS Inventario │
                 │    :8090     │◄─┤    :8091      │
                 └──────┬───────┘  └──────┬────────┘
                        │   HTTP interno  │
                        │                 │
                        │    JPA          │    JPA
                        ▼                 ▼
                 ┌────────────────────-─────────┐
                 │     PostgreSQL :5432         │
                 │     microservicios_db        │
                 ├─────────┬─────────-┬─────────┤
                 │productos│inventario│historial│
                 │         │          │_compras │
                 └─────────┴─────────-┴─────────┘
```

### Puertos

| Servicio      | Puerto | Descripción                       |
|---------------|--------|-----------------------------------|
| API Gateway   | 8080   | Punto de entrada único            |
| MS Producto   | 8090   | Gestión del catálogo de productos |
| MS Inventario | 8091   | Gestión de stock y compras        |
| PostgreSQL    | 5432   | Base de datos compartida          |

### Comunicación entre servicios

```
┌──────────────────┐         HTTP / REST          ┌──────────────────┐
│   MS Producto    │ ──────────────────────────▶  │   MS Inventario  │
│     :8090        │  POST                        │     :8091        │
│                  │ ◀──────────────────────────  │                  │
│                  │ Response:inventario response │                  │
└──────────────────┘                              └──────────────────┘
```

```
┌──────────────────┐         HTTP / REST          ┌──────────────────┐
│  MS Inventario   │ ──────────────────────────▶  │   MS Producto    │
│     :8091        │  GET /obtener-nombre/{id}    │     :8090        │
│                  │ ◀──────────────────────────  │                  │
│                  │  Response: nombre producto   │                  │
└──────────────────┘                              └──────────────────┘
```

La comunicación interna entre microservicios se realiza mediante HTTP REST. El microservicio `inventario` consume el endpoint del microservicio `producto` para obtener el nombre del producto durante el flujo de compra. Ambas peticiones internas incluyen el header `X-API-KEY` para autenticación.


### Descripción flujo de compra
```
                      POST /api/v1/compra {productoId, cantidad}
                                        │
                                        ▼
                                ┌─────────────────────┐
                                │  Validar producto   │──── No existe ────▶ 404
                                │  (buscar por ID)    │
                                └─────────┬───────────┘
                                          │ Existe
                                          ▼
                                ┌─────────────────────┐
                                │  Validar stock      │──── Insuficiente ──▶ 400
                                │  cantidad ≤ stock   │
                                └─────────┬───────────┘
                                          │ Suficiente
                                          ▼
                                ┌─────────────────────┐
                                │  Obtener nombre     │
                                │  HTTP → ms producto │
                                └─────────┬───────────┘
                                          │
                                          ▼
                                ┌─────────────────────┐
                                │  Actualizar stock   │
                                │  stock - cantidad   │
                                └─────────┬───────────┘
                                          │
                                          ▼
                                ┌─────────────────────┐
                                │  Registrar compra   │
                                │  historial_compras  │
                                └─────────┬───────────┘
                                          │
                                          ▼
                                      201 Created
```

### Uso de la IA en el proyecto

1. Optimización del código
2. Generación de las clases de configuración, posteriormente ajustadas
3. Creacion de los archivos docker y docker-compose
4. Ajuste de las pruebas unitarias y pruebas de integracón
5. Ajuste del archivo readme.MD haciendolo más legible