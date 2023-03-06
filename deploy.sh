#!/bin/bash

REPOSITORY=/home/ec2-user/app/server
PROJECT_NAME=morak_back_end

# git clone 받은 위치로 이동
cd $REPOSITORY/$PROJECT_NAME/

APP_NAME=morak_back_end
CURRENT_PID=$(pgrep -f $APP_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 종료할것 없음."
else
  echo "> kill -9 $CURRENT_PID"
  sudo kill -15 $CURRENT_PID
  sleep 5
fi

# jar 파일 위치로 이동
cd $REPOSITORY/$PROJECT_NAME/build/libs

echo "> JAR BUILD"
sudo nohup java -jar morak_back_end-0.0.1-SNAPSHOT.jar >> /home/ec2-user/deploy.log 2>/home/ec2-user/deploy_err.log &
