package com.zhangwusheng.binlog.nettyhandler;

import com.zhangwusheng.binlog.command.FetchBinlogChecksumCommand;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class ShowMasterStatusRecordHandler extends MessageToMessageDecoder {
    
    private Logger log = LoggerFactory.getLogger ( ShowMasterStatusRecordHandler.class );
    
    @Override
    protected void decode ( ChannelHandlerContext ctx, Object msg, List out ) throws Exception {
        if( msg instanceof ShowMasterStatusRecord){
            ShowMasterStatusRecord greetingPacket = (ShowMasterStatusRecord) msg;
            log.info ( "ShowMasterStatus:"+greetingPacket.toString () );
    
           
    
//            ctx.channel ().writeAndFlush ( authenticateCommand.toByteBuf () );
            ctx.pipeline ().remove ( this );
    
            FetchBinlogChecksumCommand checksumCommand = new FetchBinlogChecksumCommand ();
            ctx.channel ().writeAndFlush ( checksumCommand.toByteBuf () );
    
        }
    }
}
