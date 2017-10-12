package com.zws.binlog.handler;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class FetchBinlogChecksumProtocolHandler extends QueryPacketHandlerBase {
    
    private Logger log = LoggerFactory.getLogger ( FetchBinlogChecksumProtocolHandler.class );
    
    
    @Override
    protected void onParseFinished ( ChannelHandlerContext ctx, Record record, List< Object > out ) {
    
        FetchBinglogCheckRecord fetchBinglogCheckRecord;
        if( record.getRowPackets ().length > 0  ) {
            fetchBinglogCheckRecord = new FetchBinglogCheckRecord (
                    record.getRowPackets ( )[ 0 ].getValue ( 0 )
                    , record.getRowPackets ( )[ 0 ].getValue ( 1 )
            );
    
//            log.info ( showMasterStatusRecord.toString ( ) );
            
        }else
            fetchBinglogCheckRecord = FetchBinglogCheckRecord.EMPTY;
    
        out.add ( fetchBinglogCheckRecord );
        
    
        ctx.pipeline ().remove ( this );
     }
}
