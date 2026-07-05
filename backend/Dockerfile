FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B
COPY backend/src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/news-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Duser.timezone=Asia/Shanghai", "-XX:+UseContainerSupport", "-XX:InitialRAMPercentage=60.0", "-XX:MaxRAMPercentage=75.0", "-XX:+UseG1GC", "-XX:CICompilerCount=4", "-XX:+HeapDumpOnOutOfMemoryError", "-XX:HeapDumpPath=/tmp/heapdump.hprof", "-jar", "app.jar"]
