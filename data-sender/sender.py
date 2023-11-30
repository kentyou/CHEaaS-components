#!/usr/bin/env python3
#
# Copyright (c) 2023 Kentyou.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v2.0
# which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-v20.html
#
# Contributors:
#    Kentyou - initial API and implementation

import argparse
import json
import logging
import pathlib
import sys
import time
from dataclasses import dataclass
from typing import Any, Dict, List, Optional, cast

import paho.mqtt.client
import tqdm

__author__ = "thomas.calmant@kentyou.com"
__copyright__ = "Copyright 2023 Kentyou"


@dataclass
class MQTTConf:
    """
    Configuration of the MQTT link
    """
    topic: str = "assist-iot/push"
    host: str = "broker.hivemq.com"
    port: int = 1883
    qos: int = 1
    user: Optional[str] = None
    password: Optional[str] = None


def load_mqtt_conf(options: Any) -> MQTTConf:
    """
    Merges the default configuration with the one given by the user
    """
    conf = MQTTConf()

    if options.mqtt_host:
        conf.host = cast(str, options.mqtt_host)
    if options.mqtt_port is not None:
        conf.port = cast(int, options.mqtt_port)
    if options.mqtt_topic:
        conf.topic = cast(str, options.mqtt_topic)

    if options.mqtt_user:
        conf.user = cast(str, options.mqtt_user)
    if options.mqtt_password:
        conf.password = cast(str, options.mqtt_password)

    return conf


class MQTTClient:
    """
    MQTT client to send messages
    """

    def __init__(self, conf: MQTTConf) -> None:
        self.__conf = conf
        self.client: Optional[paho.mqtt.client.Client] = None

    def connect(self) -> None:
        """
        Connect the MQTT client
        """
        if self.client is not None:
            self.disconnect()

        self.client = paho.mqtt.client.Client(reconnect_on_failure=True)
        if self.__conf.user and self.__conf.password:
            self.client.username_pw_set(self.__conf.user, self.__conf.password)
        self.client.connect(self.__conf.host, self.__conf.port)
        logging.info("Connected to MQTT")

    def disconnect(self) -> None:
        """
        Disconnect MQTT client
        """
        if self.client is not None:
            self.client.disconnect()
            self.client = None

    def send_raw(self, data: bytes) -> None:
        """
        Send the data as is
        """
        if self.client is None:
            raise Exception("MQTT client is not connected")

        self.client.publish(self.__conf.topic, data, qos=self.__conf.qos)

    def send_json(self, data: Dict[str, Any]) -> None:
        """
        Sends JSON data as UTF-8
        """
        self.send_raw(json.dumps(data).encode("utf-8"))


def delta(previous: Dict[str, Any], new: Dict[str, Any]) -> str:
    """
    Computes a delta message
    """
    new_keys = set(new.keys())
    previous_keys = set(previous.keys())

    added = new_keys - previous_keys
    removed = previous_keys - new_keys
    others = new_keys & previous_keys

    messages = [f"--{k}" for k in removed]
    messages.extend(
        f"{k}:'{previous[k]}'->'{new[k]}'" for k in others if previous[k] != new[k]
    )
    messages.extend(f"++{k}" for k in added)
    return ", ".join(messages)


def main(argv=None) -> int:
    """
    Script entrypoint
    """
    parser = argparse.ArgumentParser()
    parser.add_argument("-d", "--debug", action="store_true", help="Debug mode")

    group = parser.add_argument_group("Input", "Input file")
    group.add_argument(
        "-i", "--input", required=True, type=pathlib.Path, help="Input file"
    )
    group.add_argument("--delay", type=float, default=1.0, help="Delay between entries")
    group.add_argument("--loop", action="store_true", help="Loop entries")

    group = parser.add_argument_group("MQTT", "Options to send data to MQTT")
    group.add_argument("--mqtt-host", help="MQTT broker host")
    group.add_argument("--mqtt-port", type=int, help="MQTT broker port")
    group.add_argument("--mqtt-topic", help="MQTT broker topic")
    group.add_argument(
        "--mqtt-qos", type=int, choices=(0, 1, 2), help="MQTT Quality of Service"
    )
    group.add_argument("--mqtt-user", help="MQTT broker user")
    group.add_argument("--mqtt-password", help="MQTT broker password")

    options = parser.parse_args(argv)

    if options.debug:
        logging.getLogger().setLevel(logging.DEBUG)

    # Options
    loop = cast(bool, options.loop)
    delay = cast(float, options.delay)

    # Check file
    input_file = cast(pathlib.Path, options.input)
    if not input_file.exists():
        logging.error("File not found: %s", input_file)
        return 1

    try:
        with open(input_file, "r", encoding="utf-8") as fd:
            data = cast(List[Dict[str, Any]], json.load(fd))
    except IOError as ex:
        logging.error("Error reading input file '%s': %s", input_file, ex)
        return 1

    # Prepare MQTT client
    mqtt_conf = load_mqtt_conf(options)
    mqtt = MQTTClient(mqtt_conf)

    print("Connecting to MQTT", mqtt_conf.host, "on port", mqtt_conf.port)

    try:
        # Connect MQTT
        mqtt.connect()

        run = True
        while run:
            run = loop
            print("Sending data from start...")
            previous = {}
            progress = tqdm.tqdm(data)
            for entry in progress:
                if entry != previous:
                    progress.write(f"Data changed: {delta(previous, entry)}")

                previous = entry
                mqtt.send_json(entry)
                time.sleep(delay)
            print("End of data.")
    except KeyboardInterrupt:
        print("Got Ctrl+C. Abandon.", file=sys.stderr)
        return 127
    finally:
        mqtt.disconnect()

    return 0


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO, format="[%(levelname)s] %(message)s")
    try:
        sys.exit(main())
    except KeyboardInterrupt:
        # Double Ctrl+C
        sys.exit(127)
