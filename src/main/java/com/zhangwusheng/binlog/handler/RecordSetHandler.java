package com.zhangwusheng.binlog.handler;

import com.zhangwusheng.ByteUtil;
import com.zhangwusheng.binlog.network.ColumnDefinitionPacket;
import com.zhangwusheng.binlog.network.RowPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhangwusheng on 17/10/4.
 */
public class RecordSetHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private Logger log = LoggerFactory.getLogger ( ShowMasterStatusHandler.class );
    
    protected enum State
    {
        COLUMNS_COUNT,
        COLUMNS_NAMES,
        ROWS_VALUES
    }
    
    private State currentState = State.COLUMNS_COUNT;
    
    private int columnCount  = 0;
    
    private List<ColumnDefinitionPacket> columnDefinitionPacketList = new LinkedList< ColumnDefinitionPacket > (  );
    
    public int getColumnCount ( ) {
        return columnCount;
    }
    
    public ColumnDefinitionPacket[] getColumnDefinitionPackets ( ) {
        return columnDefinitionPacketList.toArray (new ColumnDefinitionPacket[columnDefinitionPacketList.size ()] );
    }
    
    public RowPacket[] getRowPackets ( ) {
        return rowPacketList.toArray (new RowPacket[rowPacketList.size ()]);
    }
    
    private List<RowPacket> rowPacketList = new LinkedList< RowPacket>();
    
    protected void onParseFinish(ChannelHandlerContext ctx){}
    
    protected void channelRead0 ( ChannelHandlerContext ctx, ByteBuf msg ) throws Exception {
        
        if( currentState == State.COLUMNS_COUNT ){
            columnCount = ByteUtil.readInteger ( msg,msg.readableBytes () );
            
            currentState = State.COLUMNS_NAMES;
        }else if(currentState == State.COLUMNS_NAMES  ){
            if(msg.getByte ( 0 ) == (byte)0xFE){
                currentState = State.ROWS_VALUES;
                msg.skipBytes ( msg.readableBytes () );
            }
            else {
                ColumnDefinitionPacket columnDefinitionPacket = new ColumnDefinitionPacket ();
                columnDefinitionPacket.parse ( msg );
                
                this.columnDefinitionPacketList.add ( columnDefinitionPacket );
                log.info ( columnDefinitionPacket.toString () );
            }
        }else if( currentState == State.ROWS_VALUES){
            if(msg.getByte ( 0 ) == (byte)0xFE){
                currentState = State.COLUMNS_COUNT;
                msg.skipBytes ( msg.readableBytes () );
                
                onParseFinish ( ctx );
            }
            else {
                RowPacket rowPacket = new RowPacket ();
                rowPacket.parse ( msg );
                
                this.rowPacketList.add ( rowPacket );
                log.info ( rowPacket.toString () );
                
//                String binlogFile = rowPacket.getValue ( 0 );
//                String binlogPosition = rowPacket.getValue ( 1 );
//                String executedGtid = rowPacket.getValue ( 4 );

//                log.info ( "binlogFile="+binlogFile+",binlogPosition="+binlogPosition
//                        +",executedGtid="+executedGtid);
            }
        }
    }
}
