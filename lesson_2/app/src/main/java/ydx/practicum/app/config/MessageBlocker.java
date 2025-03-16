package ydx.practicum.app.config;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.stereotype.Component;
import ydx.practicum.app.model.BlockedUsers;
import ydx.practicum.app.model.Message;

@Component
public class MessageBlocker {


    private final KTable<String, BlockedUsers> storeForBlacklistUser;

    public MessageBlocker(KTable<String, BlockedUsers> storeForBlacklistUser) {
        this.storeForBlacklistUser = storeForBlacklistUser;
    }


    public KStream<String, Message> blockMessages(KStream<String, Message> stream) {

        // если пользователь указан как заблокированный, то такие сообщения не нужно роутить
        return stream.leftJoin(storeForBlacklistUser, (message, blacklist) -> {
                    System.out.println("сообщение " + message.getMessage());
                    System.out.println("блэклист "+ blacklist);
                    if (blacklist != null && blacklist.userIsBlocked(message.getUserFrom())) {
                        return null;
                    }
                    return message;
                })
                .peek((userTo, message) -> System.out.println("Сообщение от " + userTo + " проверяется на блокировку"))
                .filter((user, message) -> message != null)
                .peek((userTo, message) -> System.out.println("Сообщение от " + userTo + " прошло блокировку"));
    }
}
