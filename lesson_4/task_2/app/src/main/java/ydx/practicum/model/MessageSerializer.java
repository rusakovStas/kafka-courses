package ydx.practicum.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;


public class MessageSerializer implements Serializer<Message> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String s, Message message) {
        try {
            if (message == null) {
                return null;
            }
            return objectMapper.writeValueAsBytes(message);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing Message", e);
        }
    }
}
