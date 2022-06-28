from time import sleep
import RPi.GPIO as GPIO

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)
pin_motor_1_a = 23  # GPIO12
pin_motor_1_b = 18  # GPIO16
pin_motor_2_a = 25  # GPIO18
pin_motor_2_b = 24  # GPIO25
pin_motor_3_a = 16  # GPIO36
pin_motor_3_b = 12  # GPIO32
pin_motor_4_a = 21  # GPOIO38
pin_motor_4_b = 20  # GPIO40

GPIO.setup(pin_motor_1_a, GPIO.OUT)
GPIO.setup(pin_motor_1_b, GPIO.OUT)
motor_1_f = GPIO.PWM(pin_motor_1_a, 1000)
motor_1_r = GPIO.PWM(pin_motor_1_b, 1000)
motor_1_f.start(0)
motor_1_r.start(0)
motor_a = [motor_1_f, motor_1_r]

GPIO.setup(pin_motor_2_a, GPIO.OUT)
GPIO.setup(pin_motor_2_b, GPIO.OUT)
motor_2_f = GPIO.PWM(pin_motor_2_a, 2000)
motor_2_r = GPIO.PWM(pin_motor_2_b, 2000)
motor_2_f.start(0)
motor_2_r.start(0)
motor_b = [motor_2_f, motor_2_r]

GPIO.setup(pin_motor_3_a, GPIO.OUT)
GPIO.setup(pin_motor_3_b, GPIO.OUT)
motor_3_f = GPIO.PWM(pin_motor_3_a, 3000)
motor_3_r = GPIO.PWM(pin_motor_3_b, 3000)
motor_3_f.start(0)
motor_3_r.start(0)
motor_c = [motor_3_f, motor_3_r]

GPIO.setup(pin_motor_4_a, GPIO.OUT)
GPIO.setup(pin_motor_4_b, GPIO.OUT)
motor_4_f = GPIO.PWM(pin_motor_4_a, 4000)
motor_4_r = GPIO.PWM(pin_motor_4_b, 4000)
motor_4_f.start(0)
motor_4_r.start(0)
motor_d = [motor_4_f, motor_4_r]

max = 60

def stop():

        motor_a[0].ChangeDutyCycle(0)
        motor_a[1].ChangeDutyCycle(0)
        motor_b[1].ChangeDutyCycle(0)
        motor_b[0].ChangeDutyCycle(0)
        motor_c[0].ChangeDutyCycle(0)
        motor_c[1].ChangeDutyCycle(0)
        motor_d[0].ChangeDutyCycle(0)
        motor_d[1].ChangeDutyCycle(0)

def forward():

        stop()
        motor_a[1].ChangeDutyCycle(max)
        motor_b[1].ChangeDutyCycle(max)
        motor_c[0].ChangeDutyCycle(max)
        motor_d[0].ChangeDutyCycle(max)

def reverse():

        stop()
        motor_a[0].ChangeDutyCycle(max)
        motor_b[0].ChangeDutyCycle(max)
        motor_c[1].ChangeDutyCycle(max)
        motor_d[1].ChangeDutyCycle(max)


def right():
        stop()
        motor_a[1].ChangeDutyCycle(max-20)
        motor_b[0].ChangeDutyCycle(max-20)
        motor_c[1].ChangeDutyCycle(max-20)
        motor_d[0].ChangeDutyCycle(max-20)

def left():
        stop()
        motor_a[0].ChangeDutyCycle(max-20)
        motor_b[1].ChangeDutyCycle(max-20)
        motor_c[0].ChangeDutyCycle(max-20)
        motor_d[1].ChangeDutyCycle(max-20)


