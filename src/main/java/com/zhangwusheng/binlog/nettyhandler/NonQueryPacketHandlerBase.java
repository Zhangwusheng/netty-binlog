package com.zhangwusheng.binlog.nettyhandler;

import com.zhangwusheng.ByteUtil;
import com.zhangwusheng.binlog.network.ColumnDefinitionPacket;
import com.zhangwusheng.binlog.network.EofPacket;
import com.zhangwusheng.binlog.network.OKPacket;
import com.zhangwusheng.binlog.network.RowPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class NonQueryPacketHandlerBase extends ByteToMessageDecoder {
    
    private Logger log = LoggerFactory.getLogger ( NonQueryPacketHandlerBase.class );
    
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
//        out.add ( okPacket );
    
        onParseFinish(ctx,out);
    }
}
