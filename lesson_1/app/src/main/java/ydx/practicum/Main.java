package ydx.practicum;

import ydx.practicum.consumer.PullConsumer;
import ydx.practicum.consumer.PushConsumer;
import ydx.practicum.model.Message;
import ydx.practicum.producer.MyProducer;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static final String CLUSTER_HOST = System.getProperty("bootstrap.servers", "localhost:9094,localhost:9095,localhost:9096");
    public static final String TOPIC = System.getProperty( "topic", "lesson1-topic");
    public static final String CONSUMER_GROUP_1 = "lesson1-consumer-group-1";
    public static final String CONSUMER_GROUP_2 = "lesson1-consumer-group-2";

    public static void main(String[] args) {
        var producerExecutor = Executors.newScheduledThreadPool(1);
        var producer = new MyProducer(CLUSTER_HOST);
        producerExecutor.scheduleAtFixedRate(() -> {
            producer.sendMessage(TOPIC, 3, new Message());
        }, 0, 1, TimeUnit.SECONDS);


        var pullConsumerExecutor = Executors.newScheduledThreadPool(1);
        // помещаем консьюмеры в разные группы, что бы они независимо получили одни и теже сообщения
        var pullConsumer = new PullConsumer(CLUSTER_HOST, CONSUMER_GROUP_1, TOPIC);
        pullConsumerExecutor.scheduleAtFixedRate(() -> {
            //считываем пачку сообщений каждые 10 секунд
            pullConsumer.pull(record -> System.out.println("Pull consumer got message " + record.value()));
        }, 0, 10, TimeUnit.SECONDS);

        var pushConsumerExecutor = Executors.newSingleThreadExecutor();
        var pushConsumer = new PushConsumer(CLUSTER_HOST, CONSUMER_GROUP_2);
        pushConsumerExecutor.submit(() -> {
            //подписываемся на топик и реагируем как только поступает сообщение
            pushConsumer.subscribe(TOPIC, (record) -> {
                System.out.println("Push consumer got message " + record.value());
            });
        });
    }
}