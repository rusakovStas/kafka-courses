package ydx.practicum.app.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ydx.practicum.app.config.KafkaProperties;
import ydx.practicum.app.model.BlackListUser;
import ydx.practicum.app.model.Message;

@Service
public class BlockUserService {

    private final KafkaTemplate<String, BlackListUser> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public BlockUserService(KafkaTemplate<String, BlackListUser> kafkaTemplate, KafkaProperties kafkaProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
    }

    public void blockUser(String actor, String userToBlock){
        BlackListUser blackListUpdate = new BlackListUser(actor, userToBlock);
        kafkaTemplate.send(kafkaProperties.getTopicForBlockUser(), actor, blackListUpdate);
    }


}
