package main

import (
    _ "github.com/go-sql-driver/mysql"
    "github.com/garyburd/redigo/redis"
    "github.com/golang/glog"
    "database/sql"
    "runtime"
    "strings"
    "flag"
)

// 命令行参数
var mysql_address string
var table_name string
var redis_address string

// 数据库实例

// redis实例
var redis_client redis.Conn

func init() {
    flag.StringVar(&mysql_address, "mysql", "", "mysql address")
    flag.StringVar(&table_name, "table", "", "mysql table name")
    flag.StringVar(&redis_address, "redis", "", "redis address")
}

func main() {
    runtime.GOMAXPROCS(runtime.NumCPU())
    defer glog.Flush()
    flag.Parse()

    glog.Infof("check mysql `%s` and redis `%s` ...\n", mysql_address, redis_address)
    
    glog.Infof("connect mysql server ...\n")
    db, err := sql.Open("mysql", mysql_address)
    if err != nil {
        glog.Fatalf("Open database error: %s\n", err)
        return
    }
    defer db.Close()

    glog.Infof("connect redis ...\n")
    redis_client, err = redis.Dial("tcp", redis_address)
    if err != nil {
        glog.Errorf("create redis client failed, err: %v\n", err)
        return
    }
    defer redis_client.Close()

    glog.Infof("iterate table `%s`...\n", table_name)
    rows, err := db.Query("select * from t")
    if err != nil {
        glog.Fatalf("query `%s` schema information failed, err: %s\n", table_name, err)
        return
    }

    var a, b, c, d string
    for rows.Next() {
        err = rows.Scan(&a, &b, &c, &d)
        if err != nil {
            glog.Fatalf("rows.Scan failed, err: %s\n", err)
            return
        }

        key := b + "_" + a
        value := c + "_" + d

        glog.Infof("key = %s, value = %s\n", key, value)

        value_of_redis, err := redis.String(redis_client.Do("get", key))
        if err != nil {
            glog.Errorf("get key `%s` failed, err: %v\n", key, err)
            return
        }

        if strings.Compare(value, value_of_redis) != 0 {
            glog.Fatalf("db verify failed!\n")
            return
        }
    }
}
