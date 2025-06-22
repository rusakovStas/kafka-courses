package ydx.practicum;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.acl.AccessControlEntry;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourcePattern;
import org.apache.kafka.common.resource.ResourceType;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static final String CLUSTER_HOST = System.getProperty("bootstrap.servers", "localhost:9093,localhost:9095,localhost:9097");
    public static final String TOPIC_1 = System.getProperty("topic_1", "topic_1");
    public static final String TOPIC_2 = System.getProperty("topic_2", "topic_2");

    public static void main(String[] args) throws ExecutionException, IOException {
        setUpAcl();

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(getPropsForProducer())) {
            // Отправляем сообщения в топик_1 и топик_2 - все хорошо
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_1, "key", "SASL/PLAIN");
            sendMessage(producer, record);

            ProducerRecord<String, String> record2 = new ProducerRecord<>(TOPIC_2, "key", "SASL/PLAIN");
            sendMessage(producer, record2);
            producer.flush();

        } catch (Throwable e) {
            System.out.println(e);
        }
    }


    private static void sendMessage(KafkaProducer<String, String> producer, ProducerRecord<String, String> record) {

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

    private static Properties getAdminProps() throws IOException {
        Properties props = new Properties();
        props.put("ssl.truststore.location", extractTruststoreToTemp());
        props.put("ssl.truststore.password", "password");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
        props.put("sasl.mechanism", "SSL");
        props.put("sasl.jaas.config",
                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"admin\" password=\"admin-secret\";");
        return props;
    }

    private static void setUpAcl() throws ExecutionException, IOException {
        Properties props = getAdminProps();

        try (AdminClient admin = AdminClient.create(props)) {

            // ===== 1. Создание топиков =====

            NewTopic topic1 = new NewTopic("topic_1", 3, (short) 3);
            NewTopic topic2 = new NewTopic("topic_2", 3, (short) 3);

            // Если кластер single-node, используйте (short) 1 — replication-factor=1

            try {
                admin.createTopics(Arrays.asList(topic1, topic2)).all().get();
                System.out.println("Topics created");
                // ===== 2. ACLs на topic_1 ====
                // --add --allow-principal "User:*" --operation All --topic topic_1 --group "*"
                AclBinding acl1 = new AclBinding(
                        new ResourcePattern(ResourceType.TOPIC, "topic_1", PatternType.LITERAL),
                        new AccessControlEntry("User:*", "*", AclOperation.ALL, AclPermissionType.ALLOW)
                );
                AclBinding acl2 = new AclBinding(
                        new ResourcePattern(ResourceType.GROUP, "*", PatternType.LITERAL),
                        new AccessControlEntry("User:*", "*", AclOperation.ALL, AclPermissionType.ALLOW)
                );

                // ==== 3. ACLs на topic_2 ====
                // --add --allow-principal "User:*" --operation Write --topic topic_2
                AclBinding acl3 = new AclBinding(
                        new ResourcePattern(ResourceType.TOPIC, "topic_2", PatternType.LITERAL),
                        new AccessControlEntry("User:*", "*", AclOperation.WRITE, AclPermissionType.ALLOW)
                );
                // --add --deny-principal "User:*" --operation Read --topic topic_2 --group "*"
                AclBinding acl4 = new AclBinding(
                        new ResourcePattern(ResourceType.TOPIC, "topic_2", PatternType.LITERAL),
                        new AccessControlEntry("User:*", "*", AclOperation.READ, AclPermissionType.DENY)
                );
                AclBinding acl5 = new AclBinding(
                        new ResourcePattern(ResourceType.GROUP, "*", PatternType.LITERAL),
                        new AccessControlEntry("User:*", "*", AclOperation.READ, AclPermissionType.DENY)
                );

                // Добавляем все ACL
                admin.createAcls(Arrays.asList(acl1, acl2, acl3, acl4, acl5)).all().get();
                System.out.println("ACLs created");
            } catch (ExecutionException e) {
                if (e.getCause() instanceof org.apache.kafka.common.errors.TopicExistsException) {
                    System.out.println("Topics already exist.");
                } else {
                    throw e;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

private static String extractTruststoreToTemp() throws IOException {
    InputStream resource = Main.class.getClassLoader().getResourceAsStream("client.truststore.jks");
    if (resource == null) throw new FileNotFoundException("Truststore not found in resources");
    Path tempFile = Files.createTempFile("client", ".truststore.jks");
    tempFile.toFile().deleteOnExit();
    Files.copy(resource, tempFile, StandardCopyOption.REPLACE_EXISTING);
    return tempFile.toAbsolutePath().toString();
}

private static Properties getPropsForProducer() throws IOException {
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, CLUSTER_HOST);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

    // Конфигурация SASL
    props.put("ssl.truststore.location", extractTruststoreToTemp());
    props.put("ssl.truststore.password", "password");
    props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
    props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
    props.put(SaslConfigs.SASL_JAAS_CONFIG,
            "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                    "username=\"admin\" password=\"admin-secret\";");
    return props;
}
}