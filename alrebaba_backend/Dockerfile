# Stage 1: Build
FROM gradle:7.5-jdk17 as builder
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Stage 2: Package
FROM openjdk:17-jdk
WORKDIR /spring-boot
COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar
COPY ./src/main/resources/application.yml /app/config/application.yml
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/spring-boot/app.jar" ,"-Duser.timezone=Asia/Seoul" ]
