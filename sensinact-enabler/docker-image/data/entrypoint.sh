#!/bin/bash

TPL_FILE="/opt/sensinact/configuration.tpl.json"
CONFIG_FILE="/opt/sensinact/configuration/configuration.json"

HTTP_PORT=${HTTP_PORT:-8080}
MQTT_HOST=${MQTT_HOST:-"localhost"}
MQTT_PORT=${MQTT_PORT:-1883}
MQTT_TOPIC=${MQTT_TOPIC:-"assist-iot/cheaas/#"}

cp "$TPL_FILE" "$CONFIG_FILE"
sed -i 's/{{ http_port }}/'"$HTTP_PORT"'/g' "$CONFIG_FILE"
sed -i 's/{{ mqtt_host }}/"'"$MQTT_HOST"'"/g' "$CONFIG_FILE"
sed -i 's/{{ mqtt_port }}/'"$MQTT_PORT"'/g' "$CONFIG_FILE"
sed -i 's@{{ mqtt_topic }}@"'"$MQTT_TOPIC"'"@g' "$CONFIG_FILE"

java -Dsensinact.config.dir=configuration -jar launch/launcher.jar
exit $?
