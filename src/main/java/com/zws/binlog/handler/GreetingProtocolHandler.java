package com.zws.binlog.handler;

import com.zws.ByteUtil;
import com.zws.binlog.network.GreetingPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class GreetingProtocolHandler extends ByteToMessageDecoder {
    
    private Logger log = LoggerFactory.getLogger ( GreetingProtocolHandler.class );
    
    @Override
    protected void decode ( ChannelHandlerContext ctx, ByteBuf in, List< Object > out ) throws Exception {
        Object greetingPacket = decode ( ctx, in);
        
        if( greetingPacket != null ){
            out.add ( greetingPacket );
        }
    }
    
    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        if( buffer.readableBytes () < 4 )
            return null;
        
        buffer.markReaderIndex ();
        int packetLength = ByteUtil.readInteger ( buffer,3 );
        int sequence = ByteUtil.readInteger ( buffer,1 );
        
        log.info ( "packetLength="+packetLength+",sequence="+sequence );
        
        if( buffer.readableBytes () < packetLength){
            buffer.resetReaderIndex ();
            return null;
        }
    
        GreetingPacket greetingPacket = new GreetingPacket ();
        greetingPacket.parse ( buffer );
    
//        log.info ( greetingPacket.toString () );
        
        ctx.pipeline ().remove ( this );
        return greetingPacket;
    }
}
