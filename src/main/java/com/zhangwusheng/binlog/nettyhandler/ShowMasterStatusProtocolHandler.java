package com.zhangwusheng.binlog.nettyhandler;

import com.zhangwusheng.binlog.command.FetchBinlogChecksumCommand;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class ShowMasterStatusProtocolHandler extends QueryPacketHandlerBase {
    
    private Logger log = LoggerFactory.getLogger ( ShowMasterStatusProtocolHandler.class );
    
    
    @Override
    protected void onParseFinished ( ChannelHandlerContext ctx, Record record, List< Object > out ) {
    
        ShowMasterStatusRecord showMasterStatusRecord;
        if( record.getRowPackets ().length > 0  ) {
             showMasterStatusRecord = new ShowMasterStatusRecord (
                    record.getRowPackets ( )[ 0 ].getValue ( 0 )
                    , record.getRowPackets ( )[ 0 ].getValue ( 1 )
                    , record.getRowPackets ( )[ 0 ].getValue ( 4 )
            );
    
//            log.info ( showMasterStatusRecord.toString ( ) );
        }else{
            showMasterStatusRecord = ShowMasterStatusRecord.EMPTY;
        }
        
        out.add ( showMasterStatusRecord );
    
        ctx.pipeline ().remove ( this );
    
     }
}
