#!/usr/bin/env python
import threading
import time

import network.mqtt as mqtt
import mag_303 as mag


def on_disconnect():
    print("mqtt disconnected")

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("Connected to MQTT Broker")

        threading.Thread(target=mag.run, args=(mqtt,)).start()
        # sonar.sonar_run(mqtt)
    else:

        print("Failed to connect to mqtt, return code %d\n", rc)

def on_message(self, userdata, msg):

    cmd = bytes(msg.payload).decode()




if __name__ == '__main__':
    mqtt.connect(on_connect, on_message, on_disconnect)

    while True:
        time.sleep(1)

