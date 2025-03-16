package ydx.practicum.app.config;


import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerde;
import ydx.practicum.app.model.BlackListDes;
import ydx.practicum.app.model.BlackListSer;
import ydx.practicum.app.model.BlackListUser;
import ydx.practicum.app.model.BlockedUsers;
import ydx.practicum.app.model.Message;
import ydx.practicum.app.model.MessageDeserializer;
import ydx.practicum.app.model.MessageSerdes;
import ydx.practicum.app.model.MessageSereilizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Configuration
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    public KafkaConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public AdminClient adminClient() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        return AdminClient.create(props);
    }

    @Bean
    public ProducerFactory<String, Message> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageSereilizer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ProducerFactory<String, BlackListUser> producerFactoryBlackListUser() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, BlackListSer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ProducerFactory<String, String> producerFactoryBlackListWord() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, BlackListUser> kafkaTemplateBlackListUser() {
        return new KafkaTemplate<>(producerFactoryBlackListUser());
    }

    @Bean
    public KafkaTemplate<String, Message> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplateBlackListWord() {
        return new KafkaTemplate<>(producerFactoryBlackListWord());
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kafkaStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "user-messages-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, MessageSerdes.class.getName());
        return new KafkaStreamsConfiguration(props);
    }

    /*
     * Создаем поток из топика в который кладем заблокированных юзеров
     * */
    @Bean
    public KTable<String, BlockedUsers> blackListUserStream(StreamsBuilder streamsBuilder) {
        //создаем поток из топика сообщений о запрещенных словах
        KStream<String, String> streamForBlackListUser = streamsBuilder.stream(
                kafkaProperties.getTopicForBlockUser(),
                Consumed.with(Serdes.String(), Serdes.String())
        );
        return streamForBlackListUser
                //групируем по ключу - то бишь по юзеру
                .groupByKey()
                //собираем всех юзеров которых пользователь кинул в блэклист
                .aggregate(BlockedUsers::new, (userFrom, userTo, blackList) -> {
                    blackList.addUser(userTo);
                    return blackList;
                }, Materialized.<String, BlockedUsers, KeyValueStore<Bytes, byte[]>>as(kafkaProperties.getStoreForBlacklistUser())
                        .withKeySerde(Serdes.String())
                        .withValueSerde(new JsonSerde<>(BlockedUsers.class)));

    }

    /*
     * Создаем поток из топика в который кладем запрещенные слова
     * */
    @Bean
    public KTable<String, Set<String>> blackListWordsStream(StreamsBuilder streamsBuilder) {
        //создаем поток из топика сообщений о запрещенных словах
        KStream<String, String> streamForBlackListWords = streamsBuilder.stream(
                kafkaProperties.getTopicForBlacklistWord(),
                Consumed.with(Serdes.String(), Serdes.String())
        );

        return streamForBlackListWords
                .groupByKey()
                .aggregate(
                        HashSet::new,
                        (key, value, aggregate) -> {
                            System.out.println("собираем блокировочные слова "+ value);
                            aggregate.add(value);
                            System.out.println("собираем блокировочные слова "+ String.join(",", aggregate));
                            return aggregate;
                        },
                        Materialized.<String, Set<String>, KeyValueStore<Bytes, byte[]>>as(kafkaProperties.getStoreForBlacklistWord())
                                .withKeySerde(Serdes.String())
                                .withValueSerde(new JsonSerde<>(Set.class))
                );
    }
}
