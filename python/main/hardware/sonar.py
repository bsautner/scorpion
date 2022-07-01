#!/usr/bin/env python3
import json
import time

import adafruit_hcsr04
import board

# Sonar
left_front_sonar = adafruit_hcsr04.HCSR04(trigger_pin=board.D22, echo_pin=board.D11)
right_front_sonar = adafruit_hcsr04.HCSR04(trigger_pin=board.D22, echo_pin=board.D19)
front_sonar = adafruit_hcsr04.HCSR04(trigger_pin=board.D22, echo_pin=board.D15)
down_front_sonar = adafruit_hcsr04.HCSR04(trigger_pin=board.D22, echo_pin=board.D5)
sonars = [left_front_sonar, right_front_sonar, front_sonar, down_front_sonar]
running = False
topic = "SONAR"
dist = [0.0, 0.0, 0.0, 0.0]

class Sonar:
    def __init__(self, f, l, r, d):
        self.f = f
        self.l = l
        self.r = r
        self.d = d


def sonar_run(mqtt):
    print("Started Sonar Thread...")
    while True:
        try:

            dist[0] = front_sonar.distance
            time.sleep(.1)

            dist[1] = left_front_sonar.distance
            time.sleep(.1)

            dist[2] = right_front_sonar.distance
            time.sleep(.1)

            dist[3] = down_front_sonar.distance
            time.sleep(.1)

            srs = Sonar(dist[0], dist[1], dist[2], dist[3])
            payload = json.dumps(srs.__dict__)
            # print(payload)
            mqtt.publish(topic=topic, payload=payload)

        except RuntimeError:
            print("Retrying Sonar...")

