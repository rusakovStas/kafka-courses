package com.example.kafka;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

import java.util.List;
import java.util.Map;

public class PrometheusSinkConnector extends SinkConnector {

    private String prometheusUrl;
    private String prometheusPort;


    @Override
    public String version() {
        return "";
    }

    @Override
    public void start(Map<String, String> props) { }

    @Override
    public Class<? extends Task> taskClass() {
        return null;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        return List.of();
    }

    @Override
    public void stop() {
        // Завершение работы, если требуется
    }

    @Override
    public ConfigDef config() {
        // Определяем параметры конфигурации
        return new ConfigDef();
    }
}
