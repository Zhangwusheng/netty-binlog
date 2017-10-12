package com.zws.binlog.command;



public class SetMasterBinlogChecksumCommand extends SQLQueryCommand {
	
	public SetMasterBinlogChecksumCommand ( ) {
		super("set @master_binlog_checksum= @@global.binlog_checksum");
	}
}
