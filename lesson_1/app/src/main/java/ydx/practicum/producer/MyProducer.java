package ydx.practicum.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import ydx.practicum.model.Message;
import ydx.practicum.model.MessageSerializer;

import java.util.Properties;

public class MyProducer {

    private final KafkaProducer<String, Message> kafkaProducer;

    public MyProducer(String clusterHost){
        // Конфигурация продюсера – адрес сервера, сериализаторы для ключа и значения.
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, clusterHost);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageSerializer.class.getName());
        props.put("acks", "all"); // Гарантирует запись во все реплики
        props.put("retries", 3); // Количество повторных попыток
        props.put("enable.idempotence", true); // Включает идемпотентность

        // Создание продюсера
        this.kafkaProducer = new KafkaProducer<>(props);
    }
    public void sendMessage(String topic, int partitionCount, Message msg){
        // Отправка сообщения
        for (int i = 0; i < partitionCount; i++){
            // указываем разные ключи => попадут в разные партиции
            ProducerRecord<String, Message> record = new ProducerRecord<>(topic,"key-"+i, msg);
            this.kafkaProducer.send(record);
        }
    }
}
