#!/bin/bash

cd /home/ec2-user/app
DOCKER_APP_NAME=yello

# Redis 서버 체크
#EXIST_REDIS=$(sudo docker-compose -p ${DOCKER_APP_NAME}-redis -f docker-redis.yml ps | grep Up)
#
## Redis down 상태면 Redis up
#if [ -z "$EXIST_REDIS" ]; then
#  echo "[$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)] 작동중인 Redis가 존재하지 않습니다." >> /home/ec2-user/deploy.log
#  echo "[$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)] Redis 이미지를 빌드합니다." >> /home/ec2-user/deploy.log
#  sudo docker-compose -p ${DOCKER_APP_NAME}-redis -f docker-redis.yml up -d --build
#  sleep 10
#  echo "[$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)] Redis가 작동됩니다." >> /home/ec2-user/deploy.log
#fi

# BLUE 서버 체크
EXIST_BLUE=$(sudo docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml ps | grep Up)
echo "[$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)] 배포를 시작합니다." >> /home/ec2-user/deploy.log

# GREEN이 실행중이면 BLUE up
if [ -z "$EXIST_BLUE" ]; then
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

echo "[$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)] 배포를 종료합니다." >> /home/ec2-user/deploy.log
echo "[$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)] 배포 프로세스 완료 =====================" >> /home/ec2-user/deploy.log
echo >> /home/ec2-user/deploy.log
