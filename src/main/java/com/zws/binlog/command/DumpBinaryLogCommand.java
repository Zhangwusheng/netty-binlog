package com.zws.binlog.command;

/**
 * Created by zhangwusheng on 17/10/10.
 */

import com.zws.binlog.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class DumpBinaryLogCommand implements Command {
	private long serverId;
	private String binlogFilename;
	private long binlogPosition;

	public DumpBinaryLogCommand ( long serverId, String binlogFilename, long binlogPosition) {
		this.serverId = serverId;
		this.binlogFilename = binlogFilename;
		this.binlogPosition = binlogPosition;
	}
	
	public ByteBuf toByteBuf ( ) {
		
		byte[] dumpBytes = ByteUtil.writeByte((byte) CommandType.BINLOG_DUMP.ordinal(), 1);
		byte[] positionBytes = ByteUtil.writeLong(this.binlogPosition, 4);
		byte[] flagBytes = ByteUtil.writeInt(0, 2); // flag
		byte[] serverIdBytes = ByteUtil.writeLong(this.serverId, 4);
		byte[] binlogFileNameBytes = this.binlogFilename.getBytes();

		int totalCount = dumpBytes.length + positionBytes.length + flagBytes.length + serverIdBytes.length
				+ binlogFileNameBytes.length;
		byte[] totalCountBytes = ByteUtil.writeInt(totalCount, 3);
		byte[] commandTypeBytes = new byte[1];
		commandTypeBytes[0] = 0;// 对于验证命令，这里就是1,其它都是0

		ByteBuf finalBuf = Unpooled.buffer(totalCount + 4);
		finalBuf.writeBytes(totalCountBytes).writeBytes(commandTypeBytes).writeBytes(dumpBytes)
				.writeBytes(positionBytes).writeBytes(flagBytes);
		finalBuf.writeBytes(serverIdBytes).writeBytes(binlogFileNameBytes);
		
		return finalBuf;
	}
}
