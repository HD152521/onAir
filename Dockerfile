# Build Stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# 의존성 캐시를 위한 디렉토리 생성
COPY gradle/wrapper/gradle-wrapper.properties gradle/wrapper/gradle-wrapper.properties
COPY gradle /app/gradle
COPY build.gradle settings.gradle /app/

# 의존성만 먼저 다운로드
RUN #gradle clean build -x test --no-daemon --refresh-dependencies

COPY --chown=gradle:gradle . .
RUN gradle build -x test --no-daemon

# Run Stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar" , "app.jar"]