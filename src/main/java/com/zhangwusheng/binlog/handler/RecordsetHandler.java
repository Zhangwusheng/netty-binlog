package com.zhangwusheng.binlog.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangwusheng on 17/10/4.
 */
public class RecordsetHandler  extends SimpleChannelInboundHandler<ByteBuf> {
    private Logger log = LoggerFactory.getLogger ( GreetingPacketResultHandler.class );
    
    
    
    protected void channelRead0 ( ChannelHandlerContext ctx, ByteBuf msg ) throws Exception {
        String debugString = ByteBufUtil.prettyHexDump ( msg );
        
        log.info ( debugString );
    }
}
