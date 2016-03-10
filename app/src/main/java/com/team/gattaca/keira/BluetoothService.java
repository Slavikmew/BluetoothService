package com.team.gattaca.keira;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Robert on 04.03.2016.
 */
public class BluetoothService extends Service {

    final static UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()) {
            case Constants.CONNECT_ACTION:
                MyConnectThread connectThread = new MyConnectThread(intent.getExtras().getString("address"));
                connectThread.start();
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    class MyConnectThread extends Thread {

        private String mDeviceAddress;
        BluetoothAdapter mBluetoothAdapter;
        BluetoothSocket mBluetoothSocket;
        BluetoothDevice mBluetoothDevice;

        public MyConnectThread(String deviceAddress) {

            setName("MyConnectThread");

            mBluetoothAdapter = ((BluetoothManager) getApplicationContext().
                    getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

            mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress);

        }
        @Override
        public void run() {
            try {
                mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            try {
                mBluetoothSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    mBluetoothSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return;
            }

            initWorkerThread(mBluetoothSocket);
        }
    }


    class MyWorkerThread extends HandlerThread {

        public MyHandler mHandler;

        public MyWorkerThread() {
            super("MyWorkerThread");
        }

        public void prepareHandler() {
            mHandler = new MyHandler(getLooper());
        }
    }

    class MyHandler extends Handler {

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);

            switch (msg.what) {

                case Constants.CONNECTED_ACTION_CODE:
                    BluetoothSocket connectedSocket = ((BluetoothSocket) msg.obj);

                    try (InputStream inputStream = ((BluetoothSocket) msg.obj).getInputStream();
                         OutputStream outputStream = ((BluetoothSocket) msg.obj).getOutputStream()
                    ) {

                        /*Data transfer*/

                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            connectedSocket.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                    }

            }

        }
    }

    private void initWorkerThread(BluetoothSocket bluetoothSocket) {

        MyWorkerThread myWorkerThread = new MyWorkerThread();
        myWorkerThread.start();
        myWorkerThread.prepareHandler();
        MyHandler myHandler = myWorkerThread.mHandler;
        myHandler.obtainMessage(Constants.CONNECTED_ACTION_CODE, bluetoothSocket).sendToTarget();

    }

}
