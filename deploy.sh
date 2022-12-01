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

# git clone 받은 위치로 이동
cd $REPOSITORY/$PROJECT_NAME/


echo "> ll"
sudo ls -al


echo "> GRADLE CHMOD 777"
sudo chmod 777 ./gradlew

echo "> GRADLE CLEAN"
sudo ./gradlew clean

echo "> GRADLE BUILD"
sudo ./gradlew build

# jar 파일 위치로 이동
cd build/libs

# jar 백그라운드 실행 
nohup java -jar morak_back_end-0.0.1-SNAPSHOT.jar 2>&1 &
