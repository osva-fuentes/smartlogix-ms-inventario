# ms-inventario

Microservicio de gestión de inventario para SmartLogix SaaS. Controla el stock de productos, procesa pagos asociados a las compras, sincroniza con plataformas externas y emite alertas automáticas cuando el stock llega a niveles críticos.

## Patrones de diseño implementados

| Patrón | Ubicación | Propósito |
|---|---|---|
| Singleton | `InventarioService` | Spring gestiona una única instancia del servicio para mantener consistencia de memoria |
| Observer | `StockBajoEvent` + `NotificacionStockListener` | Emite una alerta automática cuando el stock baja de 5 unidades |
| Adapter | `MarketplaceAdapter` + `MercadoLibreAdapter` | Estandariza la sincronización de stock con plataformas externas |
| Strategy | `PagoStrategy` + `TarjetaCreditoPago` | Permite intercambiar el método de pago sin modificar el controlador |
| Repository | `InventarioRepository` | Abstrae el acceso a la base de datos mediante Spring Data JPA |

## Tecnologías

- Java 21
- Spring Boot 3.5.14
- Spring Data JPA
- H2 Database (en memoria)
- Lombok
- Docker

## DTO implementados

- `ProductoRequestDTO`: datos de entrada para crear/actualizar un producto (sin id, generado por la BD).
- `ProductoResponseDTO`: datos de salida con el id real y un campo calculado `estadoStock` (DISPONIBLE / CRÍTICO / AGOTADO).

## Endpoints

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/inventario/todos` | Lista todos los productos en formato DTO |
| GET | `/api/inventario/{id}` | Busca un producto por ID, usado por ms-pedidos vía RestTemplate |
| POST | `/api/inventario/crear` | Crea un nuevo producto |
| POST | `/api/inventario/descontar/{id}?cantidad=N` | Descuenta stock, dispara Strategy + Adapter + Observer |

## Seguridad

Las rutas bajo `/api/inventario` están protegidas por un `ApiGatewayFilter` que exige el header:

```
Authorization: SmartLogix-Token-2024
```

## Cómo ejecutar localmente

### Requisitos
- Java 21
- Maven 3.9+
- Docker (opcional, recomendado)

### Con Maven

```bash
mvn clean package -DskipTests
java -jar target/prueba-0.0.1-SNAPSHOT.jar
```

El servicio queda disponible en `http://localhost:8080`.

### Con Docker

```bash
docker build -t ms-inventario .
docker run -p 8080:8080 ms-inventario
```

### Con Docker Compose (junto a los demás microservicios)

Ver el repositorio [smartlogix-principal](https://github.com/osva-fuentes/smartlogix-principal) para el `docker-compose.yml` completo.

## Cómo probar

```bash
curl http://localhost:8080/api/inventario/todos
```

```bash
curl -X POST "http://localhost:8080/api/inventario/descontar/1?cantidad=1" \
  -H "Authorization: SmartLogix-Token-2024"
```

Respuesta esperada: `Compra exitosa`

## Estructura del proyecto

```
src/main/java/com/proyectofullstack/prueba/
├── Controller/      InventarioController
├── Service/         InventarioService
├── Repository/       InventarioRepository
├── Entity/           Inventario
├── dto/               ProductoRequestDTO, ProductoResponseDTO
├── adapter/           MarketplaceAdapter, MercadoLibreAdapter
├── Strategy/          PagoStrategy, TarjetaCreditoPago
├── Factory/           PagoFactory
├── Event/             StockBajoEvent, NotificacionStockListener
└── Security/          ApiGatewayFilter, CorsConfig
```
