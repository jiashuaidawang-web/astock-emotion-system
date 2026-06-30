FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace
COPY . .
RUN mvn -U clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
ENV TZ=Asia/Shanghai
COPY --from=builder /workspace/astock-app/target/*.jar /app/astock-app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/astock-app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-local}"]
