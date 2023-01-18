#!/bin/bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)

source ${ABSDIR}/profile.sh
source ${ABSDIR}/switch.sh

IDLE_PORT=$(find_switch_port)

echo "> 새롭게 실행한 애플리케이션 health 확인 " >> /home/ec2-user/deploy.log
echo "> 실행 포트 : $IDLE_PORT" >> /home/ec2-user/deploy.log

for CNT in {1..10}
do
    echo "> health 확인용 반복문 시작... $CNT 회" >> /home/ec2-user/deploy.log
    UP=$(curl -s http://127.0.0.1:${IDLE_PORT}/application/health | grep 'UP')
    if [ -z "${UP}" ]
    then
        echo "> 아직 애플리케이션이 시작되지 않았습니다." >> /home/ec2-user/deploy.log
    else
        echo "> 애플리케이션이 정상적으로 실행되었습니다." >> /home/ec2-user/deploy.log
        switch_nginx_proxy
        break ;
    fi

    sleep 10
done

if [ $(CNT) -eq 10 ]
then
    echo "> 애플리케이션 실행에 실패했습니다." >> /home/ec2-user/deploy.log
    exit 1
fi