package ydx.practicum.app.config;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.stereotype.Component;
import ydx.practicum.app.model.Message;

import java.util.Set;

@Component
public class MessageCense {

    private final KafkaProperties kafkaProperties;

    public MessageCense(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }


    public KStream<String, Message> censeMessage(KStream<String, Message> stream) {

        return stream.process(() -> new Processor<String, Message, String, Message>() {
            private ReadOnlyKeyValueStore<String, Set<String>> store;

            @Override
            public void init(ProcessorContext<String, Message> context) {
                store = context.getStateStore(kafkaProperties.getStoreForBlacklistWord());
            }

            @Override
            public void process(Record<String, Message> record) {
                Set<String> bannedWords = store.get(kafkaProperties.getKeyForBlacklistWords());
                System.out.println("bannedWords "+ String.join(",", bannedWords));
                Message message = record.value();
                String content = message.getMessage();
                for (String word : bannedWords) {
                    content = content.replaceAll(word, "***");
                }
                message.setMessage(content);
            }
        }, kafkaProperties.getStoreForBlacklistWord());
    }
}
