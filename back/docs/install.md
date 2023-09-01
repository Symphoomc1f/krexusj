## 物联网安装

### 创建数据库 用户

```shell script
./mysql -h127.0.0.1  -e "create user 'hc_things'@'%' identified by 'hc_things12345678';"
./mysql -h127.0.0.1  -e "flush privileges;"
./mysql -h127.0.0.1  -e "CREATE DATABASE hc_things ;"
./mysql -h127.0.0.1  -e "grant all privileges on hc_things.* to 'hc_things'@'%' ;"

 wget https://dl.winqi.cn/hc/iot/hc_things.sql1

mv hc_things.sql1 hc_things.sql

./mysql -h127.0.0.1  -e "use hc_things; source /home/mysql/mysql/bin/hc_things.sql"
```

### 添加域名映射

```shell script
vim /etc/hosts

127.0.0.1   iot.homecommunity.cn
```

### 下载jar包

```shell script
su hc

mkdir iot

cd iot 

mkdir bin

mkdir face


cd bin

vim start_iot.sh

nohup java -jar -Dspring.profiles.active=dev -Dcache -Xms256m -Xmx512m ../Things.jar > ../things.log $2 2>&1 &

tail -f ../things.log

cd ~/iot/

 wget https://dl.winqi.cn/hc/iot/Things.jar

cd bin

sh start_iot.sh

```

## lanproxy 安装( 宇泛设备只能局域网内通讯所以需要部署内网穿透软件)

### 创建docker-compose 文件

```shell script
mkdir lanproxy
cd lanproxy
vim docker-compose.yml

version: '3.1'
services:
  lanproxy-client:
    image: franklin5/lanproxy-server
    container_name: lanproxy-server
    environment:
     - LANPROXY_USERNAME=admin
     - LANPROXY_PASSWORD=admin
    volumes:
     - /usr/local/docker/lanproxy-server/config-data:/home/hc/.lanproxy
    ports:
     - 8090:8090
     - 4900:4900
     - 4993:4993
     - 9000-9100:9000-9100
    restart: always

docker-compose up -d 
```

