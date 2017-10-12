package util

import (
    "math/rand"
    "time"
)

type Rander struct {
	rander *rand.Rand	
}

func NewRander() *Rander {
	r := &Rander{}
	r.rander = rand.New(rand.NewSource(time.Now().UnixNano()))
	return r
}

// 生成随机字符串
func (self *Rander) RandomString(length int) string {
    data := make([]byte, length)
    var num int 

    for i := 0; i < length; i++ {
        num = self.rander.Intn(57) + 65
        for {
            if num>90 && num<97 {
                num = self.rander.Intn(57) + 65
            } else {
                break
            }   
        }   
        data[i] = byte(num)
    }   
    return string(data)
}

// 生成随机数
func (self *Rander) RandomInt(max int) int {
    return self.rander.Intn(max)
}

func (self *Rander) RandomInt64(max int64) int64 {
    return self.rander.Int63n(max)
}

// 取当前时间
func (self *Rander) Datetime() string {
	t := time.Now()
	return t.Format("2006-01-02 15:04:05")
}

func (self *Rander) Date() string {
	t := time.Now()
	return t.Format("2006-01-02")
}