package ydx.practicum.app.model;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

public class MessageSerdes extends Serdes.WrapperSerde<Message> {

    public MessageSerdes() {
        super(new MessageSereilizer(), new MessageDeserializer());
    }
}
