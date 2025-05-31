
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;

public class SimpleProducer {
    public static void main(String[] args) throws Exception {
        // Настройка Producer
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-1:9093"); // Адрес брокера (может быть localhost:9093, если тестируешь локально)
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // Аутентификация SASL/SCRAM
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "SCRAM-SHA-256");
        props.put("sasl.jaas.config",
                "org.apache.kafka.common.security.scram.ScramLoginModule required " +
                        "username=\"producer1\" password=\"producer-secret\";");

        // Создание продюсера
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        String topic = "topic-1";

        // Отправка одного сообщения
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, "my-key", "Hello from Java Producer!");
        RecordMetadata meta = producer.send(record).get(); // Синхронно, ждем подтверждения

        System.out.printf("Сообщение опубликовано: topic=%s partition=%d offset=%d%n",
                meta.topic(), meta.partition(), meta.offset());

        producer.close();
    }
}
