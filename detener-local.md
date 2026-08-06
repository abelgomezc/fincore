# Detener Local — FinCore Banking

Guía para apagar el entorno completo de FinCore Banking:
- **Docker**: Redis, Kafka, Zookeeper, Kafka UI
- **Local**: microservicios Java y frontend React

---

## 1. Detener microservicios locales

### Opción A: Si usas `mvn spring-boot:run` (recomendado)

En cada terminal donde tengas un microservicio corriendo, presiona `Ctrl+C`.

O desde PowerShell, buscar y detener los procesos Maven:

```powershell
Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like "*spring-boot:run*" } | Stop-Process -Force
```

### Opción B: Si usas `java -jar`

```powershell
Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like "*fincore*" } | Stop-Process -Force
```

### Lista de microservicios a detener

| Servicio | Puerto |
|----------|--------|
| Eureka Server | 8761 |
| API Gateway | 8080 |
| Auth Service | 8081 |
| Customer Service | 8082 |
| Account Service | 8083 |
| Ledger Service | 8084 |
| Transfer Service | 8092 |
| Fraud Service | 8090 |
| Notification Service | 8085 |
| Audit Service | 8091 |
| Backoffice Service | 8093 |
| Batch Service | 8094 |

---

## 2. Detener frontend

En la terminal donde corre `npm run dev`, presiona `Ctrl+C`.

O desde PowerShell:

```powershell
Get-Process -Name "node" -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like "*vite*" } | Stop-Process -Force
```

Frontend corre en: **http://localhost:5173**

---

## 3. Detener contenedores Docker

```bash
# Detener contenedores de FinCore
docker stop fincore-redis fincore-kafka fincore-zookeeper fincore-kafka-ui

# Eliminar contenedores (opcional, no borra volúmenes)
docker rm fincore-redis fincore-kafka fincore-zookeeper fincore-kafka-ui
```

> **Nota**: No se eliminan los volúmenes, por lo que los datos de Redis y Kafka se preservan.

---

## 4. Verificar que todo está detenido

```powershell
$ports = @(5173, 8080, 8081, 8082, 8083, 8084, 8085, 8090, 8091, 8092, 8093, 8094, 8761, 6379, 9092, 2181, 8080)
foreach ($p in $ports) {
  try {
    $r = Test-NetConnection -ComputerName 127.0.0.1 -Port $p -WarningAction SilentlyContinue -InformationLevel Quiet
    if ($r) { Write-Output "${p}: OCUPADO" } else { Write-Output "${p}: libre" }
  } catch { Write-Output "${p}: libre" }
}
```

Todos los puertos deben mostrar **libre**.

---

## 5. Levantar nuevamente

Para volver a iniciar el entorno, ejecutar:

```bash
# 1. Levantar Docker
docker-compose up -d

# 2. Compilar y levantar microservicios
mvn compile -DskipTests -f fincore-parent/pom.xml
# Luego ejecutar cada servicio en su terminal correspondiente

# 3. Levantar frontend
cd frontend
npm run dev
```

O usar el script de arranque:

```bash
arranque-local.bat
```

Consultar `arranque-local.md` para instrucciones detalladas.
