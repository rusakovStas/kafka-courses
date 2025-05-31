package ydx.practicum;

import ydx.practicum.consumer.PullConsumer;
import ydx.practicum.consumer.PushConsumer;
import ydx.practicum.model.Message;
import ydx.practicum.producer.MyProducer;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static final String CLUSTER_HOST = System.getProperty("bootstrap.servers", "localhost:9094,localhost:9095,localhost:9096");
    public static final String TOPIC = System.getProperty( "topic", "lesson1-topic");
    public static final String CONSUMER_GROUP_1 = "lesson1-consumer-group-1";
    public static final String CONSUMER_GROUP_2 = "lesson1-consumer-group-2";

    public static void main(String[] args) {

    }
}