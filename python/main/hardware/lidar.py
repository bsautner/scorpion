import time
import digitalio
import board
import RPi.GPIO as GPIO
from math import cos, sin, pi, floor
from adafruit_rplidar import RPLidar
from adafruit_rplidar import RPLidarException
from time import sleep

PORT_NAME = '/dev/ttyUSB0'

# used to scale data to fit on the screen
scan_data = [0] * 360
motor = digitalio.DigitalInOut(board.D4)
motor.direction = digitalio.Direction.OUTPUT

def collect_data(mqtt):
    lidar = RPLidar(None, PORT_NAME)
    try:

        motor.value = False
        sleep(2)
        motor.value = True
        sleep(2)

        print(lidar.info)
        #
        for scan in lidar.iter_scans():
            mqtt.publish(topic="LIDAR", payload=scan)

    except RPLidarException:
        if lidar is not None:
            lidar.stop()
            lidar.disconnect()
            collect_data()
    except KeyboardInterrupt:
        print('Stopping.')
        lidar.stop()
        lidar.disconnect()

if __name__ == '__main__':

    print("hello lidar")


    collect_data()

    while True:

        time.sleep(1)


