if [ $# -ne 1 ]; then
    echo "run.sh <kafka2redis|dbverify|dbtools>"
    exit -1
fi

if [ $1 == 'kafka2redis' ]; then
    echo "run kafka2redis ..."
    bin/kafka2redis -alsologtostderr=true -kafka=tserver2:9092 -topic=binlog -redis=vserver2:6379
elif [ $1 == 'dbtools' ]; then
    echo "run dbtools ..."
    bin/dbtools -alsologtostderr=true -concurrency=10 -number_of_query=10000 -number_of_commit=5 -table=test.tt -dsn="alex:alex123@tcp(vserver1:3306)/test?autocommit=false"
elif [ $1 == 'dbverify' ]; then
	echo "run dbverify ..."   
	bin/dbverify -alsologtostderr=true -mysql="alex:alex123@tcp(vserver1:3306)/test" -redis=vserver2:6379
else
    echo "invalid command"
fi

# bin/kafka2redis -alsologtostderr=true -kafka=tserver2:9092 -topic=binlog -redis=vserver2:6379
# bin/dbtools -alsologtostderr=true -concurrency=10 -number_of_query=100000000 -number_of_commit=5 -table=test.t -dsn="alex:alex123@tcp(vserver1:3306)/test?autocommit=false"
