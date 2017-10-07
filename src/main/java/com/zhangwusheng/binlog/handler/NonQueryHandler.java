package com.zhangwusheng.binlog.handler;

/**
 *
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */

import com.zhangwusheng.HandlerUtil;
import com.zhangwusheng.binlog.command.ShowMasterStatusCommand;
import com.zhangwusheng.binlog.network.EofPacket;
import com.zhangwusheng.binlog.network.OKPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NonQueryHandler extends SimpleChannelInboundHandler<ByteBuf> {
	
	private Logger log = LoggerFactory.getLogger ( NonQueryHandler.class );
	
	protected OKPacket okPacket;
	protected EofPacket eofPacket;
	
	@Override
	protected void channelRead0( ChannelHandlerContext context, ByteBuf msg) throws Exception {
		try {
			if( msg.getByte ( 0 ) == 0x00) {
				OKPacket okPacket = new OKPacket ( );
				okPacket.parse ( msg );
				this.okPacket = okPacket;
			}else if( msg.getByte ( 0 ) == 0xFE) {
				EofPacket eofPacket = new EofPacket ();
				eofPacket.parse ( msg );
				this.eofPacket = eofPacket;
				//这里应该结束。
			}
			
			
			onParseFinish ( context );
//			context.pipeline().remove(this);// 完成使命，退出历史舞台
//			ShowMasterStatusCommand queryCommand = new ShowMasterStatusCommand ( );
//
//			context.channel ().writeAndFlush ( queryCommand.toByteBuf () );
			
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
	
	protected void onParseFinish(ChannelHandlerContext ctx){}
	
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
