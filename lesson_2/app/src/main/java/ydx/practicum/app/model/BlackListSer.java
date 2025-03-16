package ydx.practicum.app.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

public class BlackListSer implements Serializer<BlackListUser> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String s, BlackListUser blackListUser) {
        try {
            return objectMapper.writeValueAsBytes(blackListUser);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка при сериализации blackListUser", e);
        }
    }
}
