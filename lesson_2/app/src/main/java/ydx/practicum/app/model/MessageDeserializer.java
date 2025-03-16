package ydx.practicum.app.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.io.InputStream;

public class MessageDeserializer implements Deserializer<Message> {
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public Message deserialize(String s, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, Message.class);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при десериализации UserMessage", e);
        }
    }
}
