package com.zws.binlog.command;

/**
 * Created by zhangwusheng on 17/10/10.
 */

public class FetchBinlogChecksumCommand extends SQLQueryCommand {
	
	public FetchBinlogChecksumCommand ( ) {
		super("show global variables like 'binlog_checksum'");
	}
}
