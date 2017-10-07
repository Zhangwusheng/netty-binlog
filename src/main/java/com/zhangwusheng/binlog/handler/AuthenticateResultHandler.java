package com.zhangwusheng.binlog.handler;

/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */

import com.zhangwusheng.HandlerUtil;
import com.zhangwusheng.binlog.command.NettyQueryCommand;
import com.zhangwusheng.binlog.network.EofPacket;
import com.zhangwusheng.binlog.network.OKPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticateResultHandler extends SimpleChannelInboundHandler<ByteBuf> {

	private Logger log = LoggerFactory.getLogger ( AuthenticateResultHandler.class );
	
	@Override
	protected void channelRead0( ChannelHandlerContext context, ByteBuf msg) throws Exception {
		try {
			log.info ( "writeAndFlush authenticateCommand,msg.readableBytes="+msg.readableBytes () );
			
			String debugString = ByteBufUtil.prettyHexDump ( msg );
			log.info ( "AuthenticateResultHandler==========" );
			log.info ( debugString );
			log.info ( "AuthenticateResultHandler==========" );
			
			
			if( msg.getByte ( 0 ) == 0x00) {
				OKPacket okPacket = new OKPacket ( );
				okPacket.parse ( msg );
				
				log.info ( "------------------------" );
				log.info ( okPacket.toString () );
				log.info ( "------------------------" );
			}else if( msg.getByte ( 0 ) == 0xFE) {
				EofPacket eofPacket = new EofPacket ();
				eofPacket.parse ( msg );
				//这里应该结束。
			}
			
			{
				context.pipeline().remove(this);// 完成使命，退出历史舞台
//				String sql ="show master status";
				String sql = "show binlog events in 'mysql-bin.000002' limit 5";
				NettyQueryCommand queryCommand = new NettyQueryCommand ( sql );
				
				context.channel ().writeAndFlush ( queryCommand.toByteBuf () );
			}
		} catch (Exception e) {
//			LoggerUtils.error(logger, e.toString());
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
