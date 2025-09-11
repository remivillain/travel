FROM openjdk:21-jdk-slim

WORKDIR /app

# Copier et construire l'application
COPY . .
RUN chmod +x ./mvnw && ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/travel-0.0.1-SNAPSHOT.jar"]
