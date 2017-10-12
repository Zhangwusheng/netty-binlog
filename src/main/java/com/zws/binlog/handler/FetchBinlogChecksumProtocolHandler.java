package com.zws.binlog.handler;

import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class FetchBinlogChecksumProtocolHandler extends QueryPacketHandlerBase {
    
    @Override
    protected void onParseFinished ( ChannelHandlerContext ctx, Record record, List< Object > out ) {
    
        FetchBinglogCheckRecord fetchBinglogCheckRecord;
        if( record.getRowPackets ().length > 0  ) {
            fetchBinglogCheckRecord = new FetchBinglogCheckRecord (
                    record.getRowPackets ( )[ 0 ].getValue ( 0 )
                    , record.getRowPackets ( )[ 0 ].getValue ( 1 )
            );
            
        }else {
            fetchBinglogCheckRecord = FetchBinglogCheckRecord.EMPTY;
        }
    
        out.add ( fetchBinglogCheckRecord );
        
        ctx.pipeline ().remove ( this );
     }
}
