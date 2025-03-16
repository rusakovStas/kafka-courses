package ydx.practicum.app.model;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

public class BlackListSerdes extends Serdes.WrapperSerde<BlackListUser> {
    public BlackListSerdes() {
        super(new BlackListSer(), new BlackListDes());
    }
}
