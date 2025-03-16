package ydx.practicum.app.config;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.stereotype.Component;
import ydx.practicum.app.model.Message;

import java.util.Set;

@Component
public class MessageCense {

    private final KafkaProperties kafkaProperties;
    private final KTable<String, Set<String>> bannedWords;

    public MessageCense(KafkaProperties kafkaProperties, KTable<String, Set<String>> bannedWords) {
        this.kafkaProperties = kafkaProperties;
        this.bannedWords = bannedWords;
    }


    public KStream<String, Message> censeMessage(KStream<String, Message> stream) {
        stream.process(() -> new Processor<String, Message, String, Message>() {
            private ProcessorContext<String, Message> context;
            private KeyValueStore<String, Set<String>> store;

            @Override
            public void init(ProcessorContext<String, Message> context) {
                this.context = context;
                this.store = context.getStateStore(kafkaProperties.getStoreForBlacklistWord());
            }

            @Override
            public void process(Record<String, Message> record) {
                // TODO: не работает. Уже не понимаю почему
                Set<String> bannedWords = store.get(kafkaProperties.getKeyForBlacklistWords());
                Message message = record.value();
                bannedWords.forEach((word) -> {
                    message.setMessage(message.getMessage().replace(word, "***"));
                });
                context.forward(record.withValue(message));
            }
        });
        return stream;
    }
}
