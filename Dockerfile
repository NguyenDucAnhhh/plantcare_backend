# Stage 1: Build file JAR
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Copy các file cấu hình gradle trước để tận dụng cache
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./

# Copy toàn bộ mã nguồn
COPY src ./src

# Build ứng dụng bỏ qua bước test (để deploy nhanh hơn)
RUN ./gradlew clean build -x test

# Stage 2: Chạy ứng dụng với môi trường nhỏ nhẹ (JRE)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy file jar từ Stage 1 sang Stage 2
COPY --from=build /app/build/libs/*.jar app.jar

# Render sẽ tự động gán PORT qua biến môi trường, thường là 8080 hoặc 10000
EXPOSE 8080

# Lệnh khởi chạy
ENTRYPOINT ["java", "-jar", "app.jar"]
