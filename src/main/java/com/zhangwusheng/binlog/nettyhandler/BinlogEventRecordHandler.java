package com.zhangwusheng.binlog.nettyhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class BinlogEventRecordHandler extends MessageToMessageDecoder {
    private Logger log = LoggerFactory.getLogger ( BinlogEventRecordHandler.class );
    
    
    protected void decode ( ChannelHandlerContext ctx, Object msg, List out ) throws Exception {
        if( msg instanceof BinlogEventData){
            BinlogEventData data = (BinlogEventData )msg;
            log.info (  data.toString ());
        }
    }
}
