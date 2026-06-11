# Tomcat Gradle Docker App

This project is a starter web application using Apache Tomcat 9, Gradle, and Docker.

## Build and Run

```bash
./gradlew clean war
./gradlew run
```

### Docker

```bash
docker build -t tomcat-gradle-app .
docker run -p 8080:8080 tomcat-gradle-app
```

### Docker Compose

```bash
docker-compose up --build
```
