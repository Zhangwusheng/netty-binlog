package com.zhangwusheng.binlog.nettyhandler;

import com.zhangwusheng.binlog.command.SetMasterBinlogChecksumCommand;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class FetchBinlogCheckRecordHandler extends MessageToMessageDecoder {
    
    private Logger log = LoggerFactory.getLogger ( FetchBinlogCheckRecordHandler.class );
    
    @Override
    protected void decode ( ChannelHandlerContext ctx, Object msg, List out ) throws Exception {
        if( msg instanceof FetchBinglogCheckRecord){
            FetchBinglogCheckRecord greetingPacket = (FetchBinglogCheckRecord) msg;
            log.info ( "FetchBinglogCheck:"+greetingPacket.toString () );
    
            ctx.pipeline ().remove ( this );
    
            SetMasterBinlogChecksumCommand checksumCommand = new SetMasterBinlogChecksumCommand ();
            ctx.channel ().writeAndFlush ( checksumCommand.toByteBuf () );
    
        }
    }
}
