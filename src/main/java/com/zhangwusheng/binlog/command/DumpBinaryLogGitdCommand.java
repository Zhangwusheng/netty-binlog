package com.zhangwusheng.binlog.command;

/**
 * 
 * @author zhiqiang.liu
 * @2016年1月1日
 * @QQ: 837500869
 */

import com.zhangwusheng.ByteUtil;
import com.zhangwusheng.binlog.GtidSet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DumpBinaryLogGitdCommand implements Command {
	private long serverId;
	private String binlogFilename;
	private long binlogPosition;
	private GtidSet gtidSet;

	public DumpBinaryLogGitdCommand(long serverId, String binlogFilename, long binlogPosition,GtidSet gtidSet) {
		this.serverId = serverId;
		this.binlogFilename = binlogFilename;
		this.binlogPosition = binlogPosition;
		this.gtidSet = gtidSet;
	}
	
	public ByteBuf toByteBuf ( ) {

		ByteBuf finalBuf = Unpooled.buffer(100);

				byte[] dumpBytes = ByteUtil.writeByte((byte) CommandType.BINLOG_DUMP_GTID.ordinal(), 1);
		finalBuf.writeBytes(dumpBytes);

		byte[] flagBytes = ByteUtil.writeInt(0, 2); // flag
		finalBuf.writeBytes(flagBytes);

		byte[] serverIdBytes = ByteUtil.writeLong(this.serverId, 4);
		finalBuf.writeBytes(serverIdBytes);

		byte[] binlogFilenameLengthBytes = ByteUtil.writeLong(this.serverId, 4);
		finalBuf.writeBytes(binlogFilenameLengthBytes);
		byte[] binlogFileNameBytes = this.binlogFilename.getBytes();
		finalBuf.writeBytes(binlogFileNameBytes);

		byte[] positionBytes = ByteUtil.writeLong(this.binlogPosition, 4);
		finalBuf.writeBytes(positionBytes);


		Collection<GtidSet.UUIDSet> uuidSets = gtidSet.getUUIDSets();
		int dataSize = 8 /* number of uuidSets */;

		for (GtidSet.UUIDSet uuidSet : uuidSets) {
			dataSize += 16 /* uuid */ + 8 /* number of intervals */ +
					uuidSet.getIntervals().size() /* number of intervals */ * 16 /* start-end */;
		}

		byte[] gtidSizeBytes = ByteUtil.writeLong(dataSize, 4);

		finalBuf.writeBytes(gtidSizeBytes);

		byte[] gtidCountBytes = ByteUtil.writeLong(uuidSets.size(), 8);
		finalBuf.writeBytes(gtidCountBytes);


		for (GtidSet.UUIDSet uuidSet : uuidSets) {
//			buffer.write(hexToByteArray(uuidSet.getUUID().replace("-", "")));
			finalBuf.writeBytes(hexToByteArray(uuidSet.getUUID().replace("-", "")));

			Collection<GtidSet.Interval> intervals = uuidSet.getIntervals();
//			buffer.writeLong(intervals.size(), 8);
			byte[] temp1 = ByteUtil.writeLong(intervals.size(), 8);
			finalBuf.writeBytes(temp1);


			for (GtidSet.Interval interval : intervals) {
//				buffer.writeLong(interval.getStart(), 8);
				byte[] temp2 = ByteUtil.writeLong(interval.getStart(), 8);
				finalBuf.writeBytes(temp2);
				temp2=ByteUtil.writeLong(interval.getEnd() + 1 /* right-open */, 8);
				finalBuf.writeBytes(temp2);
//				buffer.writeLong(interval.getEnd() + 1 /* right-open */, 8);
			}
		}

		ByteBuf finalBuf2 = Unpooled.buffer(finalBuf.readableBytes()+4);
		finalBuf2.writeBytes(ByteUtil.writeInt(finalBuf.readableBytes()+4,3));
		byte[] commandTypeBytes = new byte[1];
		commandTypeBytes[0] = 0;// 对于验证命令，这里就是1,其它都是0
		finalBuf2.writeBytes(commandTypeBytes);
		finalBuf2.writeBytes(finalBuf);
		return finalBuf2;

//		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//		buffer.writeInteger(CommandType.BINLOG_DUMP_GTID.ordinal(), 1);
//		buffer.writeInteger(0, 2); // flag
//		buffer.writeLong(this.serverId, 4);
//		buffer.writeInteger(this.binlogFilename.length(), 4);
//		buffer.writeString(this.binlogFilename);
//		buffer.writeLong(this.binlogPosition, 8);
//		Collection<GtidSet.UUIDSet> uuidSets = gtidSet.getUUIDSets();
//		int dataSize = 8 /* number of uuidSets */;
//		for (GtidSet.UUIDSet uuidSet : uuidSets) {
//			dataSize += 16 /* uuid */ + 8 /* number of intervals */ +
//					uuidSet.getIntervals().size() /* number of intervals */ * 16 /* start-end */;
//		}
//		buffer.writeInteger(dataSize, 4);
//		buffer.writeLong(uuidSets.size(), 8);
//		for (GtidSet.UUIDSet uuidSet : uuidSets) {
//			buffer.write(hexToByteArray(uuidSet.getUUID().replace("-", "")));
//			Collection<GtidSet.Interval> intervals = uuidSet.getIntervals();
//			buffer.writeLong(intervals.size(), 8);
//			for (GtidSet.Interval interval : intervals) {
//				buffer.writeLong(interval.getStart(), 8);
//				buffer.writeLong(interval.getEnd() + 1 /* right-open */, 8);
//			}
//		}



//		byte[] dumpBytes = ByteUtil.writeByte((byte) CommandType.BINLOG_DUMP.ordinal(), 1);
//		byte[] positionBytes = ByteUtil.writeLong(this.binlogPosition, 4);
//		byte[] flagBytes = ByteUtil.writeInt(0, 2); // flag
//		byte[] serverIdBytes = ByteUtil.writeLong(this.serverId, 4);
//		byte[] binlogFileNameBytes = this.binlogFilename.getBytes();
//
//		int totalCount = dumpBytes.length + positionBytes.length + flagBytes.length + serverIdBytes.length
//				+ binlogFileNameBytes.length;
//		byte[] totalCountBytes = ByteUtil.writeInt(totalCount, 3);
//		byte[] commandTypeBytes = new byte[1];
//		commandTypeBytes[0] = 0;// 对于验证命令，这里就是1,其它都是0
//
//		ByteBuf finalBuf = Unpooled.buffer(totalCount + 4);
//		finalBuf.writeBytes(totalCountBytes).writeBytes(commandTypeBytes).writeBytes(dumpBytes)
//				.writeBytes(positionBytes).writeBytes(flagBytes);
//		finalBuf.writeBytes(serverIdBytes).writeBytes(binlogFileNameBytes);
//

	}

	private static byte[] hexToByteArray(String uuid) {
		byte[] b = new byte[uuid.length() / 2];
		for (int i = 0, j = 0; j < uuid.length(); j += 2) {
			b[i++] = (byte) Integer.parseInt(uuid.charAt(j) + "" + uuid.charAt(j + 1), 16);
		}
		return b;
	}
}
