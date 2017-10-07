package com.zhangwusheng.binlog.handler;

import com.zhangwusheng.binlog.command.FetchBinlogChecksumCommand;
import com.zhangwusheng.binlog.command.SetMasterBinlogChecksumCommand;
import com.zhangwusheng.binlog.network.RowPacket;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by zhangwusheng on 17/10/4.
 */
public class FetchBinlogChecksumHandler extends RecordSetHandler {
    private Logger log = LoggerFactory.getLogger ( FetchBinlogChecksumHandler.class );
    
    private        String variableName ;
    private    String variableValue ;
    
    
    @Override
    protected void onParseFinish (ChannelHandlerContext ctx ) {
        super.onParseFinish ( ctx );
        
        RowPacket[] rowPackets =  getRowPackets ( );
        variableName = rowPackets[0].getValue ( 0 );
        variableValue = rowPackets[0].getValue ( 1 );
    
        log.info ( "variableName="+variableName+",variableValue="+variableValue );
    
        ctx.pipeline().remove(this);
    
        SetMasterBinlogChecksumCommand checksumCommand = new SetMasterBinlogChecksumCommand ();
        ctx.channel ().writeAndFlush ( checksumCommand.toByteBuf () );
    }
}
