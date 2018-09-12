#!/usr/bin/env bash

source ~/.bashrc

printf "stopping..\n" >> bot.log
./gradlew --stop >> bot.log

printf "pulling from git..\n" >> bot.log
git pull >> gitpull.log
"---------------------" >> gitpull.log

printf "starting..\n" >> bot.log
nohup ./gradlew run > /dev/null 2>&1 &
