package ydx.practicum.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import ydx.practicum.model.Message;
import ydx.practicum.model.MessageDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.function.Consumer;

public class PushConsumer {
    private Properties props;

    public PushConsumer(String clusterHost, String consumerGroup) {
        // Настройка консьюмера – адрес сервера, сериализаторы для ключа и значения

        this.props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, clusterHost);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//       отключаем автокоммит для этого консюмера
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
    }

    public void subscribe(String topic, Consumer<ConsumerRecord<String, Message>> handleMsg){
        try (var consumer = new KafkaConsumer<String, Message>(props)) {

            // Подписка на топик
            consumer.subscribe(Collections.singletonList(topic));

            // Чтение сообщений
            while (true) {
                ConsumerRecords<String, Message> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, Message> record : records) {
                    handleMsg.accept(record);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
