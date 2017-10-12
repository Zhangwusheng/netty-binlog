package com.zws.binlog.command;

/**
 *
 * @author zhiqiang.liu
 * @2016年1月1日
 *
 */

public class ShowMasterStatusCommand extends SQLQueryCommand {
	
	public ShowMasterStatusCommand ( ) {
		super("show master status");
	}
}
