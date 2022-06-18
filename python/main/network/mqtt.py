import paho.mqtt.client as mqtt_client

import json
import random

broker = "10.0.0.205"
port = 1883
client_id = f'python-mqtt-{random.randint(0, 1000)}'
username = 'ben'
password = 'imarobot'
client = mqtt_client.Client(client_id)

def publish(topic, payload):
    try:
        v = json.dumps(payload)

        result = client.publish(topic, v)
        # result: [0, 1]
        status = result[0]
        if status == 1:
            print(f"Failed to send message to topic {topic}")
    except (ConnectionResetError, ConnectionRefusedError) as err:
            print(err)

def connect(on_connect, on_message, on_disconnect):

    client.username_pw_set(username, password)
    client.on_connect = on_connect
    client.connect(broker, port, 30)
    client.on_message = on_message
    client.on_disconnect = on_disconnect
    client.loop_start()
