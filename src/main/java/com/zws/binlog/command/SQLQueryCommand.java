package com.zws.binlog.command;

/**
 * Created by zhangwusheng on 17/10/10.
 */

import com.zws.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class SQLQueryCommand implements Command {
	
	private String sql = null;
	
	public String getSql ( ) {
		return sql;
	}
	
	public SQLQueryCommand ( String sql) {
		this.sql = sql;
	}
	
	public ByteBuf toByteBuf ( ) {
		
		byte[] queryBytes = ByteUtil.writeByte((byte) CommandType.QUERY.ordinal(), 1);
		byte[] sqlBytes = this.sql.getBytes();
	
		int totalCount = queryBytes.length + sqlBytes.length;
		byte[] totalCountBytes = ByteUtil.writeInt(totalCount, 3);
		byte[] commandTypeBytes = new byte[1];
		
		ByteBuf finalBuf = Unpooled.buffer(totalCount + 4);
		finalBuf.writeBytes(totalCountBytes).writeBytes(commandTypeBytes).writeBytes(queryBytes).writeBytes(sqlBytes);
		
		return finalBuf;
	}
}
