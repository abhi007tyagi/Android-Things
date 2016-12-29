Android Things - MQTT LED
==========================

This project demonstrate glowing of LED through MQTT.
Android Things device is connected to MQTT Broker (Macbook in this case running Mosquitto).
The device has subscribed to "topic/led". Through the terminal on Macbook, using mosquitto publish command,
ON/OFF commands are send to the device. Based on the received command from publisher, LED either glows or turn OFF.
