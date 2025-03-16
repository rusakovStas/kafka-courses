package ydx.practicum.app.config;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.RecordContext;
import org.apache.kafka.streams.processor.TopicNameExtractor;
import org.springframework.stereotype.Component;
import ydx.practicum.app.model.Message;
import ydx.practicum.app.service.KafkaTopicCreator;

@Component
public class MessageRoute {
    private final KafkaTopicCreator kafkaTopicCreator;

    public MessageRoute(KafkaTopicCreator kafkaTopicCreator) {
        this.kafkaTopicCreator = kafkaTopicCreator;
    }

    public KStream<String, Message> routeMessagesToUsers(KStream<String, Message> stream) {
        stream
                .peek((userTo, message) -> System.out.println("Сообщение готово зароутиться"))
                .peek((userTo, message) -> kafkaTopicCreator.createTopicIfNotExist("messages-for-"+userTo))
                .to((userTo, message, recordContext) -> "messages-for-"+userTo);
        return stream;
    }
}
