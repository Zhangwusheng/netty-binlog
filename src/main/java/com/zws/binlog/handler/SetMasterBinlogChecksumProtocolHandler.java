package com.zws.binlog.handler;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class SetMasterBinlogChecksumProtocolHandler extends NonQueryPacketHandlerBase {
    
    private Logger log = LoggerFactory.getLogger ( SetMasterBinlogChecksumProtocolHandler.class );
    
    
    @Override
    protected void onParseFinish ( ChannelHandlerContext ctx, List< Object > out) {
        
        out.add ( okPacket );
        ctx.pipeline ().remove ( this );
     }
}
