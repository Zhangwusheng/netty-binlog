package com.zhangwusheng.binlog.nettyhandler;

import com.zhangwusheng.binlog.GtidSet;
import com.zhangwusheng.binlog.command.DumpBinaryLogCommand;
import com.zhangwusheng.binlog.command.DumpBinaryLogGitdCommand;
import com.zhangwusheng.binlog.network.OKPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class SetMasterBinlogChecksumRecordHandler extends MessageToMessageDecoder {
    
    private Logger log = LoggerFactory.getLogger ( SetMasterBinlogChecksumRecordHandler.class );
    
    @Override
    protected void decode ( ChannelHandlerContext ctx, Object msg, List out ) throws Exception {
        if( msg instanceof OKPacket){
            OKPacket greetingPacket = (OKPacket) msg;
            log.info ( "OKPacket:"+greetingPacket.toString () );
            
            ctx.pipeline ().remove ( this );
        }
    
        DumpBinaryLogCommand queryCommand =
                new DumpBinaryLogCommand (1,"mysql-bin.000002",4 );
        ctx.channel ().writeAndFlush ( queryCommand.toByteBuf () );

        GtidSet gtidSet = new GtidSet( "584416c8-a84b-11e7-b641-74e50bc69d0a:1-4");
//        DumpBinaryLogGitdCommand dumpBinaryLogGitdCommand = new DumpBinaryLogGitdCommand(
//                2,"mysql-bin.000002",4,gtidSet);
        DumpBinaryLogGitdCommand dumpBinaryLogGitdCommand = new DumpBinaryLogGitdCommand(
                2,"mysql-bin.000002",1119,gtidSet);
    
        ByteBuf byteBuf = dumpBinaryLogGitdCommand.toByteBuf ();

        String debug = ByteBufUtil.prettyHexDump(byteBuf);
//        log.info(debug);
//        ctx.channel ().writeAndFlush (byteBuf );

      }
}
