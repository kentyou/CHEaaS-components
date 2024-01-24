#!/bin/bash

IMAGE=${IMAGE:-"sensinact"}
TAG=${TAG:-"0.0.2-SNAPSHOT-CHEAAS"}

mvn -f data/pom.xml clean prepare-package || exit 1

docker build -t "$IMAGE:$TAG" . || exit 1
