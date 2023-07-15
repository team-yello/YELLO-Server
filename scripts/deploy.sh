#!/bin/bash

cd /home/ec2-user/app
DOCKER_APP_NAME=yello

# BLUE 서버 체크
EXIST_BLUE=$(sudo docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml ps | grep Up)
echo "[$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)] 배포를 시작합니다." >> /home/ec2-user/deploy.log

# Redis 컨테이너가 실행 중인지 확인
REDIS_RUNNING=$(sudo docker ps -q -f "name=redis" -f "expose=6379")

# Redis 컨테이너가 실행 중이면 일시적으로 종료
if [ -n "$REDIS_RUNNING" ]; then
  echo "[$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)] Redis 컨테이너를 일시적으로 종료합니다." >> /home/ec2-user/deploy.log
  sudo docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml stop redis
  sudo docker-compose -p ${DOCKER_APP_NAME}-green -f docker-compose.green.yml stop redis
fi

# GREEN이 실행중이면 BLUE up
if [ -z "$EXIST_BLUE" ]; then
  REDIS_GREEN_RUNNING=$(sudo docker-compose -p ${DOCKER_APP_NAME}-green -f docker-compose.green.yml ps | grep redis)

  if [ -n "$REDIS_GREEN_RUNNING" ]; then
      echo "[$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)] Green Redis 컨테이너를 종료합니다." >> /home/ec2-user/deploy.log
      sudo docker-compose -p ${DOCKER_APP_NAME}-green -f docker-compose.green.yml stop redis
  fi

  echo "[$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)] Blue 배포를 시작합니다." >> /home/ec2-user/deploy.log

  sudo docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml up -d --build
  sleep 30

  BLUE_HEALTH=$(sudo docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml ps | grep Up)
  if [ -z "$BLUE_HEALTH" ]; then
    sudo ./slack_blue.sh
  else
    echo "[$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)] Green 서버를 종료합니다." >> /home/ec2-user/deploy.log
    sudo docker-compose -p ${DOCKER_APP_NAME}-green -f docker-compose.green.yml down
    sudo docker image prune -af
    echo "[$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)] Green 서버를 종료했습니다." >> /home/ec2-user/deploy.log
  fi

# BLUE가 실행중이면 GREEN up
else
  REDIS_BLUE_RUNNING=$(sudo docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml ps | grep redis)

  if [ -n "REDIS_BLUE_RUNNING" ]; then
      echo "[$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)] Blue Redis 컨테이너를 종료합니다." >> /home/ec2-user/deploy.log
      sudo docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml stop redis
  fi

  echo "[$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)] Green 배포가 시작됩니다." >> /home/ec2-user/deploy.log
  sudo docker-compose -p ${DOCKER_APP_NAME}-green -f docker-compose.green.yml up -d --build
  sleep 30

  GREEN_HEALTH=$(sudo docker-compose -p ${DOCKER_APP_NAME}-green -f docker-compose.green.yml ps | grep Up)

  if [ -z "$GREEN_HEALTH" ]; then
      sudo ./slack_green.sh
  else
    echo "[$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)] Blue 서버를 종료합니다." >> /home/ec2-user/deploy.log
    sudo docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml down
    sudo docker image prune -af
    echo "[$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)] Blue 서버를 종료했습니다." >> /home/ec2-user/deploy.log
  fi
fi

# Redis 컨테이너가 종료되었을 경우 다시 시작
if [ -n "$REDIS_RUNNING" ]; then
  echo "[$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)] Redis 컨테이너를 다시 시작합니다." >> /home/ec2-user/deploy.log
  sudo docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml start redis
  sudo docker image prune -af
  echo "[$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)] Redis 컨테이너를 다시 시작했습니다." >> /home/ec2-user/deploy.log
fi

echo "[$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)] 배포를 종료합니다." >> /home/ec2-user/deploy.log
echo "[$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)] 배포 프로세스 완료 =====================" >> /home/ec2-user/deploy.log
echo >> /home/ec2-user/deploy.log
