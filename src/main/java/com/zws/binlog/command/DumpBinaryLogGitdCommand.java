package com.zws.binlog.command;

/**
 * Created by zhangwusheng on 17/10/10.
 */

import com.zws.binlog.util.ByteUtil;
import com.zws.binlog.event.GtidSet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Collection;

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
	
	/**
	 *
	 *rpl_master.cc  376
	 *
	 READ_INT(flags,2);
	 READ_INT(thd->server_id, 4);
	 READ_INT(name_size, 4);
	 READ_STRING(name, name_size, sizeof(name));
	 READ_INT(pos, 8);
	 DBUG_PRINT("info", ("pos=%llu flags=%d server_id=%d", pos, flags, thd->server_id));
	 READ_INT(data_size, 4);
	 CHECK_PACKET_SIZE(data_size);
	 if (slave_gtid_executed.add_gtid_encoding(packet_position, data_size) !=
	 RETURN_STATUS_OK)
	 DBUG_RETURN(true);
	 slave_gtid_executed.to_string(&gtid_string);
	 DBUG_PRINT("info", ("Slave %d requested to read %s at position %llu gtid set "
	 "'%s'.", thd->server_id, name, pos, gtid_string));
	 
	 kill_zombie_dump_threads(thd);
	 query_logger.general_log_print(thd, thd->get_command(),
	 "Log: '%s' Pos: %llu GTIDs: '%s'",
	 name, pos, gtid_string);
	 my_free(gtid_string);
	 mysql_binlog_send(thd, name, (my_off_t) pos, &slave_gtid_executed, flags);
	 *
	 * */
	
	public ByteBuf toByteBuf ( ) {
		
		ByteBuf finalBuf = Unpooled.buffer(100);
		
		//CommandType:1 0x1e
		byte[] dumpBytes = ByteUtil.writeByte((byte) CommandType.BINLOG_DUMP_GTID.ordinal(), 1);
		finalBuf.writeBytes(dumpBytes);
		
		//Flags:2
		byte[] flagBytes = ByteUtil.writeInt(0, 2); // flag
//		byte[] flagBytes = ByteUtil.writeInt(4, 2); // flag
		finalBuf.writeBytes(flagBytes);
		
		//serverid:4
		byte[] serverIdBytes = ByteUtil.writeLong(this.serverId, 4);
		finalBuf.writeBytes(serverIdBytes);
		//binlog-filename-len:4
		byte[] binlogFilenameLengthBytes = ByteUtil.writeLong(this.binlogFilename.length (), 4);
		finalBuf.writeBytes(binlogFilenameLengthBytes);
		//binlog-filename:string
		byte[] binlogFileNameBytes = this.binlogFilename.getBytes();
		finalBuf.writeBytes(binlogFileNameBytes);
		//binlogPosition:8
		byte[] positionBytes = ByteUtil.writeLong(this.binlogPosition, 8);
		finalBuf.writeBytes(positionBytes);
		
		
		Collection<GtidSet.UUIDSet> uuidSets = gtidSet.getUUIDSets();
		int dataSize = 8 /* number of uuidSets */;
		
		for (GtidSet.UUIDSet uuidSet : uuidSets) {
			dataSize += 16 /* uuid */ + 8 /* number of intervals */ +
					uuidSet.getIntervals().size() /* number of intervals */ * 16 /* start-end */;
		}
		
		//datasize:4
		byte[] gtidSizeBytes = ByteUtil.writeLong(dataSize, 4);
		
		finalBuf.writeBytes(gtidSizeBytes);
		//nsids:8
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
		finalBuf2.writeBytes(ByteUtil.writeInt(finalBuf.readableBytes(),3));
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
	
	public static byte[] hexToByteArray(String uuid) {
		byte[] b = new byte[uuid.length() / 2];
		for (int i = 0, j = 0; j < uuid.length(); j += 2) {
			b[i++] = (byte) Integer.parseInt(uuid.charAt(j) + "" + uuid.charAt(j + 1), 16);
		}
		return b;
	}
}
