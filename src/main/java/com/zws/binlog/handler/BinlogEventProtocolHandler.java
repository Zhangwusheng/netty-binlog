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
        
//        String debug = ByteBufUtil.prettyHexDump ( msg );
//        log.info ( debug );
        decode1 ( ctx, msg, out );
        
//        if ( currentState == State.END ) {
//            BinlogEventData data = new BinlogEventData ( );
//            data.setHeader ( this.header );
//            out.add ( data );
//
//            currentState = State.BEGIN;
//        }
        
    }
    
    int packetLength;
    int sequence;
    EventHeaderV4 header;
    Event event = null;
    EventDeserializer deserializer = new EventDeserializer ();

//    protected void decode0 ( ChannelHandlerContext ctx, ByteBuf msg, List< Object > out ) throws Exception {
//
//        if ( currentState == State.BEGIN ) {
//            currentState = State.HEADER;
//        }
//        msg.markReaderIndex ( );
//        if ( msg.readableBytes ( ) < 4 ) {
//            msg.resetReaderIndex ( );
//            return;
//        } else {
//            packetLength = ByteUtil.readInteger ( msg, 3 );
//            sequence = ByteUtil.readInteger ( msg, 1 );
//            if ( msg.readableBytes ( ) < packetLength ) {
//                msg.resetReaderIndex ( );
//                return;
//            }
//        }
//
//        String debug = ByteBufUtil.prettyHexDump ( msg );
//
//        if ( currentState == State.HEADER ) {
//            int marker = ByteUtil.readUnsignedInt ( msg, 1 );
//
//            header = new EventHeaderV4 ( );
//            long timestamp = ByteUtil.readUnsignedLong ( msg, 4 );
//            header.setTimestamp ( timestamp * 1000L );
//            int type = ByteUtil.readUnsignedInt ( msg, 1 );
//            header.setEventType ( EVENT_TYPES[ type ] );
//            long serverId = ByteUtil.readUnsignedLong ( msg, 4 );
//            header.setServerId ( serverId );
//            long length = ByteUtil.readUnsignedLong ( msg, 4 );
//            header.setEventLength ( length );
//            long nextPos = ByteUtil.readUnsignedLong ( msg, 4 );
//            header.setNextPosition ( nextPos );
//            int flag = ByteUtil.readUnsignedInt ( msg, 2 );
//            header.setFlag ( flag );
//
//            log.info ( header.toString ( ) );
//
//            currentState = State.DATA;
//        } else if ( currentState == State.DATA ) {
//            msg.skipBytes ( ( int ) header.getEventLength ( ) );
//            currentState = State.END;
//        }
//    }
    
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
    
        ByteUtil.prettyPrint ( dataBuffer,log );
    
        
        int marker = ByteUtil.readUnsignedInt ( dataBuffer, 1 );
    
         event = deserializer.decodeHeader ( dataBuffer ).decodeEventData ( dataBuffer ).buildEvent ();
//        log.info ( event.toString () );
        
        out.add ( event );
//        header = new EventHeaderV4 ( );
//        long timestamp = ByteUtil.readUnsignedLong ( dataBuffer, 4 );
//        header.setTimestamp ( timestamp * 1000L );
//        int type = ByteUtil.readUnsignedInt ( dataBuffer, 1 );
//        header.setEventType ( EVENT_TYPES[ type ] );
//        long serverId = ByteUtil.readUnsignedLong ( dataBuffer, 4 );
//        header.setServerId ( serverId );
//        long length = ByteUtil.readUnsignedLong ( dataBuffer, 4 );
//        header.setEventLength ( length );
//        long nextPos = ByteUtil.readUnsignedLong ( dataBuffer, 4 );
//        header.setNextPosition ( nextPos );
//        int flag = ByteUtil.readUnsignedInt ( dataBuffer, 2 );
//        header.setFlag ( flag );
        
//        log.info ( header.toString ( ) );
        
//        log.info ("dataBuffer.readableBytes ()="
//                + dataBuffer.readableBytes ()
//        +",event length="+length+",total-header="+(packetLength-20));

//        currentState = State.END;
        
    }
}

