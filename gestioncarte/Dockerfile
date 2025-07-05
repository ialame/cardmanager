# Étape 1 : Build avec Maven
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /usr/src/app

COPY ./pom.xml ./pom.xml
COPY ./mason/pom.xml ./mason/
COPY ./painter/pom.xml ./painter/
COPY ./gestioncarte/pom.xml ./gestioncarte/
COPY ./gestioncarte/src/main/frontend/package.json ./gestioncarte/src/main/frontend/
COPY ./gestioncarte/src/main/frontend/package-lock.json ./gestioncarte/src/main/frontend/package-lock.json
COPY ./mason ./mason
COPY ./painter ./painter
COPY ./gestioncarte ./gestioncarte

RUN mvn -f ./pom.xml clean install -DskipTests

# Debug: List the contents of the target directory
RUN ls -l /usr/src/app/gestioncarte/target/

# Étape 2 : Image finale avec JRE
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /usr/src/app/gestioncarte/target/retriever-*.jar app.jar

CMD ["java", "-jar", "app.jar"]