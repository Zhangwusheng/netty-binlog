package com.zws.binlog.handler;

import com.zws.ByteUtil;
import com.zws.binlog.network.EofPacket;
import com.zws.binlog.network.OKPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class NonQueryPacketHandlerBase extends ByteToMessageDecoder {
    
    protected OKPacket okPacket;
    protected EofPacket eofPacket;
    
    int packetLength;
    int sequence;
    
    protected void onParseFinish(ChannelHandlerContext ctx, List< Object > out){}
    
    protected void decode ( ChannelHandlerContext ctx, ByteBuf msg, List< Object > out ) throws Exception {
        msg.markReaderIndex ();
        if( msg.readableBytes () < 4 ) {
            msg.resetReaderIndex ();
            return;
        }else{
            packetLength= ByteUtil.readInteger ( msg,3 );
            sequence = ByteUtil.readInteger ( msg,1 );
            if( msg.readableBytes () < packetLength){
                msg.resetReaderIndex ();
                return;
            }
        }
        
        this.okPacket = new OKPacket ();
        okPacket.parse ( msg );
        
        onParseFinish(ctx,out);
    }
}
