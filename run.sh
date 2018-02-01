#!/bin/sh
type="test"
if [ $# -gt 0 ];then
    type=$1
fi
if [ $type == 'prod' ];then
    mvn clean install -Dmaven.test.skip=true -Pprod
    scp -r ./target/scheduler-zk-service-1.0-SNAPSHOT.war root@192.168.1.5:/data/tomcat/
fi
if [ $type == 'test' ]; then
    mvn clean install  -Dmaven.test.skip=true -Ptest
    scp -r ./target/scheduler-zk-service-1.0-SNAPSHOT.war work@192.168.1.2:/data/tmp/
fi
