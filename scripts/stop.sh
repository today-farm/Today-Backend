#!/bin/bash

echo "> stop 시작" >> /home/ec2-user/deploy.log

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

IDLE_PROFILE=$(find_profile)

echo "> 실행할 profile : $IDLE_PROFILE " >> /home/ec2-user/deploy.log

CONTAINER_ID=$(sudo docker ps -f "ancestor=${IDLE_PROFILE}" -q)

if [ -z $CONTAINER_ID ]
then
    echo "> 기존에 실행되고 있던 컨테이너가 없습니다. " >> /home/ec2-user/deploy.log
else
    echo "> 기존에 실행되고 있던 컨테이너 : $CONTAINER_ID" >> /home/ec2-user/deploy.log
    sudo docker stop $CONTAINER_ID
    sudo docker rm $CONTAINER_ID
    echo "> 컨네이너 종료 완료, $CONTAINER_ID" >> /home/ec2-user/deploy.log
    sudo docker image rm $IDLE_PROFILE
    echo "> 기존 이미지 삭제 완료, $IDLE_PROFILE" >> /home/ec2-user/deploy.log
    sleep 10
fi