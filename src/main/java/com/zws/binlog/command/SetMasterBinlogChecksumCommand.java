package com.zws.binlog.command;

/**
 *
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */

public class SetMasterBinlogChecksumCommand extends NettyQueryCommand {
	
//	private String sql = null;
	
	public SetMasterBinlogChecksumCommand ( ) {
		super("set @master_binlog_checksum= @@global.binlog_checksum");
//		this.sql = sql;
	}
	
//	public ByteBuf toByteBuf ( ) {
//
//		byte[] queryBytes = ByteUtil.writeByte((byte) CommandType.QUERY.ordinal(), 1);
//		byte[] sqlBytes = this.getSql().getBytes();// 不用带\0,所以不需要使用ByteUtils
//		// // 构造总的数据
//		int totalCount = queryBytes.length + sqlBytes.length;
//		byte[] totalCountBytes = ByteUtil.writeInt(totalCount, 3);
//		byte[] commandTypeBytes = new byte[1];
//		commandTypeBytes[0] = 0;// 对于fetchBinlogNamePosition命令，这里就是0
//		// 可以发送了
//		ByteBuf finalBuf = Unpooled.buffer(totalCount + 4);
//		finalBuf.writeBytes(totalCountBytes).writeBytes(commandTypeBytes).writeBytes(queryBytes).writeBytes(sqlBytes);
//
//		return finalBuf;
//	}
}