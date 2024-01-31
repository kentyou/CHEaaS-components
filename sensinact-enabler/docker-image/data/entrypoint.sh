#!/bin/bash

TPL_FILE="/opt/sensinact/configuration.tpl.json"
CONFIG_FILE="/opt/sensinact/configuration/configuration.json"

HTTP_PORT=${HTTP_PORT:-8080}
MQTT_HOST=${MQTT_HOST:-"localhost"}
MQTT_PORT=${MQTT_PORT:-1883}
MQTT_TOPIC_FILTER=${MQTT_TOPIC_FILTER:-"assist-iot/cheaas/#"}
MQTT_TOPIC_V1=${MQTT_TOPIC_V1:-"assist-iot/cheaas/v1/#"}
MQTT_TOPIC_V2=${MQTT_TOPIC_V2:-"assist-iot/cheaas/v2"}
MQTT_TOPIC_V3=${MQTT_TOPIC_V3:-"assist-iot/cheaas/v3"}

cp "$TPL_FILE" "$CONFIG_FILE"
sed -i 's/{{ http_port }}/'"$HTTP_PORT"'/g' "$CONFIG_FILE"
sed -i 's/{{ mqtt_host }}/"'"$MQTT_HOST"'"/g' "$CONFIG_FILE"
sed -i 's/{{ mqtt_port }}/'"$MQTT_PORT"'/g' "$CONFIG_FILE"
sed -i 's@{{ mqtt_topic_filter }}@"'"$MQTT_TOPIC_FILTER"'"@g' "$CONFIG_FILE"
sed -i 's@{{ mqtt_topic_v1 }}@"'"$MQTT_TOPIC_V1"'"@g' "$CONFIG_FILE"
sed -i 's@{{ mqtt_topic_v2 }}@"'"$MQTT_TOPIC_V2"'"@g' "$CONFIG_FILE"
sed -i 's@{{ mqtt_topic_v3 }}@"'"$MQTT_TOPIC_V3"'"@g' "$CONFIG_FILE"

java -Dsensinact.config.dir=configuration -jar launch/launcher.jar
exit $?
