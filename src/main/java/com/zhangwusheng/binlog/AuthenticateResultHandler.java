package com.zhangwusheng.binlog;

/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */

import com.zhangwusheng.HandlerUtil;
import com.zhangwusheng.binlog.network.EofPacket;
import com.zhangwusheng.binlog.network.OKPacket;
import io.netty.buffer.ByteBuf;
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
			
			// 再发送一个命令,前提是自己没有设置指定的binlogname & binlogPosition
			{
				// 根据需要决定是否发送fetchBinlogName&Position
//				ConnectionAttributes myAttributes = ((MyNioSocketChannel) context.channel()).getAttributes();
//				String name = myAttributes.getBinlogFileName().trim();
//				long position = myAttributes.getBinlogPosition();
//				if (null != name && name.length() > 0) {
//					// 说明已经预设了起点
//					// 不需要进行FetchBinlogNamePositionResultHandler
//					context.pipeline().remove(MyConstants.FETCH_BINLOG_NAMEPOSITION_RESULT_HANDLER);
//					// 直接跳到fetchbinlogchecksum环节
//					new FetchBinlogChecksumCommand("show global variables like 'binlog_checksum'").write(context);
//					LoggerUtils.debug(logger,
//							"binlog positon specified :" + name + ":" + position + ", try to fetch checksum");
//				} else {
//					new FetchBinlogNamePositionCommand("show master status").write(context);
//					LoggerUtils.debug(logger, "try to fetch binlog current name&positon");
//				}
			}
			context.pipeline().remove(this);// 完成使命，退出历史舞台
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
