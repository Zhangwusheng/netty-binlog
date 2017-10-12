package com.zws.binlog.handler;

import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class SetMasterBinlogChecksumProtocolHandler extends NonQueryPacketHandlerBase {
    
    @Override
    protected void onParseFinish ( ChannelHandlerContext ctx, List< Object > out) {
        
        out.add ( okPacket );
        ctx.pipeline ().remove ( this );
     }
}
