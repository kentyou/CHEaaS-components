# Deliverable for ASSIST-IoT CHEaaS

This repository is a companion of the ASSIST-IoT CHEaaS project deliverable #2.

## License

This project is released under the terms of the [Eclipse Public License v2.0](./LICENSE).

## Content

* `data-sender`: A Python script that can replay recorded data.
* `rest-health-endpoint`: Implementation of the ASSIST-IoT enablers REST endpoint, providing status information and metrics.
* `sensinact-run`: Run environment for an Eclipse sensiNact instance with CHEaaS support.
* `sensinact-enabler`: A docker image definition and an Helm Chart to deploy it in an ASSIST-IoT Kubernetes architecture.

## Eclipse sensiNact composition setup

1. Compile the REST health endpoint with Maven:
   ```bash
   cd rest-health-endpoint/
   mvn clean install
   cd ..
   ```
2. Prepare the Eclipse sensiNact instance repository
   ```bash
   cd sensinact-run/
   mvn clean prepare-package
   ```
3. Configure Eclipse sensiNact by editing `sensinact-run/configuration/configuration.json`
   * Make sure the MQTT configuration is correct, in the `sensinact.southbound.mqtt~mqtt-cheaas` entry.
   See the [Eclipse sensiNact MQTT client documentation](https://eclipse-sensinact.readthedocs.io/en/latest/southbound/mqtt/mqtt-client.html#configuration) for more details.
4. Run the Eclipse sensiNact instance
   ```bash
   cd sensinact-run/
   docker compose up -d
   ```
5. Query the Eclipse sensiNact REST endpoints:
   * Check the instance health (should return HTTP status 200)
     ```bash
     curl http://localhost:8080/v1/health
     ```
   * Check the providers (CHE) inside Eclipse sensiNact:
     ```bash
     curl http://localhost:8080/sensinact/providers
     ```

     More details on the Eclipse sensiNact REST endpoint are available here:
     [Eclipse sensiNact REST northbound documentation](https://eclipse-sensinact.readthedocs.io/en/latest/northbound/RestDataAccess.html)

## Payload replay

To test the bridge, you can replay a sample of the CHE data available in the [ASSIST-IoT OnlyOffice server](https://onlyoffice.assist-iot.eu/).

### Run the sender script locally

1. Install Python 3.12 (might work with newer versions)
2. Install dependencies: `python -m pip install paho-mqtt tqdm`
    * `paho-mqtt`: the [Eclipse Paho MQTT client](https://eclipse.dev/paho/index.php?page=clients/python/index.php)
    * `tqdm`: [TQDM](https://tqdm.github.io/) progress bar handler
3. Check the arguments documentation: `python data-sender/sender.py --help`
4. Download a data sample. We will then consider its name is `payload.json`
5. Run the sender script:
   `python data-sender/sender.py -i payload.json`

### Run the sender script in Docker

1. Download a data sample. We will then consider its name is `payload.json`
2. Copy `payload.json` to the `data-sender/data` folder
3. Update the `data-sender/compose.yaml` file to ensure that:
   * the input payload binding is correct
   * the script `--input` argument points to the right place
   * if necessary, add arguments to connect a specific MQTT broker
4. Compile the image: `docker compose -f data-sender/compose.yaml build`
5. Run the sender: `docker compose -f data-sender/compose.yaml up -d`

## References

* [ASSIST-IoT project](https://assist-iot.eu/)
* [Eclipse sensiNact documentation](https://eclipse-sensinact.readthedocs.io/)
* [Eclipse sensiNact source code repository](https://github.com/eclipse/org.eclipse.sensinact.gateway)
* [Eclipse sensiNact Docker image by Kentyou](https://github.com/kentyou/eclipse-sensinact-container)
