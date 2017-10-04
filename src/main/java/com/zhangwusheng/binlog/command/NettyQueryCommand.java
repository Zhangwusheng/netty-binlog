package com.zhangwusheng.binlog.command;

/**
 *
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */

import com.zhangwusheng.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NettyQueryCommand implements Command {
	
	private String sql = null;
	
	public NettyQueryCommand ( String sql) {
		this.sql = sql;
	}
	
	public ByteBuf toByteBuf ( ) {
		
		byte[] queryBytes = ByteUtil.writeByte((byte) CommandType.QUERY.ordinal(), 1);
		byte[] sqlBytes = this.sql.getBytes();// 不用带\0,所以不需要使用ByteUtils
		// // 构造总的数据
		int totalCount = queryBytes.length + sqlBytes.length;
		byte[] totalCountBytes = ByteUtil.writeInt(totalCount, 3);
		byte[] commandTypeBytes = new byte[1];
		commandTypeBytes[0] = 0;// 对于fetchBinlogNamePosition命令，这里就是0
		// 可以发送了
		ByteBuf finalBuf = Unpooled.buffer(totalCount + 4);
		finalBuf.writeBytes(totalCountBytes).writeBytes(commandTypeBytes).writeBytes(queryBytes).writeBytes(sqlBytes);
		
		return finalBuf;
	}
}
