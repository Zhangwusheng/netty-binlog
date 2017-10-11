/*
 * Copyright 2013 Patrick Prasse
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhangwusheng.binlog.event.deserialization;

//import com.github.shyiko.mysql.binlog.event.GtidEventData;
//import com.github.shyiko.mysql.binlog.io.ByteArrayInputStream;
import com.zhangwusheng.ByteUtil;
import com.zhangwusheng.binlog.event.data.GtidEventData;
import com.zhangwusheng.binlog.event.data.NullEventData;
import com.zhangwusheng.binlog.handler.BinlogEventHandler;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:pprasse@actindo.de">Patrick Prasse</a>
 */
public class NullEventDataDeserializer implements EventDataDeserializer<NullEventData > {
    
    private Logger log = LoggerFactory.getLogger ( NullEventDataDeserializer.class );
    
    public NullEventData deserialize ( ByteBuf msg )  {
    
        
        log.info ( "msg size:"+msg.readableBytes () );
        
        return new NullEventData ();
        
    }
}
