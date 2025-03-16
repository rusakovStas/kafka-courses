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

Идея такая: 
Я создал топик messages
В java/ydx/practicum/app/config/KafkaConfig.java - создаются стримы из топиков в виде Bean. 
    1. ydx.practicum.app.config.KafkaConfig#blackListUserStream - KTable из топика сообщений о блокировках юзеров - где ключом выступает юзер, а value - его блэклист (для KTable создается PersistentStore)
    2. ydx.practicum.app.config.KafkaConfig#blackListWordsStream - KTable из топика запрещенных слов. Ключом выступает общий ключ для всех слов. Значением - Set<String> со всеми словами. Для этой KTable создается PersistanceStore. 
Далее я создал отдельные компоненты для процессинга потока сообщений. 
java/ydx/practicum/app/config/MessageBlocker.java - для применения блэклистов юзеров 
В рамках этого компонента - я заджоинил поток сообщений с KTable из blackListUserStream. И если сообщение попадает под условия блэклиста - то оно фильтруется (хотелось бы направлять в специальный топик banned-messages, но руки не дошли)
java/ydx/practicum/app/config/MessageCense.java - для затирания запрещенных слов. <--- Вот тут у меня не работает( Никак не могу доставить стор.  
java/ydx/practicum/app/config/MessageRoute.java - для роутинга сообщений в отдельные топики для каждого юзера

java/ydx/practicum/app/config/MessageTopology.java - в этом компоненте я просто создаю поток из топика messages и прогоняю его через все обработчики
