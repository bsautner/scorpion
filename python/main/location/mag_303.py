import time
from math import atan2

# SPDX-FileCopyrightText: 2019 Bryan Siepert for Adafruit Industries
#
# SPDX-License-Identifier: MIT

from time import sleep
import board
import busio
import adafruit_lsm303_accel
import adafruit_lis2mdl

i2c = busio.I2C(board.SCL, board.SDA)
mag = adafruit_lis2mdl.LIS2MDL(i2c)
accel = adafruit_lsm303_accel.LSM303_Accel(i2c)
pi = 3.14159
targetHeading = 0.0


M_B = [ -7.55,-15.43, 27.08]
M_Ainv = [[0.993, 0.017, -0.016], [0.017, 0.995, -0.009], [-0.011, -0.011, 1.021]]

def run(mqtt):
    while True:
        # print("Acceleration (m/s^2): X=%0.3f Y=%0.3f Z=%0.3f"%accel.acceleration)
        # print("Magnetometer (micro-Teslas)): X=%0.3f Y=%0.3f Z=%0.3f"%mag.magnetic)
        # print("")
        x, y, z = mag.magnetic
        Mxyz = [x, y, z]
        temp = [0.0,0.0,0.0]

        for i in range(3):
            temp[i] = (Mxyz[i] - M_B[i])

        Mxyz[0] = M_Ainv[0][0] * temp[0] + M_Ainv[0][1] * temp[1] + M_Ainv[0][2] * temp[2]
        Mxyz[1] = M_Ainv[1][0] * temp[0] + M_Ainv[1][1] * temp[1] + M_Ainv[1][2] * temp[2]
        Mxyz[2] = M_Ainv[2][0] * temp[0] + M_Ainv[2][1] * temp[1] + M_Ainv[2][2] * temp[2]

        heading = (atan2(Mxyz[1], Mxyz[0]) * 180) / pi

        if heading < 0:
            heading = 360 + heading

        print(heading)
        mqtt.publish(topic="MAG", payload=heading)
        sleep(0.5)



if __name__ == '__main__':
    run()