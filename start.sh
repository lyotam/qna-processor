#!/bin/bash

set -e

./mvnw clean install
echo "will now start qna-processor"
nohup ./mvnw spring-boot:run &> qna-processor.out &
printf "\e[32mDONE: Q&A Processor is running =]\e[0m\n"