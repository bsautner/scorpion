#!/usr/bin/env python
import time

import vocals.lights as lights
import network.mqtt as mqtt

def on_disconnect():
    print("mqtt disconnected")

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("Connected to MQTT Broker!")
        client.subscribe("#")
    else:
        print("Failed to connect to mqtt, return code %d\n", rc)

def on_message(self, userdata, msg):
    print(msg.topic + " " + str(msg.payload))


lights.off()
lights.blink(1, 255 * 255)

mqtt.connect(on_connect, on_message, on_disconnect)


while True:
    time.sleep(1)

