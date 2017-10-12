package main

import (
    "github.com/golang/glog"
    // "os/signal"
    "runtime"
    "flag"
    "fmt"
    "os"
    "dbtools/util"
)

// table schema
var schema [] util.ColumnDef

// command line args
var args = &util.Args{}

func init() {
    flag.IntVar(&args.Concurrency, "concurrency", 1, "number of clients to simulate for query to run")
    flag.IntVar(&args.Number_of_query, "number_of_query", 100, "limit each client to this number of queries")
    flag.IntVar(&args.Number_of_commit, "number_of_commit", 1000, "commit records every X number of statements")
    flag.StringVar(&args.Table, "table", "", "table name")
    flag.StringVar(&args.DSN, "dsn", "", "data source name")
}

func main() {
    runtime.GOMAXPROCS(runtime.NumCPU())
    defer glog.Flush()

    flag.Parse()
    check_args()

    glog.Info("MySQL Stress Test ...")
    glog.Infof("load %s's schema information...", args.Table)

    dispatcher := util.NewDispatcher(args.DSN, args.Table, args.Concurrency)
    dispatcher.LoadSchema()
    insert_sql := dispatcher.GenInsertSQL()
    update_sql := dispatcher.GenUpdateSQL()

    // 创建多个channel, 并生成Executor协程，每个协程分配一个channel
    var channels []chan util.Message
    for i := 0; i < args.Concurrency; i++ {
        channel := make(chan util.Message)
        channels = append(channels, channel)
        
        go func(rid int, channel chan util.Message) {
            executor := util.NewExecutor(1000+rid, args.DSN)
            executor.SetInsertSQL(insert_sql)
            executor.SetUpdateSQL(update_sql)
            executor.SetChannel(channel)
            executor.SetNumberOfCommit(args.Number_of_commit)
            executor.Run()
        }(i, channel)
    }

    dispatcher.SetChannels(channels)

    // Dispatcher生成数据并发送到channel
    dispatcher.Run(args.Number_of_query)
    return

    // catch Ctrl+C
    /*
    terminate := make(chan os.Signal, 1)
    signal.Notify(terminate, os.Interrupt)
    <-terminate
    glog.Info("dbtools exit!")
    */
}

func check_args() {
    if (args.Concurrency < 1) {
        fmt.Println("concurrency must >= 1, die!")
        flag.PrintDefaults()
        os.Exit(-1)
    }

    if (args.Table == "") {
        fmt.Println("table is empty, die!")
        flag.PrintDefaults()
        os.Exit(-1)
    }

    if (args.DSN == "") {
        fmt.Println("dsn is empty, die!")
        flag.PrintDefaults()
        os.Exit(-1)
    }
    /*
    if (args.Command != "insert" && args.Command != "mixed") {
        fmt.Println("invalid command, must be `insert` or `mixed`")
        flag.PrintDefaults()
        os.Exit(-1)
    }
    */
}
