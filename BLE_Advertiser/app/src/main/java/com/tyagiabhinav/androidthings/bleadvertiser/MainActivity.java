package com.tyagiabhinav.androidthings.bleadvertiser;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.UUID;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private BluetoothManager mBluetoothManager;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        // get Bluetooth Service Manager
        mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        startAdvertising();

    }

    @Override
    protected void onDestroy() {
        stopAdvertising();
        super.onDestroy();
        Log.w(TAG, "onDestroy");
    }

    /**
     * Verify the level of Bluetooth support provided by the hardware.
     *
     * @param bluetoothAdapter System {@link BluetoothAdapter}.
     * @return true if Bluetooth is properly supported, false otherwise.
     */
    private boolean checkBluetoothSupport(BluetoothAdapter bluetoothAdapter) {

        if (bluetoothAdapter == null) {
            Log.w(TAG, "Bluetooth is not supported");
            return false;
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.w(TAG, "Bluetooth LE is not supported");
            return false;
        }

        return true;
    }

    /**
     * Start advertising the data
     *
     */
    public void startAdvertising() {
        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();

        // We can't continue without proper Bluetooth support
        if (!checkBluetoothSupport(bluetoothAdapter)) {
            return;
        }

        mBluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (mBluetoothLeAdvertiser == null) {
            Log.w(TAG, "Failed to create advertiser");
            return;
        }

        // Set advertising setting
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED) // balanced between advertising power and latency
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setTimeout(0) // disable advertising time limit
                .setConnectable(false) // not connectable - only advertising info
                .build();

        ParcelUuid pUuid = new ParcelUuid(UUID.fromString(getString(R.string.uuid)));

        // Generate data to advertise
        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(false) // set as false as the total payload size can not exceed 31 bytes
                .setIncludeTxPowerLevel(false)
                .addServiceData(pUuid, "HELLO BLE".getBytes())
                .build();

        // Starts advertising.
        mBluetoothLeAdvertiser.startAdvertising(settings, data, mAdvertiseCallback);
    }


    /**
     * Stop advertising the data
     *
     */
    public void stopAdvertising() {
        if (mBluetoothLeAdvertiser == null) {
            Log.w(TAG, "advertiser is null");
            return;
        }

        // Stop advertising.
        mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
    }


    /**
     * Advertising callback
     */
    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.i(TAG, "LE Advertise Started.");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.w(TAG, "LE Advertise Failed: " + errorCode);
        }
    };
}
