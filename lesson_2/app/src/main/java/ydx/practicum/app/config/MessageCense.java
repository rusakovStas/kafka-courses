package ydx.practicum.app.config;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;
import org.springframework.stereotype.Component;
import ydx.practicum.app.model.Message;

import java.util.Set;
import java.util.function.Consumer;

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
            private KeyValueStore<String, ValueAndTimestamp<Set<String>>> store;

            @Override
            public void init(ProcessorContext<String, Message> context) {
                this.context = context;
                //  этот ValueAndTimestamp - вообще не понятно откуда вылез
                this.store = (KeyValueStore<String, ValueAndTimestamp<Set<String>>>) context.getStateStore(kafkaProperties.getStoreForBlacklistWord());
            }

            @Override
            public void process(Record<String, Message> record) {
                Message message = record.value();
                store.all().forEachRemaining(stringSetKeyValue -> stringSetKeyValue.value.value().forEach((word) -> {
                    message.setMessage(message.getMessage().replace(word, "***"));
                }));
                context.forward(record.withValue(message));
            }
        }, kafkaProperties.getStoreForBlacklistWord());
        return stream;
    }
}
