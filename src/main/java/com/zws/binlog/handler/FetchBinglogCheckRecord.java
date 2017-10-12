package com.zws.binlog.handler;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class FetchBinglogCheckRecord {
    
    private        String variableName ;
    private    String variableValue ;
    boolean empty;
    
    public static FetchBinglogCheckRecord EMPTY =new FetchBinglogCheckRecord ( "","",true );
    
    public FetchBinglogCheckRecord(String variableName,String variableValue,boolean empty){
        this.variableName = variableName;
        this.variableValue = variableValue;
        this.empty = empty;
    }
    public FetchBinglogCheckRecord(String variableName,String variableValue){
        this(variableName,variableValue,false);
    }
    
    @Override
    public String toString ( ) {
        return "FetchBinglogCheckRecord{" +
                "variableName='" + variableName + '\'' +
                ", variableValue='" + variableValue + '\'' +
                '}';
    }
    
    public String getVariableName ( ) {
        return variableName;
    }
    
    public String getVariableValue ( ) {
        return variableValue;
    }
}
