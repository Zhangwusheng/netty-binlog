package com.zws.binlog.handler;

import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class ShowMasterStatusProtocolHandler extends QueryPacketHandlerBase {
    
      
    @Override
    protected void onParseFinished ( ChannelHandlerContext ctx, Record record, List< Object > out ) {
    
        ShowMasterStatusRecord showMasterStatusRecord;
        if( record.getRowPackets ().length > 0  ) {
             showMasterStatusRecord = new ShowMasterStatusRecord (
                    record.getRowPackets ( )[ 0 ].getValue ( 0 )
                    , record.getRowPackets ( )[ 0 ].getValue ( 1 )
                    , record.getRowPackets ( )[ 0 ].getValue ( 4 )
            );
        }else{
            showMasterStatusRecord = ShowMasterStatusRecord.EMPTY;
        }
        
        out.add ( showMasterStatusRecord );
        ctx.pipeline ().remove ( this );
     }
}
