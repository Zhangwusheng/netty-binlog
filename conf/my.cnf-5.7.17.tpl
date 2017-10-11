# Generated by Percona Configuration Wizard (http://tools.percona.com/) version REL5-20120208
# Configuration name server-12 generated for zhangwusheng@sina.com at 2017-03-09 02:21:53

[mysql]

# CLIENT #
port                           = 3333
socket                         = /usr/local/mysql/mysql.sock

[mysqld]

# GENERAL #
user                           = mysql
port                           = 3333
default-storage-engine         = InnoDB
socket                         = /usr/local/mysql/mysql.sock
pid-file                       = /usr/local/mysql/mysql.pid

# MyISAM #
key-buffer-size                = 128M
#myisam-recover                 = FORCE,BACKUP

# SAFETY #
max-allowed-packet             = 16M
max-connect-errors             = 1000000

# DATA STORAGE #
datadir                        = /usr/local/mysql/data

# BINARY LOGGING #
log-bin                        = /usr/local/mysql/data/mysql-bin
expire-logs-days               = 14
sync-binlog                    = 1


# REPLICATION #
skip-slave-start               = 1
log-slave-updates              = 1
relay-log                      = /usr/local/mysql/data/relay-bin
slave-net-timeout              = 60

# CACHES AND LIMITS #
tmp-table-size                 = 32M
max-heap-table-size            = 32M
query-cache-type               = 0
query-cache-size               = 0
max-connections                = 500
thread-cache-size              = 50
open-files-limit               = 65535
table-definition-cache         = 4096
table-open-cache               = 256

# INNODB #
innodb-flush-method            = O_DIRECT
innodb-log-files-in-group      = 2
innodb-log-file-size           = 128M
innodb-flush-log-at-trx-commit = 1
innodb-file-per-table          = 1
innodb-buffer-pool-size        = 2G

# LOGGING #
log-error                      = /usr/local/mysql/data/mysql-error.log
log-queries-not-using-indexes  = 1
slow-query-log                 = 1
slow-query-log-file            = /usr/local/mysql/data/mysql-slow.log
log                            = /usr/local/mysql/mysql.log

# MYSELF #
log-slave-updates=1    
binlog_format=row  
gtid_mode=on                 #开启gtid模式
enforce_gtid_consistency=on  #强制gtid一致性，开启后对于特定create table不被支持
bind-address=0.0.0.0

server_id=143
general_log=on
general_log_file = /usr/local/mysql/data/general.log

relay_log_info_repository = TABLE
master_info_repository    = TABLE
relay_log_recovery        = on

