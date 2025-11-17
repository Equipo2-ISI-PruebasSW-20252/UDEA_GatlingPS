# Gatling Performance Testing - ParaBank

Proyecto de pruebas de rendimiento para la aplicación ParaBank utilizando Gatling 3.3.1. Este repositorio contiene simulaciones diseñadas para validar los requisitos no funcionales del sistema bancario bajo diferentes escenarios de carga.

## Descripción

Este proyecto implementa pruebas de rendimiento automatizadas para validar que el sistema ParaBank cumple con los requisitos no funcionales de tiempo de respuesta, escalabilidad y disponibilidad bajo diferentes cargas de usuarios concurrentes.

**Sistema bajo prueba:** ParaBank Demo Banking Application  
**URL Base:** `https://parabank.parasoft.com/parabank/services/bank`  
**Framework:** Gatling 3.3.1  
**Lenguaje:** Scala 2.12

## Requisitos Previos

- Java JDK 8 o superior
- Apache Maven 3.6+

## Instalación
```bash
# Clonar el repositorio
git clone <repository-url>
cd GatlingProject

# Compilar el proyecto
mvn clean install

# Verificar la instalación
mvn gatling:help
```

## Estructura del Proyecto
```
GatlingProject/
├── src/test/
│   ├── scala/parabank/
│   │   ├── Data.scala                          # Datos de configuración compartidos
│   │   ├── LoginTest.scala                     # HU1: Login bajo carga
│   │   ├── TransferTest.scala                  # HU2: Transferencias simultáneas
│   │   ├── CargaSimultaneaEstadoCuenta.scala  # HU3: Consulta de estados de cuenta
│   │   ├── LoanRequestTest.scala              # HU4: Solicitudes de préstamo
│   │   └── PaymentServiceTest.scala           # HU5: Pagos de servicios
│   └── resources/
│       ├── data/
│       │   ├── cargaSimultaneaEstadoCuenta.csv
│       │   ├── loanRequests.csv
│       │   ├── payments.csv
│       │   └── transaction.csv
│       ├── gatling.conf                       # Configuración de Gatling
│       └── logback.xml                        # Configuración de logs
├── pom.xml
└── README.md
```

## Historias de Usuario

### Historia de Usuario No Funcional 1: Tiempo de respuesta en login

**Como** usuario del banco,  
**quiero** que el sistema procese mi inicio de sesión en menos de 2 segundos bajo carga normal,  
**para** que pueda acceder rápidamente a mi cuenta sin demoras innecesarias.

**Criterios de aceptación:**
- El tiempo de respuesta para el login debe ser ≤ 2 segundos con hasta 100 usuarios concurrentes
- Bajo carga pico (200 usuarios), el tiempo no debe superar los 5 segundos
- Tasa de éxito > 95%

**Implementación:** `LoginTest.scala`

---

### Historia de Usuario No Funcional 2: Transferencias simultáneas

**Como** usuario del banco,  
**quiero** que el sistema pueda escalar eficientemente cuando muchos usuarios hacen transferencias al mismo tiempo,  
**para** que la experiencia se mantenga fluida sin errores o interrupciones.

**Criterios de aceptación:**
- El sistema debe manejar al menos 150 transacciones por segundo durante pruebas de estrés
- No deben perderse transacciones ni ocurrir fallos bajo carga intensa
- Se debe usar un feeder de Gatling mediante CSV
- Tasa de éxito ≥ 95%

**Implementación:** `TransferTest.scala`

---

### Historia de Usuario No Funcional 3: Carga simultánea de estados de cuenta

**Como** usuario del banco,  
**quiero** que la consulta de mi estado de cuenta sea rápida incluso cuando muchos usuarios la están solicitando al mismo tiempo,  
**para** que pueda acceder a mi historial sin retrasos.

**Criterios de aceptación:**
- Consultas a los estados de cuenta deben tener un tiempo de respuesta ≤ 3 segundos con 200 usuarios simultáneos
- La tasa de error durante la prueba de carga no debe superar el 1%
- Tasa de éxito ≥ 99%

**Implementación:** `CargaSimultaneaEstadoCuenta.scala`

---

### Historia de Usuario No Funcional 4: Solicitud de préstamo bajo carga

**Como** usuario del banco,  
**quiero** que el sistema procese solicitudes de préstamo de forma rápida y sin errores, incluso cuando muchos usuarios las envían al mismo tiempo,  
**para** que pueda obtener una respuesta oportuna sobre mi elegibilidad.

**Criterios de aceptación:**
- Con una carga de 150 usuarios concurrentes realizando solicitudes de préstamo
- El tiempo de respuesta promedio debe ser ≤ 5 segundos
- El sistema debe mantener una tasa de éxito ≥ 98%
- No deben presentarse errores de validación inesperados ni caídas del servicio
- Cero solicitudes fallidas

**Implementación:** `LoanRequestTest.scala`

---

### Historia de Usuario No Funcional 5: Pago de servicios con concurrencia alta

**Como** cliente del banco,  
**quiero** que el módulo de pago de servicios funcione de manera eficiente durante picos de uso,  
**para** que pueda realizar mis pagos sin retrasos ni fallas, incluso en horarios de alta demanda.

**Criterios de aceptación:**
- Durante una simulación de 200 usuarios concurrentes realizando pagos
- El tiempo de respuesta por transacción debe ser ≤ 3 segundos
- La tasa de errores funcionales debe ser ≤ 1%
- El sistema debe registrar correctamente el pago en el historial del usuario sin duplicaciones
- Tasa de éxito ≥ 99%

**Implementación:** `PaymentServiceTest.scala`

---

## Ejecución de Pruebas

### Ejecutar todas las simulaciones
```bash
mvn clean gatling:test
```

### Opciones adicionales de Maven
```bash
# Ejecutar con logs detallados
mvn gatling:test -X
```

## Interpretación de Resultados

Los resultados se generan automáticamente en `target/gatling/` con una estructura como:
```
target/gatling/
└── [simulation-name]-[timestamp]/
    ├── index.html          # Reporte interactivo principal
    ├── js/
    ├── style/
    └── simulation.log      # Log detallado de la simulación
```

## CI/CD

El proyecto incluye un workflow de GitHub Actions (`.github/workflows/gatling.yml`) que:

1. Se ejecuta automáticamente en push/PR a la rama `main`
2. Configura el entorno con JDK 17
3. Compila el proyecto con Maven
4. Ejecuta todas las simulaciones de Gatling
5. Almacena los reportes como artefactos

### Visualizar reportes en GitHub Actions

1. Ve a la pestaña "Actions" del repositorio
2. Selecciona el workflow ejecutado
3. Descarga el artefacto "gatling" en la sección "Artifacts"
4. Descomprime y abre `index.html` en tu navegador

### Configuración de Gatling

El archivo `gatling.conf` permite ajustar:
- Timeouts de conexión
- Configuración de reportes
- Indicadores de rendimiento
- Escritores de datos

**Nota:** ParaBank es una aplicación demo proporcionada por Parasoft con fines educativos. Los datos utilizados en estas pruebas son ficticios y no representan información bancaria real.
