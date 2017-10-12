package util

import (
    _ "github.com/go-sql-driver/mysql"
    "github.com/golang/glog"
    "database/sql"
)

// 封装工作线程
type Executor struct {
	rid int
	dsn string
	insert_sql string
    update_sql string
    number_of_commit int
	channel chan Message
    
    // 各类DB处理的Handler
    db *sql.DB
    tx *sql.Tx
    i_stmt *sql.Stmt
    u_stmt *sql.Stmt
}

func NewExecutor(rid int, dsn string) *Executor {
	executor := &Executor{}
	executor.rid = rid
	executor.dsn = dsn
	return executor
}

func (self *Executor) Run() {
	glog.Infof("[routine#%d] run with channel#%v...\n", self.rid, self.channel)
	
    // 连接数据库
    var err error
    self.db, err = sql.Open("mysql", self.dsn)
    if err != nil {
        glog.Fatalf("[routine#%d] Open database error: %s\n", self.rid, err)
        return
    }
    defer self.db.Close()

   	// 预编译SQL
    self.tx, err = self.db.Begin()
    if err != nil {
        glog.Errorf("[routine#%d] tx begin failed, err: %v\n", self.rid, err)
        return
    }

    self.i_stmt, err = self.tx.Prepare(self.insert_sql)
    if err != nil {
        glog.Errorf("[routine#%d] sql `%s` prepare failed, err: %v\n", self.rid, self.insert_sql, err)
        return
    }
    
    self.u_stmt, err = self.tx.Prepare(self.update_sql)
    if err != nil {
        glog.Errorf("[routine#%d] sql `%s` prepare failed, err: %v\n", self.rid, self.update_sql, err)
        return
    }

    glog.Infof("[routine#%d] get values...\n", self.rid)

    i := 0
    for {
    	message := <- self.channel
    	if message.Type ==  'Q' { 
            glog.Infof("[routine#%d] get poison pill ...\n", self.rid)
            self.channel <- message
            break 
        }
    
        if message.Type == 'I' {
            glog.Infof("[routine#%d] insert: %v\n", self.rid, message)
    	    _, err = self.i_stmt.Exec(message.Before...)
    	    if err != nil {
        	   glog.Errorf("[routine#%d] sql exec failed, err: %v\n", self.rid, err)
               panic("err")
        	   return
    	    }
        } else if message.Type == 'U' {
            glog.Infof("[routine#%d] update: %v\n", self.rid, message)
            _, err = self.u_stmt.Exec(append(message.After, message.Before...)...)
            if err != nil {
               glog.Errorf("[routine#%d] sql exec failed, err: %v\n", self.rid, err)
               return
            }
        }
        
    	i++
    	if i % self.number_of_commit == 0 {
    		self.i_stmt.Close()
            self.u_stmt.Close()
            self.tx.Commit()
            self.tx, _ = self.db.Begin()
            self.i_stmt, _ = self.tx.Prepare(self.insert_sql)
            self.u_stmt, _ = self.tx.Prepare(self.update_sql)
            i = 0
    	}
    }

	self.i_stmt.Close()
    self.u_stmt.Close()
    self.tx.Commit()
}

func (self *Executor) SetChannel(channel chan Message) {
    self.channel = channel
}

func (self *Executor) SetNumberOfCommit(num int) {
    self.number_of_commit = num
}

func (self *Executor) SetInsertSQL(sql string) {
	self.insert_sql = sql
}

func (self *Executor) SetUpdateSQL(sql string) {
    self.update_sql = sql
}