import time
from math import atan2, degrees

import board
import adafruit_lis3mdl
import numpy as np

i2c = board.I2C()  # uses board.SCL and board.SDA
sensor = adafruit_lis3mdl.LIS3MDL(i2c)

M_Ainv = np.array(   [[1.047365, 0.041783,-0.139412],
                 [0.041783, 1.071033, 0.058439],
                 [0.005152, 0.022216, 1.045404]])
M_B = np.array([-0.058439, 0.058439, 0.713624])

def vector_2_degrees(x, y):
   angle = degrees(atan2(y, x))
   if angle < 0:
        angle += 360
   return angle


def get_heading(_sensor):
    magnet_x, magnet_y, magnet_z = _sensor.magnetic
    Mxyz = [magnet_x, magnet_y, magnet_z]
    temp = [0.0, 0.0, 0.0]

    for i in range(3):
        temp[i] = (Mxyz[i] - M_B[i])
        Mxyz[0] = M_Ainv[0][0] * temp[0] + M_Ainv[0][1] * temp[1] + M_Ainv[0][2] * temp[2]
        Mxyz[1] = M_Ainv[1][0] * temp[0] + M_Ainv[1][1] * temp[1] + M_Ainv[1][2] * temp[2]
        Mxyz[2] = M_Ainv[2][0] * temp[0] + M_Ainv[2][1] * temp[1] + M_Ainv[2][2] * temp[2]
    return vector_2_degrees( Mxyz[0] ,  Mxyz[1])


def run():
    while True:
        print("heading: {:.2f} degrees".format(get_heading(sensor)))
        time.sleep(1.0)



if __name__ == '__main__':
    run()
