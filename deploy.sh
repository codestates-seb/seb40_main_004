#!/bin/bash

REPOSITORY= /home/ec2-user/app/server/morak_back_end

sudo cd $REPOSITORY

echo "> GIT STASH"
git stash

echo "> GIT PULL"
git pull

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

echo "> GRADLE CHMOD 777"
sudo chmod 777 ./gradlew

echo "> GRADLE CLEAN"
sudo ./gradlew clean

echo "> GRADLE BUILD"
sudo ./gradlew build


JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep '.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

echo "> $JAR_PATH 배포"
shdo nohup java -jar /home/ec2-user/app/server/morak_back_end/build/libs/morak_back_end-0.0.1-SNAPSHOT.jar > /dev/null 2> /dev/null < /dev/null &
