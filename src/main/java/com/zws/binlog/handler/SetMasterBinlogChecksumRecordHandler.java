package com.zws.binlog.handler;

import com.zws.binlog.event.GtidSet;
import com.zws.binlog.command.DumpBinaryLogCommand;
import com.zws.binlog.command.DumpBinaryLogGitdCommand;
import com.zws.binlog.network.OKPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class SetMasterBinlogChecksumRecordHandler extends MessageToMessageDecoder {
    
    @Override
    protected void decode ( ChannelHandlerContext ctx, Object msg, List out ) throws Exception {
        
        if( msg instanceof OKPacket){
//            OKPacket greetingPacket = (OKPacket) msg;
            ctx.pipeline ().remove ( this );
        }
    
        DumpBinaryLogCommand queryCommand =
                new DumpBinaryLogCommand (1,"mysql-bin.000002",4 );
//        ctx.channel ().writeAndFlush ( queryCommand.toByteBuf () );

        GtidSet gtidSet = new GtidSet( "584416c8-a84b-11e7-b641-74e50bc69d0a:1-4");
//        DumpBinaryLogGitdCommand dumpBinaryLogGitdCommand = new DumpBinaryLogGitdCommand(
//                2,"mysql-bin.000002",4,gtidSet);
        DumpBinaryLogGitdCommand dumpBinaryLogGitdCommand = new DumpBinaryLogGitdCommand(
                2,"mysql-bin.000002",1119,gtidSet);
    
        ByteBuf byteBuf = dumpBinaryLogGitdCommand.toByteBuf ();

        ctx.channel ().writeAndFlush (byteBuf );

      }
}
