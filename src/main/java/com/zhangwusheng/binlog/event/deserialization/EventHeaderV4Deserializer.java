package com.zhangwusheng.binlog.event.deserialization;

import com.zhangwusheng.ByteUtil;
import com.zhangwusheng.binlog.event.EventHeaderV4;
import com.zhangwusheng.binlog.event.EventType;
import io.netty.buffer.ByteBuf;

//import java.io.IOException;

/**
 * Created by zhangwusheng on 17/10/11.
 */
public class EventHeaderV4Deserializer implements EventHeaderDeserializer<EventHeaderV4 > {
    
    private static final EventType[] EVENT_TYPES = EventType.values ( );
    
    public EventHeaderV4 deserialize ( ByteBuf msg ) {
    
        EventHeaderV4 header = new EventHeaderV4 ( );
        long timestamp = ByteUtil.readUnsignedLong ( msg, 4 );
        header.setTimestamp ( timestamp * 1000L );
        int type = ByteUtil.readUnsignedInt ( msg, 1 );
        header.setEventType ( EVENT_TYPES[ type ] );
        long serverId = ByteUtil.readUnsignedLong ( msg, 4 );
        header.setServerId ( serverId );
        long length = ByteUtil.readUnsignedLong ( msg, 4 );
        header.setEventLength ( length );
        long nextPos = ByteUtil.readUnsignedLong ( msg, 4 );
        header.setNextPosition ( nextPos );
        int flag = ByteUtil.readUnsignedInt ( msg, 2 );
        header.setFlag ( flag );
    
//        log.info ( header.toString ( ) );
    
    
        return header;
    }
}
