package ydx.practicum.app.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ydx.practicum.app.config.KafkaProperties;
import ydx.practicum.app.model.BlackListUser;

@Service
public class BlacklistWordService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public BlacklistWordService(KafkaTemplate<String, String> kafkaTemplate, KafkaProperties kafkaProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
    }

    public void blockWord(String word){
        kafkaTemplate.send(kafkaProperties.getTopicForBlacklistWord(), kafkaProperties.getKeyForBlacklistWords(), word);
    }
}
