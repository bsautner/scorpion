#!/usr/bin/env python
import threading
import time

import network.mqtt as mqtt
import time
import board
import adafruit_lis3mdl
from math import atan2, degrees

i2c = board.I2C()  # uses board.SCL and board.SDA
sensor = adafruit_lis3mdl.LIS3MDL(i2c)

def on_disconnect():
    print("mqtt disconnected")

def on_connect(client, userdata, flags, rc):
    if rc == 0:

        print("Connected to MQTT Broker!")
        # client.subscribe("#")


        threading.Thread(target=run, args=()).start()

    else:
        print("Failed to connect to mqtt, return code %d\n", rc)

def on_message(self, userdata, msg):

    cmd = bytes(msg.payload).decode()
    print(cmd)

def run():
    while True:
        mag_x, mag_y, mag_z = sensor.magnetic
        heading = get_heading(sensor)
        mqtt.publish("MAG", heading)
        time.sleep(1)

def vector_2_degrees(x, y):
    angle = degrees(atan2(y, x))
    if angle < 0:
           angle += 360
    return angle


def get_heading(_sensor):
    magnet_x, magnet_y, _ = _sensor.magnetic
    return vector_2_degrees(magnet_x, magnet_y)


if __name__ == '__main__':
    mqtt.connect(on_connect, on_message, on_disconnect)

    while True:
        time.sleep(1)

