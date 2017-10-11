package com.zhangwusheng.binlog.handler;

/**
 *
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */

import com.zhangwusheng.ByteUtil;
import com.zhangwusheng.HandlerUtil;
import com.zhangwusheng.binlog.event.EventHeaderV4;
import com.zhangwusheng.binlog.event.EventType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinlogEventHandler extends SimpleChannelInboundHandler<ByteBuf> {
	
	private Logger log = LoggerFactory.getLogger ( BinlogEventHandler.class );
	
	private static final EventType[] EVENT_TYPES = EventType.values();
	
	@Override
	protected void channelRead0( ChannelHandlerContext context, ByteBuf msg) throws Exception {

		try{
			
			log.info ( ByteBufUtil.prettyHexDump ( msg) );
			
			EventHeaderV4 header = new EventHeaderV4 ();
			header.setTimestamp( ByteUtil.readUnsignedLong(msg, 4) * 1000L);
			header.setEventType(EVENT_TYPES[ByteUtil.readUnsignedInt(msg, 1)]);
			header.setServerId(ByteUtil.readUnsignedLong(msg, 4));
			header.setEventLength(ByteUtil.readUnsignedLong(msg, 4));
			header.setNextPosition(ByteUtil.readUnsignedLong(msg, 4));
			header.setFlag(ByteUtil.readUnsignedInt(msg, 2));

			log.info ( header.toString () );
			//
//
// LoggerUtils.debug(logger, header.toString());
			
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
	
	@Override
	public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause) {
		HandlerUtil.cleanChannelContext ( ctx, cause );
		// Close the connection when an exception is raised.
		// cause.printStackTrace();//务必要关闭
//		LoggerUtils.error(logger, cause.toString());
//		NettyUtils.cleanChannelContext(ctx, cause);
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		HandlerUtil.cleanChannelContext ( ctx, null );
//		LoggerUtils.debug(logger, "[channelInactive] socket is closed by remote server");
//		NettyUtils.cleanChannelContext(ctx, null);
	}
	
}
