# Tourney — Docker setup

## Prerequisites
- Docker Desktop (or Docker Engine + Compose plugin) installed and running
- JDK 23 and Maven available locally (for the build step)

---

## Project layout

Place the files from this archive so they sit alongside your existing service
directories. The final structure should look like this:

```
tourney/
├── auth-service/
│   ├── Dockerfile          ← added
│   └── pom.xml, src/ …
├── tournament-service/
│   ├── Dockerfile          ← added
│   └── …
├── team-service/
│   ├── Dockerfile          ← added
│   └── …
├── gateway-service/
│   ├── Dockerfile          ← added
│   └── …
├── notification-service/
│   ├── Dockerfile          ← added
│   └── …
├── init-scripts/
│   └── auth-db-init.sql    ← added
├── docker-compose.yml      ← added
├── .env                    ← added  (edit before first run)
└── .dockerignore           ← added
```

---

## Step 1 — Edit .env

Open `.env` and fill in your real SMTP credentials.
Everything else can stay as the defaults for local development.

---

## Step 2 — Build all JARs

Run this once from the project root (where the parent `pom.xml` lives):

```bash
mvn clean package -DskipTests
```

This produces a `target/*.jar` inside each service directory, which is what
each Dockerfile copies into the image.

---

## Step 3 — Start everything

```bash
docker compose up --build
```

`--build` forces Docker to rebuild the images from the JARs you just compiled.
On subsequent runs where you haven't changed code, you can omit `--build`:

```bash
docker compose up
```

---

## Service URLs once running

| Service              | URL                          | Notes                        |
|----------------------|------------------------------|------------------------------|
| Gateway (entry point)| http://localhost:8080        | All browser traffic goes here|
| Auth service         | http://localhost:8081        | Direct access for debugging  |
| Tournament service   | http://localhost:8082        | Direct access for debugging  |
| Team service         | http://localhost:8083        | Direct access for debugging  |
| Notification service | http://localhost:8084        | Direct access for debugging  |
| RabbitMQ UI          | http://localhost:15672       | Login: tourney / tourney_secret |

---

## Useful commands

```bash
# Stop all containers (keeps volumes / data)
docker compose down

# Stop and delete all data (full reset)
docker compose down -v

# View logs for one service
docker compose logs -f tournament-service

# Rebuild a single service after a code change
mvn clean package -DskipTests -pl tournament-service
docker compose up --build tournament-service

# Open a psql shell on auth-db
docker exec -it auth-db psql -U tourney -d auth_db
```

---

## application.properties changes required

Each service's `application.properties` must read from environment variables
instead of hardcoded values. Spring Boot maps environment variables to
properties automatically (e.g. `SPRING_DATASOURCE_URL` →
`spring.datasource.url`), so the simplest approach is to use placeholders:

**auth-service**
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO:update}
server.port=${SERVER_PORT:8081}
```

**tournament-service**
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO:update}
auth.datasource.url=${AUTH_DATASOURCE_URL}
auth.datasource.username=${AUTH_DATASOURCE_USERNAME}
auth.datasource.password=${AUTH_DATASOURCE_PASSWORD}
auth.service.url=${AUTH_SERVICE_URL:http://localhost:8081}
team.service.url=${TEAM_SERVICE_URL:http://localhost:8083}
spring.rabbitmq.host=${SPRING_RABBITMQ_HOST:localhost}
spring.rabbitmq.port=${SPRING_RABBITMQ_PORT:5672}
spring.rabbitmq.username=${SPRING_RABBITMQ_USERNAME:guest}
spring.rabbitmq.password=${SPRING_RABBITMQ_PASSWORD:guest}
server.port=${SERVER_PORT:8082}
```

**team-service**
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO:update}
server.port=${SERVER_PORT:8083}
```

**notification-service**
```properties
spring.rabbitmq.host=${SPRING_RABBITMQ_HOST:localhost}
spring.rabbitmq.port=${SPRING_RABBITMQ_PORT:5672}
spring.rabbitmq.username=${SPRING_RABBITMQ_USERNAME:guest}
spring.rabbitmq.password=${SPRING_RABBITMQ_PASSWORD:guest}
spring.mail.host=${SPRING_MAIL_HOST:smtp.gmail.com}
spring.mail.port=${SPRING_MAIL_PORT:587}
spring.mail.username=${SPRING_MAIL_USERNAME}
spring.mail.password=${SPRING_MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
notification.email.to=${NOTIFICATION_EMAIL_TO}
notification.email.from=${NOTIFICATION_EMAIL_FROM}
server.port=${SERVER_PORT:8084}
```

**gateway-service**
```properties
server.port=${SERVER_PORT:8080}
```
