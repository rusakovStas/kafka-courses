package ydx.practicum.app.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class BlackListDes implements Deserializer<BlackListUser> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public BlackListUser deserialize(String s, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, BlackListUser.class);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при десериализации Blacklist", e);
        }
    }
}
