#!/usr/bin/env bash

PROJECT_ROOT="/home/ec2-user/app"
WAR_FILE="$PROJECT_ROOT/app-0.0.1-SNAPSHOT.war"

APP_LOG="$PROJECT_ROOT/application.log"
ERROR_LOG="$PROJECT_ROOT/error.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +%c)

# build 파일 복사
echo "$TIME_NOW > $WAR_FILE 파일 복사" >> $DEPLOY_LOG
cp $PROJECT_ROOT/deploy/*.war $WAR_FILE
cp $PROJECT_ROOT/keystore.p12 /keystore.p12

# 실행권한 추가
chmod +x $WAR_FILE

# war 파일 실행
echo "$TIME_NOW > $WAR_FILE 파일 실행" >> $DEPLOY_LOG
nohup java -jar $WAR_FILE > $APP_LOG 2> $ERROR_LOG &

CURRENT_PID=$(pgrep -f $WAR_FILE)
echo "$TIME_NOW > 실행된 프로세스 아이디 $CURRENT_PID 입니다." >> $DEPLOY_LOG