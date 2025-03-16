package ydx.practicum.app.config;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Reducer;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ydx.practicum.app.model.BlackListUser;
import ydx.practicum.app.model.BlockedUsers;
import ydx.practicum.app.model.Message;


@Configuration
public class MessageTopology {

    private final KafkaProperties kafkaProperties;
    private final MessageRoute messageRoute;
    private final MessageBlocker messageBlocker;
    private final MessageCense messageCense;

    public MessageTopology(KafkaProperties kafkaProperties, MessageRoute messageRoute, MessageBlocker messageBlocker, MessageCense messageCense) {
        this.kafkaProperties = kafkaProperties;
        this.messageRoute = messageRoute;
        this.messageBlocker = messageBlocker;
        this.messageCense = messageCense;
    }


    @Bean
    public KStream<String, Message> userMessagesStream(StreamsBuilder streamsBuilder) {


        // Создаем поток из топика входящих сообщений
        KStream<String, Message> stream = streamsBuilder.stream(kafkaProperties.getTopicForMessagesName());

        // Применяем обработчики
        stream = messageBlocker.blockMessages(stream);
        stream = messageCense.censeMessage(stream);
        stream = messageRoute.routeMessagesToUsers(stream);


        return stream;
    }
}
