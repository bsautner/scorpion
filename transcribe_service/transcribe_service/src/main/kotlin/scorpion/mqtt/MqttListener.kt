package mqtt

import org.eclipse.paho.client.mqttv3.MqttCallback

interface MqttListener : MqttCallback {

    fun onConnected()



}