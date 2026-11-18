# TP Final - Ecosistema de microservicios Fintech

Trabajo final de la Diplomatura en Desarrollo de Software FinTech: IA y Microservicios (UTN).

Son cuatro proyectos Spring Boot 4.1 (Spring Cloud 2025.1, Java 21) que trabajan juntos. Se reutilizo la estructura y el estilo del TP de medio termino (API Fintech).

| Proyecto | Puerto | Para que sirve |
|---|---|---|
| `config-server` | 8888 | Entrega la configuracion de todos los servicios. La lee de la carpeta `config-repo` de este repositorio en GitHub. |
| `eureka-server` | 8761 | Registro de servicios. Cada microservicio se anota aca con su nombre. |
| `product-service` | 8082 | Productos de los clientes: cuentas, tarjetas, prestamos e inversiones. |
| `customer-service` | 8081 | Clientes (nombre, apellido, CUIT/CUIL como documento, email y saldo). Para mostrar los productos de un cliente le pregunta a `product-service` con Feign. Tiene login con JWT. |

## Arquitectura

```
                      GitHub (carpeta config-repo/)
                                   |
                                   v
                          +-----------------+
                          |  config-server  |   (1) los otros tres le piden
                          |      :8888      |       su configuracion al arrancar
                          +-----------------+

                          +-----------------+
                          |  eureka-server  |   (2) los dos microservicios
                          |      :8761      |       se registran con su nombre
                          +-----------------+
                              ^         ^
                   registro  /           \  registro
                            /             \
        +------------------+   (3) Feign   +------------------+
        | customer-service | ------------> | product-service  |
        |      :8081       |               |      :8082       |
        +------------------+               +------------------+
                 |                                  |
                 v                                  v
         MySQL clientes_db                  MySQL productos_db
```

### Que pasa cuando se piden los productos de un cliente

Ejemplo: `GET http://localhost:8081/clientes/1/productos` con un token valido.

1. El filtro JWT de `customer-service` valida el token y el rol.
2. `ClienteController` llama a `ClienteService`, que busca el cliente 1 en `clientes_db`. Si no existe responde 404.
3. El servicio le pide los productos a `ProductoGateway`. Su implementacion usa el cliente Feign `ProductoClient`.
4. Feign no tiene una URL fija: le pregunta a Eureka donde esta `product-service` y llama a `GET /productos/cliente/1`.
5. `product-service` busca en `productos_db` y devuelve la lista.
6. `customer-service` arma la respuesta con el cliente y sus productos. Si `product-service` no responde, devuelve 503.

### Como se reparte la configuracion

Cada servicio, al arrancar, le pide su configuracion al Config Server usando su nombre (`spring.application.name`):

| Archivo en `config-repo/` | Lo recibe | Que tiene |
|---|---|---|
| `application.yml` | todos | direccion de Eureka, actuator, formato del JSON |
| `eureka-server.yml` | eureka-server | puerto 8761 |
| `product-service.yml` | product-service | puerto 8082, base `productos_db`, usuario y password de MySQL |
| `customer-service.yml` | customer-service | puerto 8081, base `clientes_db`, usuario y password de MySQL, clave y duracion del JWT |

En el proyecto de cada servicio solo queda el nombre y la direccion del Config Server (`localhost:8888`), que es lo minimo que necesita para saber a quien pedirle el resto.

### Decisiones de diseno

- Cada servicio tiene su propia base de datos. Un producto guarda solo el `clienteId`: no hay relacion entre tablas de distintos servicios.
- Las entidades nunca salen por la API. Todo entra y sale como DTO y los mappers hacen la conversion.
- Las passwords y la clave del JWT no estan escritas en el repo: se leen de variables de entorno (ver mas abajo).
- `product-service` no tiene seguridad propia: se lo piensa como un servicio interno, al que solo llama `customer-service`. En un despliegue real no se expondria afuera (quedaria detras de un API Gateway o en una red privada). La API publica, la de clientes, es la que esta protegida con JWT y roles.

## Estructura del repositorio

```
TP-Final-UTN/
├── config-repo/        archivos de configuracion (los lee el Config Server)
├── config-server/
├── eureka-server/
├── product-service/
└── customer-service/
```

Los dos microservicios siguen la misma organizacion en capas que el TP de medio termino:

| Paquete | Que tiene |
|---|---|
| `controladores` | Endpoints REST. Solo reciben y devuelven DTOs |
| `servicios` | Interfaz + implementacion con la logica de negocio |
| `repositorios` | Interfaces `JpaRepository` |
| `entidades` | Clases JPA (nunca salen por la API) |
| `dto` | Records de request y response, con validaciones |
| `mappers` | Pasan de DTO a entidad y de entidad a DTO |
| `excepciones` | Excepciones propias y el manejador global |
| `config` | Swagger y datos iniciales |
| `seguridad` | Solo customer-service: JWT, filtro y reglas por rol |
| `integracion` | Solo customer-service: cliente Feign hacia product-service |

### Principios SOLID aplicados

- **Responsabilidad unica:** cada clase hace una sola cosa. El controlador atiende HTTP, el servicio tiene la logica, el mapper convierte y el repositorio accede a la base.
- **Abierto/cerrado e inversion de dependencias:** los controladores dependen de interfaces (`ClienteService`, `ProductoService`, `AuthService`) y Spring inyecta la implementacion. `ClienteServiceImpl` pide los productos a la interfaz `ProductoGateway`, no a Feign directamente; si manana se cambia Feign por otra cosa, el servicio no se toca.
- **Segregacion de interfaces:** interfaces chicas y especificas (`ProductoGateway` tiene un solo metodo, `AuthService` tambien).
- **Sustitucion de Liskov:** cualquier implementacion de esas interfaces se puede usar en su lugar; en los tests se reemplazan por mocks sin cambiar el codigo.

## Tecnologias

Java 21, Spring Boot 4.1, Spring Cloud 2025.1 (Config Server, Eureka, OpenFeign, LoadBalancer), Spring Data JPA, MySQL, Spring Security con JWT (jjwt), Lombok, Swagger (springdoc), JUnit 5, Mockito y H2 para los tests.

## Requisitos

- Java 21
- No hace falta instalar Maven: cada proyecto trae el wrapper (`mvnw` / `mvnw.cmd`)
- MySQL corriendo en `localhost:3306`. Las bases `clientes_db` y `productos_db` se crean solas.

## Variables de entorno

La configuracion usa variables de entorno para no subir datos sensibles al repo. Si no se definen, se usan los valores entre parentesis.

| Variable | Para que | Valor por defecto |
|---|---|---|
| `DB_USER` | usuario de MySQL | `root` |
| `DB_PASSWORD` | password de MySQL | `root` |
| `JWT_SECRET` | clave para firmar los tokens (minimo 32 caracteres) | una clave de desarrollo |

En Windows se pueden definir una vez con `setx DB_PASSWORD tu_password` (y despues abrir una terminal nueva).

## Como levantarlo

### 1. Clonar el repositorio

```bash
git clone https://github.com/Gandara03/TP-Final-UTN.git
cd TP-Final-UTN
```

### 2. Arrancar los servicios en orden

El orden importa: primero la configuracion, despues el registro y al final los microservicios. Cada uno va en su propia terminal y hay que esperar a que muestre `Started ...Application` antes de pasar al siguiente.

En Windows (PowerShell):

```powershell
# Terminal 1
cd config-server
.\mvnw.cmd spring-boot:run

# Terminal 2
cd eureka-server
.\mvnw.cmd spring-boot:run

# Terminal 3
cd product-service
.\mvnw.cmd spring-boot:run

# Terminal 4
cd customer-service
.\mvnw.cmd spring-boot:run
```

En Linux o Mac es igual pero con `./mvnw spring-boot:run`. Tambien se pueden arrancar desde VS Code o IntelliJ con el boton Run de cada clase `...Application`, respetando el mismo orden.

### 3. Verificar que todo esta arriba

- Panel de Eureka: http://localhost:8761. Tienen que aparecer `CUSTOMER-SERVICE` y `PRODUCT-SERVICE`.
- Config que entrega el Config Server: http://localhost:8888/customer-service/default
- Swagger de clientes: http://localhost:8081/swagger-ui.html
- Swagger de productos: http://localhost:8082/swagger-ui.html

### Probar con la configuracion local

Por defecto el Config Server lee la configuracion de GitHub. Para usar la carpeta `config-repo` local (por ejemplo para probar un cambio sin hacer push):

```powershell
cd config-server
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=native"
```

### Problemas frecuentes

| Sintoma | Causa y solucion |
|---|---|
| `Access denied for user 'root'` al arrancar un microservicio | La password de MySQL no es `root`. Definir `DB_PASSWORD` (ver Variables de entorno) y abrir una terminal nueva. |
| Un microservicio arranca en el puerto 8080 | No encontro al Config Server. Revisar que `config-server` este levantado antes. |
| `GET /clientes/{id}/productos` devuelve 503 | `product-service` esta apagado o todavia no se registro en Eureka. Esperar unos segundos y reintentar. |
| El Config Server no arranca o no encuentra la config | No tiene acceso a GitHub. Usar el perfil `native` de arriba. |

## Seguridad

`customer-service` usa JWT con dos roles:

| Usuario | Password | Rol | Puede |
|---|---|---|---|
| `admin` | `admin123` | ADMIN | consultar, crear y modificar clientes |
| `usuario` | `usuario123` | USUARIO | solo consultar |

Son usuarios de prueba que se cargan al iniciar, igual que la clave por defecto del JWT: sirven solo para correrlo en local. Fuera de un entorno de desarrollo hay que cambiar las passwords y definir `JWT_SECRET`. Para usarlos:

1. `POST /auth/login` con `{"username": "admin", "password": "admin123"}`
2. Copiar el `token` de la respuesta.
3. Mandarlo en cada pedido con el header `Authorization: Bearer <token>`. En Swagger es el boton **Authorize**.

Sin token la respuesta es 401, y con un rol que no alcanza es 403.

## Endpoints

### customer-service (puerto 8081)

| Metodo | Ruta | Rol | Descripcion |
|---|---|---|---|
| POST | `/auth/login` | publico | Devuelve el token |
| GET | `/clientes` | ADMIN, USUARIO | Lista los clientes |
| GET | `/clientes/{id}` | ADMIN, USUARIO | Consulta un cliente |
| GET | `/clientes/cuit/{cuit}` | ADMIN, USUARIO | Consulta un cliente por su CUIT/CUIL |
| GET | `/clientes/{id}/productos` | ADMIN, USUARIO | Cliente con sus productos (via Feign) |
| POST | `/clientes` | ADMIN | Registra un cliente |
| PUT | `/clientes/{id}` | ADMIN | Actualiza un cliente |

Ejemplo de alta:

```json
{
  "nombre": "Sofia",
  "apellido": "Martinez",
  "cuit": "27356789011",
  "email": "sofia.martinez@gmail.com",
  "saldo": 85000
}
```

### product-service (puerto 8082)

| Metodo | Ruta | Descripcion |
|---|---|---|
| GET | `/productos` | Lista los productos activos |
| GET | `/productos/{id}` | Consulta un producto |
| GET | `/productos/cliente/{clienteId}` | Productos de un cliente (lo usa customer-service) |
| POST | `/productos` | Crea un producto |
| PUT | `/productos/{id}` | Actualiza un producto |
| DELETE | `/productos/{id}` | Baja logica |

Ejemplo de alta:

```json
{
  "clienteId": 1,
  "tipo": "TARJETA",
  "numero": "5412-0001",
  "descripcion": "Mastercard Black",
  "monto": 1500000
}
```

`tipo` puede ser `CUENTA`, `TARJETA`, `PRESTAMO` o `INVERSION`.

## Manejo de errores

Los dos servicios responden los errores con el mismo formato:

```json
{
  "status": 404,
  "error": "No existe un cliente con id 99",
  "timestamp": "2026-11-16T10:15:30.123"
}
```

| Codigo | Cuando |
|---|---|
| 400 | Datos invalidos (en `campos` viene el detalle de cada campo) |
| 401 | Sin token, token vencido o login incorrecto |
| 403 | El rol no tiene permiso |
| 404 | No existe el cliente o el producto |
| 409 | CUIT, email o numero de producto repetido |
| 503 | `product-service` no esta disponible al pedir los productos de un cliente |

## Datos de ejemplo

Al arrancar con las tablas vacias se cargan 3 clientes y 6 productos. Los clientes 1, 2 y 3 de `customer-service` son los duenos de los productos de `product-service`, asi `GET /clientes/1/productos` ya devuelve datos.

## Tests

Hay 31 tests (JUnit 5 + Mockito) y ninguno necesita MySQL ni los otros servicios levantados:

- **Servicios:** logica de negocio con el repositorio y product-service mockeados (alta, duplicados, 404, 503, baja logica).
- **Repositorios:** con `@DataJpaTest` sobre una base H2 en memoria.
- **Mappers, JWT y el gateway de Feign.**

```powershell
cd product-service
.\mvnw.cmd test

cd ../customer-service
.\mvnw.cmd test
```

## Integracion continua

En `.github/workflows/ci.yml` hay un workflow de GitHub Actions que, en cada push y pull request a `main` o `develop`, compila los cuatro proyectos y corre los tests. El resultado se ve en la pestana **Actions** del repo y en cada pull request.

## Postman

En `postman/TP-Final-UTN.postman_collection.json` esta la coleccion con todos los pedidos (en Postman: Import y elegir el archivo).

- El login guarda el token solo, asi que hay que ejecutarlo primero.
- Se puede correr entera con el Runner de Postman. La carpeta "Roles" va al final porque cambia el token por el del usuario de solo lectura.
- Esta pensada para una base recien creada (con los datos de ejemplo). Si se corre dos veces, los altas dan 409 porque los datos ya existen.

## Revision de codigo con IA

Como pide la consigna, se le pidio una revision del pull request `develop -> main` a GitHub Copilot.

Lo que marco y que se hizo:

| Observacion de Copilot | Que se hizo |
|---|---|
| Correr los tests de los dos microservicios | Se corrieron: 12 en product-service y 19 en customer-service, todos OK |
| Probar el arranque completo en orden (Config Server, Eureka, microservicios) | Se probo con MySQL y la coleccion de Postman entera (24 pedidos OK) |
| El Config Server lee de `main`, que tiene que tener `config-repo` | Se resuelve al mergear el pull request a `main` |
| Cambiar credenciales de prueba y secreto JWT fuera de local | Ya se leen de variables de entorno; se aclaro en la seccion Seguridad |
| Agregar un workflow de GitHub Actions | Se agrego `.github/workflows/ci.yml` (ver Integracion continua) |
| Las rutas de product-service son publicas | Se dejo asi a proposito y se documento en Decisiones de diseno |

