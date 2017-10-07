package com.zhangwusheng.binlog.command;

/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 * @QQ: 837500869
 */

//import com.hzw.monitor.mysqlbinlog.utils.ByteUtils;
//import com.hzw.monitor.mysqlbinlog.utils.LoggerUtils;
import com.zhangwusheng.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
//import org.apache.log4j.LogManager;
//import org.apache.log4j.Logger;

public class DumpBinaryLogCommand implements Command {
//	private static final Logger logger = LogManager.getLogger(RequestBinaryLogCommand.class);
	private long serverId;
	private String binlogFilename;
	private long binlogPosition;

	public DumpBinaryLogCommand ( long serverId, String binlogFilename, long binlogPosition) {
		this.serverId = serverId;
		this.binlogFilename = binlogFilename;
		this.binlogPosition = binlogPosition;
	}
	
	public ByteBuf toByteBuf ( ) {
		
//		LoggerUtils.info(logger, "get ready to send binary log request - serverid: " + this.serverId + " binlogName: "
//				+ this.binlogFilename + " position:" + this.binlogPosition);
		// LoggerUtils.debug(logger, "write RequestBinaryLogCommand");
		byte[] dumpBytes = ByteUtil.writeByte((byte) CommandType.BINLOG_DUMP.ordinal(), 1);
		byte[] positionBytes = ByteUtil.writeLong(this.binlogPosition, 4);
		byte[] flagBytes = ByteUtil.writeInt(0, 2); // flag
		byte[] serverIdBytes = ByteUtil.writeLong(this.serverId, 4);
		byte[] binlogFileNameBytes = this.binlogFilename.getBytes();

		// 然后开始构造头
		int totalCount = dumpBytes.length + positionBytes.length + flagBytes.length + serverIdBytes.length
				+ binlogFileNameBytes.length;
		byte[] totalCountBytes = ByteUtil.writeInt(totalCount, 3);
		byte[] commandTypeBytes = new byte[1];
		commandTypeBytes[0] = 0;// 对于验证命令，这里就是1,其它都是0

		// 构造缓冲区并发送
		ByteBuf finalBuf = Unpooled.buffer(totalCount + 4);
		finalBuf.writeBytes(totalCountBytes).writeBytes(commandTypeBytes).writeBytes(dumpBytes)
				.writeBytes(positionBytes).writeBytes(flagBytes);
		finalBuf.writeBytes(serverIdBytes).writeBytes(binlogFileNameBytes);
		
		return finalBuf;
		//context.channel().writeAndFlush(finalBuf);// 缓存清理
		// LoggerUtils.debug(logger, "准备发送请求二进制日志成功");
	}
}
