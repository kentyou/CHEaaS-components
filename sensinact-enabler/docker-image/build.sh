#!/bin/bash

IMAGE=${IMAGE:-"sensinact"}
TAG=${TAG:-"0.0.2-SNAPSHOT-CHEAAS"}

echo "Building Health endpoint..."
mvn -f ../../rest-health-endpoint/pom.xml clean install || exit 1

echo "Constructing repository..."
mvn -f pom.xml clean prepare-package || exit 1

echo "Building image..."
docker build -t "$IMAGE:$TAG" . || exit 1
echo "Done."
