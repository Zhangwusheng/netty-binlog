package com.zws.binlog.handler;

import com.zws.ByteUtil;
import com.zws.binlog.event.Event;
import com.zws.binlog.event.EventHeaderV4;
import com.zws.binlog.event.EventType;
import com.zws.binlog.event.deserialization.EventDeserializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class BinlogEventProtocolHandler extends ByteToMessageDecoder {
    
    private enum State {
        BEGIN,
        HEADER,
        DATA,
        END
    }
    
    private State currentState = State.BEGIN;
    private static final EventType[] EVENT_TYPES = EventType.values ( );
    
    private Logger log = LoggerFactory.getLogger ( BinlogEventProtocolHandler.class );
    
    
    protected void decode ( ChannelHandlerContext ctx, ByteBuf msg, List< Object > out ) throws Exception {
        decode1 ( ctx, msg, out );
    }
    
    int packetLength;
    int sequence;

    EventDeserializer deserializer = new EventDeserializer ();
    
    protected void decode1 ( ChannelHandlerContext ctx, ByteBuf msg, List< Object > out ) throws Exception {
        
        msg.markReaderIndex ( );
        if ( msg.readableBytes ( ) < 4 ) {
            msg.resetReaderIndex ( );
            return;
        } else {
            packetLength = ByteUtil.readInteger ( msg, 3 );
            sequence = ByteUtil.readInteger ( msg, 1 );
            if ( msg.readableBytes ( ) < packetLength ) {
                msg.resetReaderIndex ( );
                return;
            }
        }
        
        ByteBuf dataBuffer = msg.readSlice ( packetLength );
    
        //1字节的marker。这个Marker在mysql协议设置了非阻塞之后，会发送FE记录结束指令
        //此处我们不处理，但是需要跳过这个字节。
        ByteUtil.readUnsignedInt ( dataBuffer, 1 );
    
        Event event = deserializer.decodeHeader ( dataBuffer ).decodeEventData ( dataBuffer ).buildEvent ();
        
        out.add ( event );
        
    }
}

