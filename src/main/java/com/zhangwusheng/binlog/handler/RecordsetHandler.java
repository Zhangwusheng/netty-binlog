package com.zhangwusheng.binlog.handler;

import com.zhangwusheng.ByteUtil;
import com.zhangwusheng.binlog.network.ColumnDefinitionPacket;
import com.zhangwusheng.binlog.network.RowPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangwusheng on 17/10/4.
 */
public class RecordsetHandler  extends SimpleChannelInboundHandler<ByteBuf> {
    private Logger log = LoggerFactory.getLogger ( RecordsetHandler.class );
    
    enum State
    {
        COLUMNS_COUNT,
        COLUMNS_NAMES,
        ROWS_VALUES
    }
    
    private State currentState = State.COLUMNS_COUNT;
    
    private int columnCount  = 0;
    private int currentColumnNameIndex = 0;
    
    
    protected void channelRead0 ( ChannelHandlerContext ctx, ByteBuf msg ) throws Exception {
    
        String debugString = ByteBufUtil.prettyHexDump ( msg );
        log.info ( "RecordsetHandler==========" );
        log.info ( debugString );
        log.info ( "RecordsetHandler==========" );
    
        
        if( currentState == State.COLUMNS_COUNT ){
            columnCount = ByteUtil.readInteger ( msg,msg.readableBytes () );
            log.info ( "Total Columns = "+columnCount );
            
            currentState = State.COLUMNS_NAMES;
            currentColumnNameIndex = 0;
        }else if(currentState == State.COLUMNS_NAMES  ){
            if(msg.getByte ( 0 ) == (byte)0xFE){
                log.info ( "Column Ended EOF" );
//            if( currentColumnNameIndex == columnCount){
                log.info ( "currentColumnNameIndex="+currentColumnNameIndex+",columnCount="+columnCount );
                currentState = State.ROWS_VALUES;
                currentColumnNameIndex = 0;
            }
            else {
                ColumnDefinitionPacket columnDefinitionPacket = new ColumnDefinitionPacket ();
                columnDefinitionPacket.parse ( msg );
                currentColumnNameIndex++;
                
                
                log.info ( "Success parse msg,this="+columnDefinitionPacket.toString () );
                
                
            }
        }else if( currentState == State.ROWS_VALUES){
            if(msg.getByte ( 0 ) == (byte)0xFE){
                currentState = State.COLUMNS_COUNT;
                currentColumnNameIndex = 0;
                
                log.info ( "Row Parse Ended........." );
            }
            else {
                RowPacket rowPacket = new RowPacket ();
                rowPacket.parse ( msg );
                
                log.info ( rowPacket.toString () );
                currentColumnNameIndex++;
            }
            
        }
        
    }
}
