package ydx.practicum.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("kafka")
public class KafkaProperties {

    private String topicForMessagesName;
    private String topicForBlockUser;
    private String topicForBlacklistWord;
    private int partitions;
    private short replicationFactor;
    private String bootstrapServers;
    private String storeForBlacklistUser;
    private String storeForBlacklistWord;
    private String topicForBlockedMessages;
    private String keyForBlacklistWords;

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public short getReplicationFactor() {
        return replicationFactor;
    }

    public void setReplicationFactor(short replicationFactor) {
        this.replicationFactor = replicationFactor;
    }

    public int getPartitions() {
        return partitions;
    }

    public void setPartitions(int partitions) {
        this.partitions = partitions;
    }

    public String getTopicForMessagesName() {
        return topicForMessagesName;
    }

    public void setTopicForMessagesName(String topicForMessagesName) {
        this.topicForMessagesName = topicForMessagesName;
    }

    public String getTopicForBlacklistWord() {
        return topicForBlacklistWord;
    }

    public void setTopicForBlacklistWord(String topicForBlacklistWord) {
        this.topicForBlacklistWord = topicForBlacklistWord;
    }

    public String getTopicForBlockUser() {
        return topicForBlockUser;
    }

    public void setTopicForBlockUser(String topicForBlockUser) {
        this.topicForBlockUser = topicForBlockUser;
    }

    public String getStoreForBlacklistUser() {
        return storeForBlacklistUser;
    }

    public void setStoreForBlacklistUser(String storeForBlacklistUser) {
        this.storeForBlacklistUser = storeForBlacklistUser;
    }

    public String getStoreForBlacklistWord() {
        return storeForBlacklistWord;
    }

    public void setStoreForBlacklistWord(String storeForBlacklistWord) {
        this.storeForBlacklistWord = storeForBlacklistWord;
    }

    public String getTopicForBlockedMessages() {
        return topicForBlockedMessages;
    }

    public void setTopicForBlockedMessages(String topicForBlockedMessages) {
        this.topicForBlockedMessages = topicForBlockedMessages;
    }

    public String getKeyForBlacklistWords() {
        return keyForBlacklistWords;
    }

    public void setKeyForBlacklistWords(String keyForBlacklistWords) {
        this.keyForBlacklistWords = keyForBlacklistWords;
    }
}
