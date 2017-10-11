package com.zhangwusheng.binlog.event.data;

import com.zhangwusheng.binlog.GtidSet;
import com.zhangwusheng.binlog.event.EventData;

/**
 * Created by zhangwusheng on 17/10/11.
 *
 * PREVIOUS_GTIDS_LOG_EVENT
 
 http://www.cnblogs.com/ivictor/p/5780617.html
 开启GTID模式后，每个binlog开头都会有一个PREVIOUS_GTIDS_LOG_EVENT事件，
 它的值是上一个binlog的PREVIOUS_GTIDS_LOG_EVENT+GTID_LOG_EVENT，
 实际上，在数据库重启的时候，需要重新填充gtid_executed的值，
 该值即是最新一个binlog的PREVIOUS_GTIDS_LOG_EVENT+GTID_LOG_EVENT。
 
 
 +-------------------------------------------------+
 |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
 +--------+-------------------------------------------------+----------------+
 |00000000| 01 00 00 00 00 00 00 00 29 fd 49 26 5b 6d 11 e5 |........).I&[m..|
 |00000010| a1 b3 ee de 8d 25 12 eb 01 00 00 00 00 00 00 00 |.....%..........|
 |00000020| 01 00 00 00 00 00 00 00 07 00 00 00 00 00 00 00 |................|
 |00000030| f9 67 1c 65                                     |.g.e            |
 +--------+-------------------------------------------------+----------------+
 示例包的数据如上，我猜测格式如下：
 1. n_sids 8
 2. gtid 16
 3. num intervals 8
 4. start-interval 8
 5. end_interval+1 8
 6. crc32 4
 
 麻蛋，这个连mysql代码都不解析？不可能啊
 */
public class PreviousGtidsData implements EventData {
    private GtidSet gtidSet;
    
    public GtidSet getGtidSet ( ) {
        return gtidSet;
    }
    
    public void setGtidSet ( GtidSet gtidSet ) {
        this.gtidSet = gtidSet;
    }
    
    @Override
    public String toString ( ) {
        return "PreviousGtidsData{" +
                "gtidSet=" + gtidSet.toString () +
                '}';
    }
}
