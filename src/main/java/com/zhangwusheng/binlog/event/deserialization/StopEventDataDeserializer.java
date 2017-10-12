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

import com.zhangwusheng.binlog.event.data.StopEventData;
import io.netty.buffer.ByteBuf;

/**
 * @author <a href="mailto:pprasse@actindo.de">Patrick Prasse</a>
 */
public class StopEventDataDeserializer implements EventDataDeserializer<StopEventData > {
    
    public StopEventData deserialize ( ByteBuf msg ) {
        StopEventData stopEventData = new StopEventData ( );
        return stopEventData;
    }
}
