FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y postgresql-client && apt-get clean
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn/ .mvn/
COPY src/ src/
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests
HEALTHCHECK --interval=5s --timeout=3s --retries=3 CMD pg_isready -h db -p 5432 -U postgres -d postgres
CMD java -jar target/*.jar