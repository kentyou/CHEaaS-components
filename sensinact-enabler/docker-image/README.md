# Docker image of Assist-IoT CHEaaS Eclipse sensiNact Enabler

## Build image

You can use the `./build.sh` script that:

1. Prepares the Eclipse sensiNact repository: `mvn -f data/pom.xml clean prepare-package`
2. Builds the image: `docker build -t sensinact:0.0.2-SNAPSHOT-CHEAAS .`

## Container configuration

Configuration is done via environment variables:

* `HTTP_PORT`: Port of the HTTP REST Northbound of Eclipse sensiNact
* `MQTT_HOST`: Hostname of the MQTT broker
* `MQTT_PORT`: Port of the MQTT broker
* `MQTT_TOPIC`: Topic to listen to (can be a filter)
