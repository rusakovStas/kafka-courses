package ydx.practicum.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import ydx.practicum.model.Message;
import ydx.practicum.model.MessageDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

public class PullConsumer {

    private final KafkaConsumer<String, Message> consumer;

    public PullConsumer(String clusterHost, String consumerGroup, String topic) {
        // Настройка консьюмера – адрес сервера, сериализаторы для ключа и значения

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, clusterHost);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 10 * 1024 * 1024);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//       отключаем автокоммит для этого консюмера
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        this.consumer = new KafkaConsumer<String, Message>(props);
        // Подписка на топик
        consumer.subscribe(Collections.singletonList(topic));
    }

    public void pull(Consumer<ConsumerRecord<String, Message>> handleMsgs) {
        consumer.poll(Duration.ofMillis(10_000)).forEach((record) -> {
            handleMsgs.accept(record);
            //вызываем ручной коммит после успешной обработки сообщения
            Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
            offsets.put(new TopicPartition(record.topic(), record.partition()),
                    new OffsetAndMetadata(record.offset() + 1)); // +1, так как смещение указывает на следующее сообщение
            consumer.commitSync(offsets);
        });
    }
}
