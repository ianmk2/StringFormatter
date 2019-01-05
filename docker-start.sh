docker rm -f stringformatter
docker create --name stringformatter \
    -p 5000:5000/tcp \
    -v /data/stringformatter:/app \
    -e JAR_PATH="./stringformatter.jar" \
    -e JAVA_OTPS="-server -Xms256m -Xmx256m -XX:NewRatio=2 -Duser.timezone=Asia/Seoul -Dfile.encoding=utf-8 -Dserver.port=5000 -Dspring.servlet.multipart.maxRequestSize=20MB -Dspring.servlet.multipart.maxFileSize=20MB" \
    --restart=always \
ianmk2/docker-java-runner:1.0
docker start stringformatter