package com.zws.binlog.handler;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class ShowMasterStatusRecord {
    
    private        String binlogFile ;
    private    String binlogPosition ;
    private     String executedGtid ;
    private boolean empty;
    
    
    public static ShowMasterStatusRecord EMPTY=new ShowMasterStatusRecord ( "","","",true );
    
    public ShowMasterStatusRecord(String binlogFile,String binlogPosition,String executedGtid,boolean isEmpty){
        this.binlogFile = binlogFile;
        this.binlogPosition = binlogPosition;
        this.executedGtid = executedGtid;
        this.empty = isEmpty;
    }
    
    public ShowMasterStatusRecord(String binlogFile,String binlogPosition,String executedGtid){
        this(binlogFile,binlogPosition,executedGtid,false);
    }
    
    public String getBinlogFile ( ) {
        return binlogFile;
    }
    
    public String getBinlogPosition ( ) {
        return binlogPosition;
    }
    
    public String getExecutedGtid ( ) {
        return executedGtid;
    }
    
    public boolean isEmpty ( ) {
        return empty;
    }
    
    @Override
    public String toString ( ) {
        return "ShowMasterStatusRecord{" +
                "binlogFile='" + binlogFile + '\'' +
                ", binlogPosition='" + binlogPosition + '\'' +
                ", executedGtid='" + executedGtid + '\'' +
                '}';
    }
}
