#!/usr/bin/env python3
import threading
import time
import ack
import mqtt
# import sonar


def on_disconnect(client, userdata, rc):
    if rc != 0:
        print("Unexpected disconnection.")


def on_connect(self, mosq, obj, rc):
    print("MQTT Connected!")
    self.subscribe("COMMAND")
    mqtt.publish("robot", "online")
    time.sleep(1)
    # x = threading.Thread(target=sonar.sonar_run, args=(mqtt,))
    # x.start()


def on_message(self, userdata, msg):
    cmd = bytes(msg.payload).decode()


    print(msg.topic + " " + str(msg.payload))
    print(cmd)

    ack.ack()


if __name__ == '__main__':
    mqtt.connect(on_connect, on_message, on_disconnect)
    while True:
        time.sleep(1)
