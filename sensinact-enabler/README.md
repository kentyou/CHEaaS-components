# Assist-IoT CHEaaS Eclipse sensiNact Enabler

This repository contains:

1. [`docker-image`](./docker-image/): the definition of the Eclipse sensiNact Docker image ready for the Assist-IoT CHEaaS project.
2. [`sensinact-helm`](./sensinact-helm/): the definition of an Helm Chart to integrate the Docker image as an Enabler in the Assist-IoT architecture.

## Usage

1. Build the Docker image. You can use the `IMAGE` and `TAG` environment variables to match your needs, for example by specifying a custom image repository:
   ```bash
   cd docker-image
   IMAGE=custom-repository:5000/sensinact TAG=0.0.2-SNAPSHOT-CHEAAS ./build.sh
   cd ..
   ```
2. Override the Helm Chart default values by creating a `values.yaml` file, which can contain the following entries:
   * `image`
     * `repository`: name of the image to execute, including repository if needed, but without the tag. This should match the `IMAGE` environment variable passed in step 1. (default: `sensinact`).
     * `tag`: tag of the image to execute. This should match the `TAG` environment variable passed in step 1. (default: `0.0.2-SNAPSHOT-CHEAAS`)
   * `mqtt_host`: Host of the MQTT broker providing updates from CHE
   * `mqtt_port`: Port of the MQTT broker providing updates from CHE
   * `mqtt_topic`: Topic filter to subscribe to updates from CHE
   * `service`: definition of the service to access the Eclipse sensiNact REST Northbound. Defaults to a `LoadBalancer` bound to port 8080.

   For example:
   ```yaml
   image:
     repository: docker-registry:5000/sensinact
     tag: "0.0.2-SNAPSHOT-CHEAAS"

   mqtt_host: edbe-edgedatabrokerx64-vernemq
   mqtt_topic: assist-iot/cheaas/#
   ```
3. Start the Helm Chart:
   ```bash
   helm install sensinact ./sensinact-helm --values ./values.yaml
   ```
4. Test the presence of the instance connecting <http://localhost:8080/sensinact/provider>

### Reminder: how to start the Edge Data Broker Enabler

The Eclipse sensiNact enabler requires the Edge Data Broker Enabler to be up and running to be able to receive messages from the Container Handling Equipment.
Here are the commands to execute to start it:

1. Register the ASSIST-IoT public repository as an Helm repository: `helm repo add assist-public-repo https://gitlab.assist-iot.eu/api/v4/projects/85/packages/helm/stable`
2. Deploy the EDBE Helm Chart: `helm install edbe assist-public-repo/edgedatabrokerx64`

**Note:** Depending on the K3S setup, it can be necessary to indicate to the Helm command where to find the Kubernetes configuration using `--kubeconfig /etc/rancher/k3s/k3s.yaml`.

## Running with a local Docker registry

If necessary, here are the actions to take to use a local Docker registry.
We will consider that the registry server host name is `docker-registry` and is known and accessible on port 5000.

1. On the `docker-registry` host, start the registry:
   ```bash
   docker run --restart always -p 5000:5000 registry:2.8
   ```
2. On the machine where the Helm command will be executed, in the folder of this repository:
   0. Make sure the Edge Data Broker Enabler is up and running (it can be started afterwards)
   1. Write the `values.yaml` file:
      ```yaml
      image:
         repository: docker-registry:5000/sensinact
         tag: "0.0.2-SNAPSHOT-CHEAAS"

      mqtt_host: edbe-edgedatabrokerx64-vernemq
      mqtt_topic: assist-iot/cheaas/#
      ```
   2. Build the Docker image and install the Helm chart:
      ```bash
      export IMAGE="docker-registry:5000/sensinact"
      export TAG="0.0.2-SNAPSHOT-CHEAAS"
      ./run.sh
      ```
   3. Check if Eclipse sensiNact is accessible correctly:
      ```bash
      curl -s http://localhost:8080/sensinact/providers
      ```

      You should see at least the `sensiNact` provider, plus any CHE sending updates to the Edge Data Broker Enabler.
