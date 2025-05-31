**Задание 1**

Создаем топик balanced_topic с 8 партициями и фактором репликации 3: 
```
kafka-topics --bootstrap-server localhost:9094 --topic balanced_topic --create --partitions 8 --replication-factor 3
```

Для перераспределения используем reassignment.json 
```
{
    "version": 1,
    "partitions": [
      {"topic": "balanced_topic", "partition": 0, "replicas": [0, 1], "log_dirs": ["any", "any"]},
      {"topic": "balanced_topic", "partition": 1, "replicas": [1, 2], "log_dirs": ["any", "any"]},
      {"topic": "balanced_topic", "partition": 2, "replicas": [2, 3], "log_dirs": ["any", "any"]},
      {"topic": "balanced_topic", "partition": 3, "replicas": [3, 0], "log_dirs": ["any", "any"]},
      {"topic": "balanced_topic", "partition": 4, "replicas": [0, 2], "log_dirs": ["any", "any"]},
      {"topic": "balanced_topic", "partition": 5, "replicas": [1, 3], "log_dirs": ["any", "any"]},
      {"topic": "balanced_topic", "partition": 6, "replicas": [2, 0], "log_dirs": ["any", "any"]},
      {"topic": "balanced_topic", "partition": 7, "replicas": [3, 1], "log_dirs": ["any", "any"]}
    ]
  }
```
Вот такой командой 
```
kafka-reassign-partitions \
--bootstrap-server localhost:9094 \
--broker-list "0,1,2" \
--topics-to-move-json-file "reassignment.json" \
--generate
```

И вот так применяем 
```
kafka-reassign-partitions --bootstrap-server localhost:9094 --reassignment-json-file reassignment.json --execute
```

Вот такой результат выдастся:
![Ожидаемый вывод консоли](imgs/after_reassiginment.png)
![Видим что поменялось](imgs/after_reassiginment_ui.png)


Останавливаем kafka-1
![Как остановить kafka-1](imgs/stop_kafka_1.png)
 
![После сбоя](imgs/after_stop.png)

Вернули обратно и стало хорошо: 
![Синхронизация восстановилось](imgs/kafka-1-return.png)

**Задание 2**
Запускаем кластер
```
docker-compose up
```

Создаем юзеров
```
# login = admin   password = admin-secret
docker exec -it kafka-1 kafka-configs --zookeeper zookeeper:2181 \
  --alter --add-config 'SCRAM-SHA-256=[iterations=4096,password=admin-secret]' \
  --entity-type users --entity-name admin

# login = producer1   password = producer-secret
docker exec -it kafka-1 kafka-configs --zookeeper zookeeper:2181 \
  --alter --add-config 'SCRAM-SHA-256=[iterations=4096,password=producer-secret]' \
  --entity-type users --entity-name producer1

# login = consumer1
docker exec -it kafka-1 kafka-configs --zookeeper zookeeper:2181 \
  --alter --add-config 'SCRAM-SHA-256=[iterations=4096,password=consumer-secret]' \
  --entity-type users --entity-name consumer1
```