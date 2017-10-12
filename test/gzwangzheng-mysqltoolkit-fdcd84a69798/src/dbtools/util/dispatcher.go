package util

import (
    _ "github.com/go-sql-driver/mysql"
    "github.com/golang/glog"
    "encoding/binary"
    "database/sql"
)

// 存储表模式定义中行的定义
type ColumnDef struct {
    Name string
    Position int
    Type string
    Length int
}

// 存储表模式定义中主键的信息
type PKDef struct {
    Name string
    Position int
}

type Dispatcher struct {
	dsn string
	table string
	column_schema []ColumnDef
	pk_schema []PKDef
	db *sql.DB
	rander *Rander 
	channels []chan Message
    concurrency int

    // 已经插入的Primary Key
    pk_map map[string][]interface{}
}

func NewDispatcher(dsn, table string, concurrency int) *Dispatcher {
	dispatcher := &Dispatcher{}
    dispatcher.concurrency = concurrency
	dispatcher.dsn = dsn
	dispatcher.table = table
    dispatcher.rander = NewRander()
    dispatcher.pk_map = make(map[string][]interface{})
	return dispatcher
}

// 设置chan数组，dispatcher根据主键取模，然后根据索引发生到对应的chan
func (self *Dispatcher) SetChannels(channels []chan Message) {
	self.channels = channels
}

// 循环生成数据
func (self *Dispatcher) Run(loop int) error {
    var message Message
    var pk string
	
    var pkvalues []interface{}
    for i := 0; i < loop; i++ {
        // 随机生成记录、记录的主键的值、以及记录主键转换成字符串后的值
		message.Before, pkvalues, pk = self.gen_insert_values()
        if _, ok := self.pk_map[pk]; !ok {
            message.Type = 'I'
            message.After = nil
            self.pk_map[pk] = pkvalues
            self.channels[hash_mod(pk, self.concurrency)] <- message
        } else {
            //message.Type = 'U'
            //message.Before = self.pk_map[pk]
            //message.After = self.gen_update_values(message.Before)
            continue
        }
        
        // 随机执行Update操作
        if i % 5 == 0 {
            for pk, pkvalues := range self.pk_map {
                message.Type = 'U'
                message.Before = pkvalues
                message.After = self.gen_update_values(pkvalues)
                self.channels[hash_mod(pk, self.concurrency)] <- message
                delete(self.pk_map, pk)
                break
            }

            if len(self.pk_map) > 100000 {
                glog.Infof("clear pk_map...\n")
                self.pk_map = nil
                self.pk_map = make(map[string][]interface{})
            }
        }
	}

    // send poison pill
    message.Type = 'Q'
    message.Before = nil
    message.After = nil
	for i := 0; i < self.concurrency; i++ {
		self.channels[i] <- message
	}

    for i := 0; i < self.concurrency; i++ {
        <- self.channels[i]
    }

    glog.Infof("dispatcher exit!\n")
	return nil
}

// 加载表的模式定义，因为每个Executor需要作为一个协程运行，因此
// 我们在外部加载表的模式信息
func (self *Dispatcher)LoadSchema() error {
	// connect mysql server
    db, err := sql.Open("mysql", self.dsn)
    if err != nil {
        glog.Fatalf("Open database error: %s\n", err)
        return err
    }
    self.db = db
    defer self.db.Close()

    err, self.column_schema = self.load_column_info()
    if err != nil {
    	return err
    } 

    err, self.pk_schema = self.load_pk_info()
    if err != nil {
    	return err
    } 

    glog.Infof("column schema: %v\n", self.column_schema)
    glog.Infof("pk schema: %v\n", self.pk_schema)
	return nil
}

func (self *Dispatcher)load_column_info() (error, [] ColumnDef) {
	var schema [] ColumnDef

    rows, err := self.db.Query("select ordinal_position, column_name, data_type, coalesce(character_maximum_length, 0) from information_schema.columns where concat(table_schema, '.', table_name)='" + self.table + "'")
    if err != nil {
        glog.Fatalf("query `%s` schema information failed, err: %s\n", self.table, err)
        return err, nil
    }
    
    var columndef ColumnDef
    for rows.Next() {
        err = rows.Scan(&columndef.Position, &columndef.Name, &columndef.Type, &columndef.Length)
        if err != nil {
        	glog.Fatalf("rows.Scan failed, err: %s\n", err)
        	return err, nil
    	}

    	schema = append(schema, columndef)
    }

    // glog.Infof("schema: %v\n", schema)
	return nil, schema
}

func (self *Dispatcher)load_pk_info() (error, [] PKDef) {
	var schema [] PKDef

    rows, err := self.db.Query("select k.column_name as name, c.ordinal_position as position from information_schema.key_column_usage k join information_schema.columns c where k.column_name=c.column_name and concat(k.table_schema, '.', k.table_name) = '" + self.table + "' order by c.ordinal_position")
    if err != nil {
        glog.Fatalf("query `%s` primary key information failed, err: %s\n", self.table, err)
        return err, nil
    }
    
    var pkdef PKDef
    for rows.Next() {
        err = rows.Scan(&pkdef.Name, &pkdef.Position)
        if err != nil {
        	glog.Fatalf("rows.Scan failed, err: %s\n", err)
        	return err, nil
    	}

    	schema = append(schema, pkdef)
    }

    // glog.Infof("primary key: %v\n", schema)
	return nil, schema
}

// 根据表的模式生成insert语句
func (self *Dispatcher) GenInsertSQL() string {
	var insert_sql = "insert ignore into " + self.table + " values("

	for index, _ := range self.column_schema {
		if index != 0 {
			insert_sql += ","
		}
		insert_sql += "?"
	}

	insert_sql += ")"
	return insert_sql
}

// 根据表的模式生成insert语句
func (self *Dispatcher) GenUpdateSQL() string {
    var update_sql = "update " + self.table + " set "

    for index, column := range self.column_schema {
        if index != 0 {
            update_sql += ","
        }
        update_sql += column.Name + "=?"
    }

    update_sql += " where "

    for index, column := range self.pk_schema {
        if index != 0 {
            update_sql += " and "
        }
        update_sql += column.Name + "=?"
    }

    glog.Infof("SQL: %s\n", update_sql)
    return update_sql
}

// 根据表的模式生成插入的数据
func (self *Dispatcher) gen_insert_values() ([] interface{}, [] interface{}, string) {
	var values [] interface{}
    var pkvalues [] interface{}
	var pkstr string

	for _, column := range self.column_schema {
		if column.Type == "char" || column.Type == "varchar" {
			values = append(values, self.rander.RandomString(column.Length-1))
		} else if column.Type == "int" {
			values = append(values, self.rander.RandomInt(2147483647))
		} else if column.Type == "bigint" {
			values = append(values, self.rander.RandomInt64(9223372036854775807))
		} else if column.Type == "date" {
			values = append(values, self.rander.Date())
		} else if column.Type == "datetime" {
			values = append(values, self.rander.Datetime())
		} else if column.Type == "blob" {
            values = append(values, Int64ToBytes(self.rander.RandomInt64(9223372036854775807)))
        }
	}

	for _, pk := range self.pk_schema {
		if pkstr != "" {
			pkstr += ":"
		}
		pkstr += to_string(values[pk.Position-1])
        pkvalues = append(pkvalues, values[pk.Position-1])
	}
	return values, pkvalues, pkstr 
}

// 根据表的模式生成更新的数据
func (self *Dispatcher) gen_update_values(pkvalues []interface{}) []interface{} {
    var values [] interface{}

    for _, column := range self.column_schema {
        if column.Type == "char" || column.Type == "varchar" {
            values = append(values, self.rander.RandomString(column.Length-1))
        } else if column.Type == "int" {
            values = append(values, self.rander.RandomInt(2147483647))
        } else if column.Type == "bigint" {
            values = append(values, self.rander.RandomInt64(9223372036854775807))
        } else if column.Type == "date" {
            values = append(values, self.rander.Date())
        } else if column.Type == "datetime" {
            values = append(values, self.rander.Datetime())
        } else if column.Type == "blob" {
            values = append(values, Int64ToBytes(self.rander.RandomInt64(9223372036854775807)))
        }
    }

    i := 0
    for _, pk := range self.pk_schema {
        values[pk.Position-1] = pkvalues[i]
        i++
    }
    return values
}

func Int64ToBytes(i int64) []byte {
    var buf = make([]byte, 8)
    binary.BigEndian.PutUint64(buf, uint64(i))
    return buf
}

func BytesToInt64(buf []byte) int64 {
    return int64(binary.BigEndian.Uint64(buf))
}

