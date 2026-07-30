import os

svc = 'eureka-server'
base = os.path.join('backend', svc)
os.makedirs(os.path.join(base, 'src', 'main', 'java'), exist_ok=True)
os.makedirs(os.path.join(base, 'src', 'main', 'resources'), exist_ok=True)
os.makedirs(os.path.join(base, 'src', 'test'), exist_ok=True)
os.makedirs(os.path.join(base, 'docs'), exist_ok=True)
os.makedirs(os.path.join(base, 'sql'), exist_ok=True)
os.makedirs(os.path.join(base, 'scripts'), exist_ok=True)
os.makedirs(os.path.join(base, 'postman'), exist_ok=True)

with open(os.path.join(base, 'README.md'), 'w', encoding='utf-8') as f:
    f.write('''# Descripcion

# Responsabilidades

# Casos de uso

# Reglas de negocio

# Modelo de dominio

# Eventos publicados

# Eventos consumidos

# APIs REST

# APIs gRPC

# Base de datos

# Dependencias

# Configuracion

# Ejecucion

# Pruebas

# Observabilidad

# Roadmap

''')

with open(os.path.join(base, 'pom.xml'), 'w', encoding='utf-8') as f:
    f.write('''<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>com.fincore</groupId>
    <artifactId>fincore-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </parent>
  <artifactId>eureka-server</artifactId>
  <name>FinCore Eureka Server</name>

  <dependencies>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
  </dependencies>
</project>
''')

with open(os.path.join(base, 'Dockerfile'), 'w', encoding='utf-8') as f:
    f.write('FROM eclipse-temurin:17-jdk-alpine\nWORKDIR /app\nCOPY target/*.jar app.jar\nEXPOSE 8761\nENTRYPOINT ["java","-jar","/app/app.jar"]\n')

with open(os.path.join(base, '.gitignore'), 'w', encoding='utf-8') as f:
    f.write('target/\n*.jar\n*.war\n*.class\n.idea/\nvscode/\n*.iml\n.env\nlogs/\n')

with open(os.path.join(base, '.env.example'), 'w', encoding='utf-8') as f:
    f.write('SPRING_APPLICATION_NAME=eureka-server\nSERVER_PORT=8761\nEUREKA_CLIENT_REGISTER_WITH_EUREKA=false\nEUREKA_CLIENT_FETCH_REGISTRY=false\nEUREKA_INSTANCE_HOSTNAME=localhost\n')

with open(os.path.join(base, 'src', 'main', 'resources', 'application.properties'), 'w', encoding='utf-8') as f:
    f.write('spring.application.name=${SPRING_APPLICATION_NAME:eureka-server}\nserver.port=${SERVER_PORT:8761}\n\n')
    f.write('eureka.client.register-with-eureka=${EUREKA_CLIENT_REGISTER_WITH_EUREKA:false}\n')
    f.write('eureka.client.fetch-registry=${EUREKA_CLIENT_FETCH_REGISTRY:false}\n')
    f.write('eureka.instance.hostname=${EUREKA_INSTANCE_HOSTNAME:localhost}\n')
