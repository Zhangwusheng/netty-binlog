package com.zhangwusheng.binlog.handler;

/**
 *
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetMasterBinlogChecksumHandler extends NonQueryHandler {
	
	private Logger log = LoggerFactory.getLogger ( SetMasterBinlogChecksumHandler.class );
	
	@Override
	protected void onParseFinish ( ChannelHandlerContext context ) {
		super.onParseFinish ( context );
		
		if( this.okPacket != null ){
			log.info ( okPacket.toString () );
		}
		
		context.pipeline().remove(this);// 完成使命，退出历史舞台
//		ShowMasterStatusCommand queryCommand = new ShowMasterStatusCommand ( );
//
//		context.channel ().writeAndFlush ( queryCommand.toByteBuf () );
		
	}
}
