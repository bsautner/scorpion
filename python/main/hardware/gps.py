#!/usr/bin/env python
import threading
import time

import network.mqtt as mqtt
import time
import board
import serial,time
from io import StringIO

#initialization and open the port
ser = serial.Serial()
ser.port = "/dev/serial0"
ser.baudrate = 9600
ser.bytesize = serial.EIGHTBITS #number of bits per bytes
ser.parity = serial.PARITY_NONE #set parity check: no parity
ser.stopbits = serial.STOPBITS_TWO #number of stop bits
#ser.timeout = None          #block read
ser.timeout = 5               #non-block read
#ser.timeout = 2              #timeout block read
ser.xonxoff = False     #disable software flow control
ser.rtscts = False     #disable hardware (RTS/CTS) flow control
ser.dsrdtr = False       #disable hardware (DSR/DTR) flow control




def on_disconnect():
    print("mqtt disconnected")

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        ser.open()
        print("Connected to MQTT Broker!")
        # client.subscribe("#")

        threading.Thread(target=run, args=()).start()

    else:
        print("Failed to connect to mqtt, return code %d\n", rc)

def on_message(self, userdata, msg):

    cmd = bytes(msg.payload).decode()
    print(cmd)

def run():
    file_str = StringIO()
    while ser.isOpen():
        read_data = ser.read(1)
        s = bytes(read_data).decode()
        if (s != "\n"):
            file_str.write(s)
        else:
            if file_str.getvalue().startswith("$GNRMC"):
                print(file_str.getvalue())
                mqtt.publish("GPS", file_str.getvalue())
            file_str.close()
            file_str = StringIO()



    else:
        print("Can not open serial port")



if __name__ == '__main__':
    mqtt.connect(on_connect, on_message, on_disconnect)


    while True:
        time.sleep(1)

