FROM maven:3.8.5-openjdk-17-slim

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean install -DskipTests


EXPOSE 8080


CMD ["java", "-jar", "target/tondeuse-0.0.1-SNAPSHOT.jar"]
