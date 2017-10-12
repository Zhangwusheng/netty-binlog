package util

import (
    "github.com/golang/glog"
    "hash/fnv"
    "strconv"
    "reflect"
)

// 存储命令行参数
type Args struct {
    Concurrency int
    Number_of_query int
    Number_of_commit int
    Table string
    DSN string
    Command string
}

// 从Dispatcher传递给Executor的消息结构
// Type取值如下：
//		'I' - insert
//		'U' - update
//		'D' - delete
//		'Q' - goroutine exit
type Message struct {
	Type byte
	Before []interface{}
	After []interface{}
}

// 一个辅助函数，判断interface{}真实类型，之后转换为string
// 按理说返回error,string更好，但是用起来麻烦，不如panic
func to_string(any interface{}) string {
	switch any.(type) {
	case string:
		return any.(string)
	case int:
		return strconv.Itoa(any.(int))
	case int64:
		return strconv.FormatInt(any.(int64), 10)
	default:
		glog.Fatalf("invlid type: %v\n", reflect.TypeOf(any))
		panic("invalid type")
	}

	// you never reach here	
	return ""
}

func hash_mod(s string, mod int) int {
    h := fnv.New32a()
    h.Write([]byte(s))
    return int(h.Sum32()) % mod
}




