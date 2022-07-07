import argparse
import queue
import sys
import threading
import time
import paho.mqtt.publish as publish

import network.mqtt as mqtt

def run():
    while True:

        publish.single(topic="ben", payload="boo", hostname="mind")
        print("tick")
        time.sleep(1)


def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("Connected to MQTT Broker...")
        client.subscribe("#")
        threading.Thread(target=run())


        # sonar.sonar_run(mqtt)
    else:

        print("Failed to connect to mqtt, return code %d\n", rc)

def on_message(self, userdata, msg):
    print("messaged")

def on_disconnect():
    print("mqtt disconnected")

if __name__ == '__main__':
    mqtt.connect(on_connect, on_message, on_disconnect)

    while True:
        time.sleep(1)