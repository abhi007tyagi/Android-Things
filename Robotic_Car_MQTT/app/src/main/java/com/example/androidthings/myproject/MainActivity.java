/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.androidthings.myproject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;

public class MainActivity extends Activity implements MqttCallback{
    private static final String TAG = MainActivity.class.getSimpleName();

//    public static final String MOTOR_A_PIN_1 = "BCM21"; //physical pin #40
//    public static final String MOTOR_A_PIN_2 = "BCM20"; //physical pin #38
    public static final String MOTOR_B_PIN_1 = "BCM22"; //physical pin #18
//    public static final String MOTOR_B_PIN_2 = "BCM23"; //physical pin #16

//    private Gpio motorAPin1;
//    private Gpio motorAPin2;
    private Gpio motorBPin1;
//    private Gpio motorBPin2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate..... Robotic Car MQTT");

        try {
            MemoryPersistence persistance = new MemoryPersistence();
            MqttClient client = new MqttClient("tcp://192.168.1.7:1883", "AndroidThingBroker", persistance);
            client.setCallback(this);
            client.connect();

            String topic = "topic/led";
            client.subscribe(topic);

        } catch (MqttException e) {
            e.printStackTrace();
        }

        PeripheralManagerService service = new PeripheralManagerService();
        try {

//            // Create GPIO connection for L293D (Motor will run through L293D).
//            motorAPin1 = service.openGpio(MOTOR_A_PIN_1);
//            // Configure as an output.
//            motorAPin1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
//
//            // Create GPIO connection for L293D (Motor will run through L293D).
//            motorAPin2 = service.openGpio(MOTOR_A_PIN_2);
//            // Configure as an output.
//            motorAPin2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            // Create GPIO connection for L293D (Motor will run through L293D).
            motorBPin1 = service.openGpio(MOTOR_B_PIN_1);
            // Configure as an output.
            motorBPin1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

//            // Create GPIO connection for L293D (Motor will run through L293D).
//            motorBPin2 = service.openGpio(MOTOR_B_PIN_2);
//            // Configure as an output.
//            motorBPin2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

//        if (motorAPin1 != null) {
//            try {
//                motorAPin1.close();
//            } catch (IOException e) {
//                Log.e(TAG, "Error on PeripheralIO API", e);
//            }
//        }
//
//        if (motorAPin2 != null) {
//            try {
//                motorAPin2.close();
//            } catch (IOException e) {
//                Log.e(TAG, "Error on PeripheralIO API", e);
//            }
//        }

        if (motorBPin1 != null) {
            try {
                motorBPin1.close();
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }

//        if (motorBPin2 != null) {
//            try {
//                motorBPin2.close();
//            } catch (IOException e) {
//                Log.e(TAG, "Error on PeripheralIO API", e);
//            }
//        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG, "connectionLost....");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        Log.d(TAG, payload);
        switch (payload){
            case "FW":
                Log.d(TAG, "Move Forward");
                motorBPin1.setValue(true);
                break;
            case "BW":
                Log.d(TAG, "Move Backward");
                break;
            case "LT":
                Log.d(TAG, "Move Left");
                break;
            case "RT":
                Log.d(TAG, "Move Right");
                break;
            case "ST":
                Log.d(TAG, "Stop Moving");
                motorBPin1.setValue(false);
                break;
            default:
                Log.d(TAG, "Message not supported!");
                break;
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(TAG, "deliveryComplete....");
    }
}
