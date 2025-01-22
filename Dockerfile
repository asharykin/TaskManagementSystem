FROM openjdk:17-jdk-slim
WORKDIR /app
COPY pom.xml mvnw .mvn/ src/ ./
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests
CMD java -jar target/tms*.jar