package ydx.practicum;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static final String CLUSTER_HOST = System.getProperty("bootstrap.servers", "localhost:9093,localhost:9095,localhost:9097");
    public static final String TOPIC_1 = System.getProperty( "topic_1", "topic_1");
    public static final String TOPIC_2 = System.getProperty( "topic_2", "topic_2");

    public static void main(String[] args) {

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(getPropsForProducer())) {
            // Отправляем сообщения в топик_1 и топик_2 - все хорошо
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_1, "key", "SASL/PLAIN");
            sendMessage(producer, record);

//            ProducerRecord<String, String> record2 = new ProducerRecord<>(TOPIC_2, "key", "SASL/PLAIN");
//            sendMessage(producer, record2);
            producer.flush();

        } catch (Throwable e) {
            System.out.println(e);
        }
    }


    private static void sendMessage(KafkaProducer<String, String> producer, ProducerRecord<String, String> record){

        producer.send(record, (metadata, exception) -> {
            System.out.println("something");
            if (exception == null) {
                System.out.println("Сообщение успешно отправлено в Kafka: " + metadata.toString());
                System.out.println(metadata);
            } else {
                System.out.println(exception);
            }
        });
    }

    private static Properties getPropsForProducer(){
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, CLUSTER_HOST);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Конфигурация SASL
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG,
                "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                        "username=\"admin\" password=\"admin-secret\";");
        return props;
    }
}