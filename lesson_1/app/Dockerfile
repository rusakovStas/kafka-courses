# Используем официальный образ OpenJDK
FROM amazoncorretto:17 as builder

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем исходный код в контейнер
COPY . .

# Собираем проект с помощью Gradle
RUN ./gradlew shadowJar

# Используем легковесный образ для финального контейнера
FROM amazoncorretto:17-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

COPY --from=builder /app/build/libs/app-1.0-all.jar ./app.jar

# Указываем точку входа
ENTRYPOINT ["java", "-jar", "app.jar"]