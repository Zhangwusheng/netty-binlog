package com.zws.binlog.handler;

import com.zws.ByteUtil;
import com.zws.binlog.network.OKPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class AuthProtocolHandler extends ByteToMessageDecoder {
    
    private Logger log = LoggerFactory.getLogger ( AuthProtocolHandler.class );
    
    @Override
    protected void decode ( ChannelHandlerContext ctx, ByteBuf in, List< Object > out ) throws Exception {
        Object okPacket = decode ( ctx, in);
        
        if( okPacket != null ){
            out.add ( okPacket );
        }
    }
    
    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        if( buffer.readableBytes () < 4 )
            return null;
        
        buffer.markReaderIndex ();
        int packetLength = ByteUtil.readInteger ( buffer,3 );
        int sequence = ByteUtil.readInteger ( buffer,1 );
        
        
        if( buffer.readableBytes () < packetLength){
            buffer.resetReaderIndex ();
            return null;
        }
    
        OKPacket okPacket = new OKPacket ();
        okPacket.parse ( buffer );
    
        ctx.pipeline ().remove ( this );
        return okPacket;
    }
}
