nohup java -Xms100M -Xmx100M -Dspring.profiles.active=sensitive,sensitive-server -jar investing-data-loader-1.0.0-SNAPSHOT.jar >& /dev/null &
echo $! > investing-dataloader.pid

