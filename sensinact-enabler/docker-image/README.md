# Docker image of Assist-IoT CHEaaS Eclipse sensiNact Enabler

## Build image

1. Make sure that the REST health endpoint has been compiled: `mvn -f ../../rest-health-endpoint/pom.xml clean install`
2. Prepares the Eclipse sensiNact repository: `mvn clean prepare-package`
3. Builds the image: `docker build -t sensinact:0.0.2-SNAPSHOT-CHEAAS .`

## Container configuration

Configuration is done via environment variables:

* `HTTP_PORT`: Port of the HTTP REST Northbound of Eclipse sensiNact
* `MQTT_HOST`: Hostname of the MQTT broker
* `MQTT_PORT`: Port of the MQTT broker
* `MQTT_TOPIC`: Topic to listen to (can be a filter)
