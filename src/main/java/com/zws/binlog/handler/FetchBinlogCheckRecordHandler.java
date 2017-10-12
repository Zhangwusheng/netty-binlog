package com.zws.binlog.handler;

import com.zws.binlog.command.SetMasterBinlogChecksumCommand;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class FetchBinlogCheckRecordHandler extends MessageToMessageDecoder {
    
    @Override
    protected void decode ( ChannelHandlerContext ctx, Object msg, List out ) throws Exception {
        if( msg instanceof FetchBinglogCheckRecord){
            
            ctx.pipeline ().remove ( this );
    
            SetMasterBinlogChecksumCommand checksumCommand = new SetMasterBinlogChecksumCommand ();
            ctx.channel ().writeAndFlush ( checksumCommand.toByteBuf () );
    
        }
    }
}
