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
package com.zws.binlog.event.deserialization;

import com.zws.binlog.util.ByteUtil;
import com.zws.binlog.event.data.TableMapEventData;
import com.zws.binlog.event.data.UpdateRowsEventData;
import io.netty.buffer.ByteBuf;

import java.io.Serializable;
import java.util.*;

//

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class UpdateRowsEventDataDeserializer extends AbstractRowsEventDataDeserializer<UpdateRowsEventData> {

    private boolean mayContainExtraInformation;

    public UpdateRowsEventDataDeserializer(Map<Long, TableMapEventData> tableMapEventByTableId) {
        super(tableMapEventByTableId);
    }

    public UpdateRowsEventDataDeserializer setMayContainExtraInformation(boolean mayContainExtraInformation) {
        this.mayContainExtraInformation = mayContainExtraInformation;
        return this;
    }

//    @Override
    public UpdateRowsEventData deserialize(ByteBuf inputStream) {
        UpdateRowsEventData eventData = new UpdateRowsEventData();
//        eventData.setTableId(inputStream.readLong(6));
        eventData.setTableId(ByteUtil.readUnsignedLong(inputStream,6));
        inputStream.skipBytes(2); // reserved
        if (mayContainExtraInformation) {
//            int extraInfoLength = inputStream.readInteger(2);
            int extraInfoLength = ByteUtil.readInteger(inputStream,2);
            inputStream.skipBytes(extraInfoLength - 2);
        }
//        int numberOfColumns = inputStream.readPackedInteger();
        int numberOfColumns = ByteUtil.readPackedInteger(inputStream);
        BitSet bitSet1 =ByteUtil.readBitSet(inputStream,numberOfColumns, true);
//        eventData.setIncludedColumnsBeforeUpdate(inputStream.readBitSet(numberOfColumns, true));
        eventData.setIncludedColumnsBeforeUpdate(bitSet1);

        BitSet bitSet2 =ByteUtil.readBitSet(inputStream,numberOfColumns, true);
//        eventData.setIncludedColumns(inputStream.readBitSet(numberOfColumns, true));
        eventData.setIncludedColumns(bitSet2);
        eventData.setRows(deserializeRows(eventData, inputStream));
        return eventData;
    }

    private List<Map.Entry<Serializable[], Serializable[]>> deserializeRows(UpdateRowsEventData eventData,
                                                                            ByteBuf inputStream)  {
        long tableId = eventData.getTableId();
        BitSet includedColumnsBeforeUpdate = eventData.getIncludedColumnsBeforeUpdate(),
               includedColumns = eventData.getIncludedColumns();
        List<Map.Entry<Serializable[], Serializable[]>> rows =
                new ArrayList<Map.Entry<Serializable[], Serializable[]>>();
//        while (inputStream.available() > 0) {
            while (inputStream.readableBytes() > CHECKSUM_LENGTH) {
            rows.add(new AbstractMap.SimpleEntry<Serializable[], Serializable[]>(
                    deserializeRow(tableId, includedColumnsBeforeUpdate, inputStream),
                    deserializeRow(tableId, includedColumns, inputStream)
            ));
        }
        return rows;
    }

}
