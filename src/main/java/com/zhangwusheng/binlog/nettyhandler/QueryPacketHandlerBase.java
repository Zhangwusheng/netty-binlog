package com.zhangwusheng.binlog.nettyhandler;

import com.zhangwusheng.ByteUtil;
import com.zhangwusheng.binlog.network.ColumnDefinitionPacket;
import com.zhangwusheng.binlog.network.RowPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class QueryPacketHandlerBase extends ByteToMessageDecoder {
    
    private Logger log = LoggerFactory.getLogger ( QueryPacketHandlerBase.class );
    
    protected enum State
    {
        BEGIN,
        COLUMNS_COUNT,
        COLUMNS_NAMES,
        ROWS_VALUES,
        FINISH
    }
    
    private State currentState = State.BEGIN;
    
    private int columnCount  = 0;
    
    private List<ColumnDefinitionPacket > columnDefinitionPacketList = new LinkedList< ColumnDefinitionPacket > (  );
    
    private int getColumnCount ( ) {
        return columnCount;
    }
    
    private ColumnDefinitionPacket[] getColumnDefinitionPackets ( ) {
        return columnDefinitionPacketList.toArray (new ColumnDefinitionPacket[columnDefinitionPacketList.size ()] );
    }
    
    private RowPacket[] getRowPackets ( ) {
        return rowPacketList.toArray (new RowPacket[rowPacketList.size ()]);
    }
    
    private List<RowPacket> rowPacketList = new LinkedList< RowPacket>();
    
    protected void onParseFinished(ChannelHandlerContext ctx,Record record,List< Object > out){}
    
    protected void decode ( ChannelHandlerContext ctx, ByteBuf msg, List< Object > out ) throws Exception {
    
        String debug = ByteBufUtil.prettyHexDump ( msg );
        log.info ( debug );
        decode0(ctx, msg, out );
        
        if(  currentState == State.FINISH ){
            Record record = new Record ( getColumnCount ()
                    ,getColumnDefinitionPackets (),getRowPackets () );
            currentState = State.BEGIN;
            onParseFinished(ctx,record,out);
        }
    }
    
    int packetLength;
    int sequence;
    protected void decode0 ( ChannelHandlerContext ctx, ByteBuf msg, List< Object > out ) throws Exception {
    
        if( currentState == State.BEGIN ){
            currentState = State.COLUMNS_COUNT;
        }
        msg.markReaderIndex ();
        if( msg.readableBytes () < 4 ) {
            msg.resetReaderIndex ();
            return;
        }else{
            packetLength= ByteUtil.readInteger ( msg,3 );
            sequence = ByteUtil.readInteger ( msg,1 );
            if( msg.readableBytes () < packetLength){
                msg.resetReaderIndex ();
                return;
            }
        }
    
        String debug = ByteBufUtil.prettyHexDump ( msg );
        log.info ( debug );
        
        if( currentState == State.COLUMNS_COUNT ){
            columnCount = ByteUtil.readInteger ( msg,1 );
            currentState = State.COLUMNS_NAMES;
        }else if( currentState == State.COLUMNS_NAMES){
            
            int dataStartIndex = msg.readerIndex ();
            System.out.println ( "----------"+msg.getByte ( dataStartIndex ) );
            if(msg.getByte ( dataStartIndex ) == (byte)0xFE){
                
                currentState = State.ROWS_VALUES;
                msg.skipBytes ( packetLength );
            } else {
                ColumnDefinitionPacket columnDefinitionPacket = new ColumnDefinitionPacket ();
                columnDefinitionPacket.parse ( msg );
                this.columnDefinitionPacketList.add ( columnDefinitionPacket );
//                log.info ( columnDefinitionPacket.toString () );
            }
        }else if( currentState == State.ROWS_VALUES){
            int dataStartIndex = msg.readerIndex ();
            if(msg.getByte ( dataStartIndex ) == (byte)0xFE){
                currentState = State.FINISH;
                msg.skipBytes ( packetLength );
            }
            else {
                RowPacket rowPacket = new RowPacket ();
                
                ByteBuf rowmsg = msg.readSlice ( packetLength );
                
                rowPacket.parse ( rowmsg );
        
                this.rowPacketList.add ( rowPacket );
                log.info ( rowPacket.toString () );
            }
        }
    }
}
