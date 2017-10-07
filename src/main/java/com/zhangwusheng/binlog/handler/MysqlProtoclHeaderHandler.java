package com.zhangwusheng.binlog.handler;

import com.zhangwusheng.ByteUtil;
import com.zhangwusheng.HandlerUtil;
import com.zhangwusheng.binlog.network.ErrorPacket;
import com.zhangwusheng.binlog.network.ServerException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
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
    private ByteBuf dataLeft = null;
    
    @Override
    protected void channelRead0 ( ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf ) throws Exception {
    
//        log.info ( "+++++++MysqlProtoclHeaderHandler.channelRead0 called" );
        
        
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
    
//        String debugString = ByteBufUtil.prettyHexDump ( byteBuf );
//        log.info ( "MysqlProtoclHeaderHandler==========" );
//        log.info ( debugString );
//        log.info ( "MysqlProtoclHeaderHandler==========" );
    
        
        byte error = byteBuf.getByte (0);
        //看看是不是ERR_PACKT
        
//        log.info ( "========Total Readed:"+ byteBuf.readableBytes () );
        
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
            //首先把剩下的数据和读取到的数据合并起来.
            CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer ();
            if( dataLeft != null ){
                compositeByteBuf.addComponent ( true,dataLeft );
            }
            compositeByteBuf.addComponent ( true,byteBuf );
            
            //这里有可能一次性读过来很多Packet的数据，所以要循环判断
            while( true ){
                if( compositeByteBuf.readableBytes () < 4 ){
                    if( dataLeft != null ){
                        dataLeft.release ();
                    }
                    dataLeft = compositeByteBuf.copy ();
//                    log.info ( "byteBuf.readableBytes () < 4:"+compositeByteBuf.readableBytes () );
                    return;
                }
    
                compositeByteBuf.markReaderIndex ();
                headerLength = ByteUtil.readInteger ( compositeByteBuf, 3 );
                sequence = ByteUtil.readInteger ( compositeByteBuf, 1 );
    
    
//                log.info ( "readableBytes="+compositeByteBuf.readableBytes ()+",headerLength="+headerLength );
                if( compositeByteBuf.readableBytes () < headerLength)
                {
                    compositeByteBuf.resetReaderIndex ();
                    if( dataLeft != null ){
                        dataLeft.release ();
                    }
                    dataLeft = compositeByteBuf.copy ();
                    return;
                }
                
                ByteBuf newBuff = Unpooled.buffer ( headerLength );
                compositeByteBuf.readBytes ( newBuff,headerLength );
                onFullPacketDataReaded(channelHandlerContext, newBuff );

//                String debug = ByteBufUtil.prettyHexDump ( newBuff );
//                log.info ( debug );
//
//                ByteBuf payload = compositeByteBuf.slice (compositeByteBuf.readerIndex (), headerLength );
//                String debug = ByteBufUtil.prettyHexDump ( payload );
//                log.info ( debug );
//
//                payload.retain ();
//
//                onFullPacketDataReaded(channelHandlerContext, payload );
            }
        }
    }
    
    private void onFullPacketDataReaded(ChannelHandlerContext channelHandlerContext,ByteBuf buffer){

            String debug = ByteBufUtil.prettyHexDump ( buffer );
//            log.info ( "-----------------" );
            log.info ( debug );
//            log.info ( "-----------------" );
        
            channelHandlerContext.fireChannelRead ( buffer );

    }
    
    @Override
    public void exceptionCaught ( ChannelHandlerContext ctx, Throwable cause ) throws Exception {

        log.error ( cause.getMessage () );
        HandlerUtil.cleanChannelContext ( ctx, cause );
    }
    
    @Override
    public void channelInactive ( ChannelHandlerContext ctx ) throws Exception {
        log.info ( "MysqlProtoclHeaderHandler.channelInactive Called" );
        HandlerUtil.cleanChannelContext ( ctx );
    }
}
