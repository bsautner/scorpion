#!/usr/bin/env python
import time

import network.mqtt as mqtt
import hardware.led as led
import threading
import hardware.sonar as sonar
import hardware.motors as motors
import camera.start_camera as camera

def on_disconnect():
    print("mqtt disconnected")

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("Connected to MQTT Broker")
        client.subscribe("#")
        threading.Thread(target=sonar.sonar_run, args=(mqtt,)).start()
        # threading.Thread(target=camera.run()).start()

        # sonar.sonar_run(mqtt)
    else:

        print("Failed to connect to mqtt, return code %d\n", rc)

def on_message(self, userdata, msg):

    cmd = bytes(msg.payload).decode()
    # print(cmd)
    # pygame.mouse.set_pos((random.choice(range(600)), random.choice(range(600))))

    if cmd == "ACK":
        threading.Thread(target=led.blink, args=(led.GREEN, 5, .2)).start()

    if cmd == "STOP":
        threading.Thread(target=motors.stop()).start()

    if cmd == "RIGHT":
        threading.Thread(target=motors.right()).start()

    if cmd == "LEFT":
       threading.Thread(target=motors.left()).start()

    if cmd == "FORWARD":
            threading.Thread(target=motors.forward()).start()

    if cmd == "REVERSE":
        threading.Thread(target=motors.reverse()).start()



if __name__ == '__main__':
    mqtt.connect(on_connect, on_message, on_disconnect)

    while True:
        time.sleep(1)

