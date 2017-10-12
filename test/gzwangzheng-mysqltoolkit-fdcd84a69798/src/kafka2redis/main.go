package main

import (
    "github.com/garyburd/redigo/redis"
	"github.com/golang/protobuf/proto"
    "github.com/Shopify/sarama"
    "github.com/golang/glog"
    "runtime"
    // "strings"
    "flag"
    // "fmt"
    // "os"
    . "kafka2redis/pb"
)

// 命令行参数
var kafka_address string
var kafka_topic string
var redis_address string

// Kafka实例
var consumer sarama.Consumer
var partitionConsumer sarama.PartitionConsumer

// redis实例
var redis_client redis.Conn

// 存储表模式
var pk_map map[string]int32 = nil

func init() {
    flag.StringVar(&kafka_address, "kafka", "", "kafka address")
    flag.StringVar(&kafka_topic, "topic", "", "kafka topic")
    flag.StringVar(&redis_address, "redis", "", "redis address")
}

func main() {
	runtime.GOMAXPROCS(runtime.NumCPU())
    defer glog.Flush()
    flag.Parse()

    glog.Infof("import from kafka `%s` to redis `%s`...\n", kafka_address, redis_address)
    
    glog.Infof("connect kafka `%s` ...\n", kafka_address)
    consumer, err := sarama.NewConsumer([]string{kafka_address}, nil)
    if err != nil {
    	glog.Errorf("create kafka consumer failed, err: %v\n", err)
    	return
    }
    defer consumer.Close()

    glog.Infof("consume topic `%s` ...\n", kafka_topic)
    partitionConsumer, err = consumer.ConsumePartition(kafka_topic, 0, sarama.OffsetNewest)
    if err != nil {
    	glog.Errorf("create kafka consumer partition failed, err: %v\n", err)
    	return
    }
    defer partitionConsumer.Close()

    glog.Infof("create redis instance: %s\n", redis_address)
    redis_client, err = redis.Dial("tcp", redis_address)
    if err != nil {
        glog.Errorf("create redis client failed, err: %v\n", err)
        return
    }
    defer redis_client.Close()

    for {
    	msg := <-partitionConsumer.Messages()
    	event := &BinlogEvent{}
        
        if err := proto.Unmarshal(msg.Value, event); err != nil {
            glog.Fatalln("Failed to parse BinlogEvent:", err)
        }

        glog.Infof("key = %s, type = %s\n", string(msg.Key[:]), event.Header.EventType)

        if event.Header.EventType == EventType_QUERY_EVENT || event.Header.EventType == EventType_XID_EVENT {
            continue
        }

        if pk_map ==  nil {
            pk_map = make(map[string]int32)
            for _, k := range event.RowEvent.PrimaryKey {
                pk_map[k.Name] = k.Position
            }
        }
        handle_row_event(event.Header.EventType, event.RowEvent)
	}
}

func handle_row_event(event_type EventType, row_event *RowEvent) error {
    if event_type == EventType_WRITE_ROW_EVENT {
        return handle_write_row_event(row_event)
    } else if  event_type == EventType_UPDATE_ROW_EVENT {
        return handle_update_row_event(row_event)
    } else if event_type == EventType_DELETE_ROW_EVENT {
        return handle_delete_row_event(row_event)
    } else {
        panic("invalid row event type")
    }
}

func handle_write_row_event(event *RowEvent) error {
    records := event.After

    for _, record := range records {
        var value string = ""
        var key string = ""
        for _, item := range record.Items {
            if value != "" {
                value += "_"
            }
            if _, found := pk_map[item.Name]; found {
                continue
            }
            value += item.Value
        }

        for _, pk := range event.PrimaryKey {
            if key != "" {
                key += "_"
            }
            key += record.Items[pk.Position-1].Value
        }
        _, err := redis_client.Do("set", key, value)
        if err != nil {
            glog.Errorf("set key failed, err: %v\n", err)
            panic("redis op failed")
        }
    }

    return nil
}

func B2S(bs []uint8) string {
    ba := make([]byte,0, len(bs))
    for _, b := range bs {
        ba = append(ba, byte(b))
    }
    return string(ba)
}

func handle_update_row_event(event *RowEvent) error {
    // before_records := event.Before
    after_records := event.After

    // 首先从redis获取key：value，判断是否存在 
    /*
    for _, record := range before_records {
        var value string = ""
        var key string = ""
        for _, item := range record.Items {
            if value != "" {
                value += "_"
            }
            if _, found := pk_map[item.Name]; found {
                continue
            }
            value += item.Value
        }

        for _, pk := range event.PrimaryKey {
            if key != "" {
                key += "_"
            }
            key += record.Items[pk.Position-1].Value
        }

        tmp_value, err := redis.String(redis_client.Do("get", key))
        if err != nil {
            glog.Errorf("get key `%s` failed, err: %v\n", key, err)
            panic("redis op failed")
        }

        if strings.Compare(value, tmp_value) != 0 {
            glog.Errorf("value dismatch, key = %s, new value = %s, old value = %s\n", key, value, tmp_value)
            panic("exit!")
        }
    }
    */

    // 插入更新后的值
    for _, record := range after_records {
        var value string = ""
        var key string = ""
        for _, item := range record.Items {
            if value != "" {
                value += "_"
            }
            if _, found := pk_map[item.Name]; found {
                continue
            }
            value += item.Value
        }

        for _, pk := range event.PrimaryKey {
            if key != "" {
                key += "_"
            }
            key += record.Items[pk.Position-1].Value
        }
        _, err := redis_client.Do("set", key, value)
        if err != nil {
            glog.Errorf("set key failed, err: %v\n", err)
            panic("redis op failed")
        }
    }

    return nil
}

func handle_delete_row_event(event *RowEvent) error {
    columns := event.Before
    glog.Infof("delete record: %v\n", columns)
    return nil
}

