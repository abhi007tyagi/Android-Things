package com.example.androidthings.myproject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String PIR_PIN = "BCM17"; //physical pin #11
    public static final String LED_PIN = "BCM13"; //physical pin #33

    private Gpio mPirGpio;
    private Gpio mLedGpio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate -------------");

        PeripheralManagerService service = new PeripheralManagerService();
        try {
            // set PIR sensor as button for LED
            // Create GPIO connection.
            mPirGpio = service.openGpio(PIR_PIN);
            // Configure as an input.
            mPirGpio.setDirection(Gpio.DIRECTION_IN);
            // Enable edge trigger events for both falling and rising edges. This will make it a toggle button.
            mPirGpio.setEdgeTriggerType(Gpio.EDGE_BOTH);
            // Register an event callback.
            mPirGpio.registerGpioCallback(mSetLEDCallback);

            // set LED as output
            // Create GPIO connection.
            mLedGpio = service.openGpio(LED_PIN);
            // Configure as an output.
            mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        // Close the resource
        if (mPirGpio != null) {
            mPirGpio.unregisterGpioCallback(mSetLEDCallback);
            try {
                mPirGpio.close();
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
        if (mLedGpio != null) {
            try {
                mLedGpio.close();
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    }

    // Register an event callback.
    private GpioCallback mSetLEDCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            Log.i(TAG, "GPIO callback ------------");
            if (mLedGpio == null) {
                return true;
            }
            try {
                Log.i(TAG, "GPIO callback -->" + gpio.getValue());
                // set the LED state to opposite of input state
                mLedGpio.setValue(gpio.getValue());
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
            // Return true to keep callback active.
            return true;
        }
    };
}
