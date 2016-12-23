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
import android.os.SystemClock;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity {
    private static final String TAG = "Ultrasonic Distance";

    public static final String TRIG_PIN = "BCM17"; //physical pin #11
    public static final String ECHO_PIN = "BCM18"; //physical pin #12

    private Gpio mTrigGpio;
    private Gpio mEchoGpio;

    private long mEchoTime = 0l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate------ ultrasonic distance");

        PeripheralManagerService service = new PeripheralManagerService();
        try {


            // Create GPIO connection.

            // set TRIGGER pin as output.
            mTrigGpio = service.openGpio(TRIG_PIN);
            // Configure as an output.
            mTrigGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            // set ECHO pin as input.
            mEchoGpio = service.openGpio(ECHO_PIN);
            // Configure as an input.
            mEchoGpio.setDirection(Gpio.DIRECTION_IN);
            // set Active High to consider High voltage as Active
            mEchoGpio.setActiveType(Gpio.ACTIVE_HIGH);
            // Enable edge trigger events for both falling and rising edges. This will make it a toggle button.
            mEchoGpio.setEdgeTriggerType(Gpio.EDGE_FALLING);
            // Register an event callback.
            mEchoGpio.registerGpioCallback(mEchoCallback);

            trigger();

        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        // Close the resource
        if (mEchoGpio != null) {
            mEchoGpio.unregisterGpioCallback(mEchoCallback);
            try {
                mEchoGpio.close();
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    }

    // Register an event callback.
    private GpioCallback mEchoCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            mEchoTime = SystemClock.elapsedRealtimeNanos() - mEchoTime;
            if (mEchoGpio == null) {
                return true;
            }

            try {
                Log.d(TAG, "echo time duration ---> " + mEchoTime);

                /**
                 * Distance = Speed * Time
                 * 2D = 340m/s * x/1000000000s
                 * D = 170 * x/1000000000  m
                 * D = 170*100 * x/1000000000  cm
                 * D = 17 * x/1000000  cm
                 */
                // now calculate distance
                double distance = (mEchoTime * 17) / 1000000; // for distance in cm
                Log.d(TAG, "Distance = " + distance);

                // reset variable for next calculation
                mEchoTime = 0l;
                Log.d(TAG, "wait 3 second");
                // wait for 3 second
                TimeUnit.SECONDS.sleep(3);

                trigger();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Return true to keep callback active.
            return true;
        }
    };

    private void trigger() {
        // run the distance calculator for 50 iterations
        // initiate TRIGGER
        try {
            Log.d(TAG, "initiating Tigger");
            // allow module to settle
            TimeUnit.MILLISECONDS.sleep(500);

            Log.d(TAG, "initiating 10us pulse tigger");
            // send 10us pulse trigger
            mTrigGpio.setValue(true);
            TimeUnit.MICROSECONDS.sleep(10);
            mTrigGpio.setValue(false);

            mEchoTime = SystemClock.elapsedRealtimeNanos();
            Log.d(TAG, "echo time start ---> " + mEchoTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
