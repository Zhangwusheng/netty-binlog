mkdir -p /data2/mysql-data/5.7.18

#mv /usr/local/mysql /usr/local/mysql-5.7.18

ln -fs /usr/local/mysql-5.7.18 /usr/local/mysql
ln -fs  /data2/mysql-data/5.7.18 /usr/local/mysql/data

groupadd mysql
useradd -g mysql mysql

/usr/local/mysql/bin/mysqld --defaults-file=/usr/local/mysql/my.cnf --initialize --user=mysql


root@localhost: ukR9P4d1,wri


chown -R mysql:mysql /usr/local/mysql-5.7.18

 /usr/local/mysql/bin/mysqld --defaults-file=/usr/local/mysql/my.cnf  --user=mysql

 /usr/local/mysql/bin/mysql -uroot -p -P3333 -h127.0.0.1


alter user 'root'@'localhost' identified by 'zhangwusheng';
alter user 'root'@'127.0.0.1' identified by 'zhangwusheng';


GRANT REPLICATION SLAVE ON *.* TO 'repl'@'%' IDENTIFIED BY 'repl';
FLUSH PRIVILEGES;

GRANT REPLICATION CLIENT ON *.* TO 'repl'@'%';
FLUSH PRIVILEGES;


show binary logs;
show binlog events in  'mysql-bin.000002';



 /usr/local/mysql/bin/mysql -urepl -p -P3333 -h127.0.0.1

/usr/local/mysql/bin/mysqld --defaults-file=/usr/local/mysql/my.cnf --user=mysql &

tcpdump -i wlp3s0 -nn -X 'port 3333 and  host 192.168.1.105' -w mysql2.cap
