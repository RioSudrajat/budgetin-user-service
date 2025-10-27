# --- Tahap 1: Build ---
# Gunakan image Maven resmi yang sudah ada Java 17 di dalamnya
FROM maven:3.9-eclipse-temurin-17 AS build

# Set working directory di dalam container
WORKDIR /app

# Copy file pom.xml dulu (agar Maven bisa download dependency)
COPY pom.xml .

# Copy Maven wrapper files (jika diperlukan oleh build)
COPY .mvn/ .mvn
COPY mvnw .
COPY mvnw.cmd .

# Download semua dependency (ini akan di-cache jika pom.xml tidak berubah)
RUN mvn dependency:go-offline -B

# Copy sisa source code
COPY src ./src

# Build aplikasi (membuat file JAR)
# Kita skip tes lagi di sini
RUN mvn package -DskipTests


# --- Tahap 2: Run ---
# Gunakan image Java 17 yang lebih kecil (hanya JRE) untuk menjalankan aplikasi
FROM eclipse-temurin:17-jre-jammy

# Set working directory
WORKDIR /app

# Copy file JAR yang sudah di-build dari tahap sebelumnya
COPY --from=build /app/target/user-service-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 (port default Spring Boot)
EXPOSE 8080

# Perintah untuk menjalankan aplikasi saat container start
ENTRYPOINT ["java", "-jar", "app.jar"]
