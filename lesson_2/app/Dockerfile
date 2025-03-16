# Используем официальный образ OpenJDK
FROM amazoncorretto:17 as builder

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем исходный код в контейнер
COPY . .

# Собираем проект с помощью Gradle
RUN ./gradlew clean build

# Используем легковесный образ для финального контейнера
FROM amazoncorretto:17-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

RUN apk update && apk add libstdc++

COPY --from=builder /app/build/libs/app-0.0.1-SNAPSHOT.jar ./app.jar

# Указываем точку входа
ENTRYPOINT ["java", "-jar", "app.jar"]