FROM openjdk:17-jdk-slim
WORKDIR /app
COPY pom.xml .
COPY mvnw .
COPY .mvn/ .mvn/
COPY src/ src/
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests
CMD sleep 5 && java -jar target/tms*.jar