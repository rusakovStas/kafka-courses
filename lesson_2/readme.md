Для проверки работы: 

1. Запускаем кластер кафка ```docker-compose up -d ```
2. Переходим в директорию приложения ```cd app```
3. Собираем докер образ приложения ```docker build -t lesson2-app .```
4. Запускаем приложение ```docker run --network host lesson2-app```

После этого можно будет увидеть что консьюмеры параллельно вычитывают сообщения. 

Информация для тестирования: 
Для того что бы отправить сообщение от юзера user4 до юзера user3: 
http://localhost:8002/send?userTo=user3&&userFrom=user4&&message=hello%20world

Для того что бы запретить слово: 
http://localhost:8002/block-word?word=hello

Для того что бы добавить пользователя в блэклист другого: 
http://localhost:8002/block-user?userFor=user3&&userToBlock=user4
