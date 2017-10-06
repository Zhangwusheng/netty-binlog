package com.zhangwusheng.binlog.handler;

import com.zhangwusheng.ByteUtil;
import com.zhangwusheng.HandlerUtil;
import com.zhangwusheng.binlog.network.ErrorPacket;
import com.zhangwusheng.binlog.network.ServerException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangwusheng on 17/10/1.
 */
public class MysqlProtoclHeaderHandler extends SimpleChannelInboundHandler<ByteBuf> {
    
    private Logger log = LoggerFactory.getLogger ( MysqlProtoclHeaderHandler.class );
    
    private int headerLength = 0;
    private int sequence = 0;
    private ByteBuf dataBuffer = null;
    @Override
    protected void channelRead0 ( ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf ) throws Exception {
    
        log.info ( "+++++++MysqlProtoclHeaderHandler.channelRead0 called" );
        
        
        /****
        “Type	    Name	        Description
        int<3>	    payload_length	Length of the payload. The number of bytes in the packet
                                    beyond the initial 4 bytes that make up the packet header.
        int<1>	    sequence_id	    Sequence ID
        string<var>	payload	        [len=payload_length] payload of the packet”
    
        摘录来自: Oracle. “MySQL Internals Manual”。 iBooks.
         */
    
        if( null == byteBuf){
            return;
        }else if( byteBuf.readableBytes () == 0 ){
            return;
        }
    
        String debug = ByteBufUtil.prettyHexDump ( byteBuf );
        log.info ( "-----------------" );
        log.info ( debug );
        log.info ( "-----------------" );
    
        
        byte error = byteBuf.getByte (0);
        //看看是不是ERR_PACKT
        
        log.info ( "========Total Readed:"+ byteBuf.readableBytes () );
        
        if( 0xFF == error ){
            byteBuf.skipBytes ( 1 );//把0xFF跳过去
            ErrorPacket errorPacket = new ErrorPacket (  );
            errorPacket.parse ( byteBuf );
            throw new ServerException (errorPacket.getErrorMessage(), errorPacket.getErrorCode(),
                    errorPacket.getSqlState());
        }
        
        if( byteBuf.readableBytes () < 4 ){
            log.info ( "byteBuf.readableBytes () < 4" );
            return;
        }else{
            if( dataBuffer == null ) {
                
                //读取到了至少4个字节，可以把头部信息读出来。
                //所有的mysql协议的头三个字节都是长度，第四个字节是序列号
                headerLength = ByteUtil.readInteger ( byteBuf, 3 );
                sequence = ByteUtil.readInteger ( byteBuf, 1 );
    
                log.info ( "+++++headerLength="+headerLength );
                dataBuffer = Unpooled.buffer ( headerLength );
                
                getPacketData ( byteBuf );
                onPacketDataReaded( channelHandlerContext);
                //如果
            }else{
                getPacketData( byteBuf );
                onPacketDataReaded( channelHandlerContext);
            }
        }
        
    }
    
    
    private void onPacketDataReaded(ChannelHandlerContext channelHandlerContext){
        if(dataBuffer.readableBytes () == this.headerLength){
    
            String debug = ByteBufUtil.prettyHexDump ( dataBuffer );
            log.info ( "-----------------" );
            log.info ( debug );
            log.info ( "-----------------" );
            
            log.info ( "dataBuffer.readableBytes () == this.headerLength="+this.headerLength );
            channelHandlerContext.fireChannelRead ( dataBuffer );
            dataBuffer = null;
            headerLength = 0;
            sequence = 0;
        }
    }
    
    private void getPacketData ( ByteBuf byteBuf ) {
        byte[] bytes = null;
        int length = 0;
        if (byteBuf.hasArray()) {// 支持数组方式
            log.info ( "has array" );
            bytes = byteBuf.array();
            length = bytes.length;
        } else {// 不支持数组方式
            length = byteBuf.readableBytes();
            bytes = new byte[length];
            byteBuf.readBytes (bytes);
        }
        
        dataBuffer.writeBytes ( bytes );
    }
    
    @Override
    public void exceptionCaught ( ChannelHandlerContext ctx, Throwable cause ) throws Exception {
//        super.exceptionCaught ( ctx, cause );
        log.error ( cause.getStackTrace ().toString () );
        HandlerUtil.cleanChannelContext ( ctx, cause );
    }
    
    @Override
    public void channelInactive ( ChannelHandlerContext ctx ) throws Exception {
//        super.channelInactive ( ctx );
        log.info ( "MysqlProtoclHeaderHandler.channelInactive Called" );
        HandlerUtil.cleanChannelContext ( ctx );
    }
}
