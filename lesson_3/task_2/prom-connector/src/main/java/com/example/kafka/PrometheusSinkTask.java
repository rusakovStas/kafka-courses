package com.example.kafka;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PrometheusSinkTask extends SinkTask {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private PrometheusHttpServer httpServer;


    @Override
    public String version() {
        return "";
    }


    @Override
    public void start(Map<String, String> map) { }

    @Override
    public void put(Collection<SinkRecord> records) { }

    @Override
    public void stop() { }
}
