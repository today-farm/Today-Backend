#!/bin/bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

function switch_nginx_proxy()
{
    IDLE_PORT=$(find_switch_port)

    echo "> 실행 포트 : $IDLE_PORT" >> /home/ec2-user/deploy.log
    echo "> /etc/nginx/conf.d/service-url.inc 변경" >> /home/ec2-user/deploy.log
    echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" | sudo tee /etc/nginx/conf.d/service-url.inc

    echo "> nginx 재시작" >> /home/ec2-user/deploy.log
    sudo service nginx restart
}