/*
 * Copyright 2013 Stanley Shyiko
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

import com.zhangwusheng.ByteUtil;
import com.zhangwusheng.binlog.event.data.TableMapEventData;
import com.zhangwusheng.binlog.event.data.WriteRowsEventData;
import io.netty.buffer.ByteBuf;

import java.io.Serializable;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class WriteRowsEventDataDeserializer extends AbstractRowsEventDataDeserializer<WriteRowsEventData> {

    private boolean mayContainExtraInformation;

    public WriteRowsEventDataDeserializer(Map<Long, TableMapEventData> tableMapEventByTableId) {
        super(tableMapEventByTableId);
    }

    public WriteRowsEventDataDeserializer setMayContainExtraInformation(boolean mayContainExtraInformation) {
        this.mayContainExtraInformation = mayContainExtraInformation;
        return this;
    }


    public WriteRowsEventData deserialize(ByteBuf inputStream)  {
        WriteRowsEventData eventData = new WriteRowsEventData();
        long tableId = ByteUtil.readUnsignedLong(inputStream,6);
        eventData.setTableId(tableId);
//        eventData.setTableId(inputStream.readLong(6));
        inputStream.skipBytes(2); // reserved
        if (mayContainExtraInformation) {
//            int extraInfoLength = inputStream.readInteger(2);
            int extraInfoLength = ByteUtil.readInteger(inputStream,2);
            inputStream.skipBytes(extraInfoLength - 2);
        }
//        int numberOfColumns = inputStream.readPackedInteger();
        int numberOfColumns = ByteUtil.readPackedInteger(inputStream);
        BitSet bitSet = ByteUtil.readBitSet(inputStream,numberOfColumns,true);
        eventData.setIncludedColumns(bitSet);
//        eventData.setIncludedColumns(inputStream.readBitSet(numberOfColumns, true));
        eventData.setRows(deserializeRows(eventData.getTableId(), eventData.getIncludedColumns(), inputStream));

        inputStream.skipBytes(CHECKSUM_LENGTH);
        return eventData;
    }

    private List<Serializable[]> deserializeRows(long tableId, BitSet includedColumns, ByteBuf inputStream)
    {
        List<Serializable[]> result = new LinkedList<Serializable[]>();
        while (inputStream.readableBytes() > CHECKSUM_LENGTH) {
            result.add(deserializeRow(tableId, includedColumns, inputStream));
        }
        return result;
    }

}
