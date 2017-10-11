package com.zhangwusheng.binlog.event.data;

import com.zhangwusheng.binlog.event.EventData;

/**
 * Created by zhangwusheng on 17/10/11.
 */
public class NullEventData implements EventData {
    @Override
    public String toString ( ) {
        return "NullEventData";
    }
}
