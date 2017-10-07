package com.zhangwusheng.binlog.handler;

import com.zhangwusheng.binlog.command.FetchBinlogChecksumCommand;
import com.zhangwusheng.binlog.network.RowPacket;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by zhangwusheng on 17/10/4.
 */
public class ShowMasterStatusHandler extends RecordSetHandler {
    private Logger log = LoggerFactory.getLogger ( ShowMasterStatusHandler.class );
    
    private        String binlogFile ;
    private    String binlogPosition ;
    private     String executedGtid ;
    
    
    @Override
    protected void onParseFinish (ChannelHandlerContext ctx ) {
        super.onParseFinish ( ctx );
        
        RowPacket[] rowPackets =  getRowPackets ( );
        binlogFile = rowPackets[0].getValue ( 0 );
        binlogPosition = rowPackets[0].getValue ( 1 );
        executedGtid = rowPackets[0].getValue ( 4 );
        
        log.info ( "binlogFile="+binlogFile+",binlogPosition="+binlogPosition
                +",executedGtid="+executedGtid);
        
        FetchBinlogChecksumCommand checksumCommand = new FetchBinlogChecksumCommand ();
        ctx.channel ().writeAndFlush ( checksumCommand.toByteBuf () );
        ctx.pipeline().remove(this);
    
    }
}
