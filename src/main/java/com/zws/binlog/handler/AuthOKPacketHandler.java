package com.zws.binlog.handler;

import com.zws.binlog.command.ShowMasterStatusCommand;
import com.zws.binlog.network.OKPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class AuthOKPacketHandler extends MessageToMessageDecoder {
    
    private Logger log = LoggerFactory.getLogger ( AuthOKPacketHandler.class );
    
    @Override
    protected void decode ( ChannelHandlerContext ctx, Object msg, List out ) throws Exception {
        if( msg instanceof OKPacket){
            OKPacket okPacket = (OKPacket) msg;
            log.info ( "Auth OKPacket:"+okPacket.toString () );
            
            ShowMasterStatusCommand showMasterStatusCommand = new ShowMasterStatusCommand ();
            ctx.channel ().writeAndFlush ( showMasterStatusCommand.toByteBuf () );
            ctx.pipeline ().remove ( this );
        }
    }
}
