FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y postgresql-client && apt-get clean
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn/ .mvn/
COPY src/ src/
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests
CMD java -jar target/*.jar