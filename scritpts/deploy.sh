#!/usr/bin/env bash

mvn clean package

sudo scp -i /mnt/c/Users/dimik/Downloads/sweater-london-1-2.pem /mnt/c/Users/dimik/IdeaProjects/Sweater/target/Sweater-1.0-SNAPSHOT.jar ec2-user@ec2-3-8-5-173.eu-west-2.compute.amazonaws.com:~

echo 'Restart server...'

sudo ssh -i /mnt/c/Users/dimik/Downloads/sweater-london-1-2.pem ec2-user@ec2-3-8-5-173.eu-west-2.compute.amazonaws.com << EOF

pgrep java | xargs kill -9
nohup java -jar Sweater-1.0-SNAPSHOT.jar > log.txt &

EOF

echo 'Bye'