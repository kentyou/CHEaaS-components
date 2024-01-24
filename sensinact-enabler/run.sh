#!/bin/bash

export IMAGE=${IMAGE:-"sensinact"}
export TAG=${TAG:-"0.0.2-SNAPSHOT-CHEAAS"}

pushd docker-image
./build.sh || exit 1
popd

if [ -f ./values.yaml ]
then
    echo "Using values.yaml"
    helm install sensinact ./sensinact-helm --values ./values.yaml "$@" || exit 1
else
    echo "Using image $IMAGE:$TAG"
    helm install sensinact ./sensinact-helm --set "image.repository=$IMAGE" --set "image.tag=$TAG" "$@" || exit 1
fi
