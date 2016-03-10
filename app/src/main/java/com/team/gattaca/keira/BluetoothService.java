package com.team.gattaca.keira;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Robert on 04.03.2016.
 */
public class BluetoothService extends Service {

    final static UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    final static String TAG = "MyWorkerThread";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()) {
            case Constants.CONNECT_ACTION:
                MyWorkerThread mConnect = new MyWorkerThread();
                mConnect.start();
                mConnect.prepareHandler();
                MyHandler mConnectHandler = mConnect.mHandler;
                mConnectHandler.obtainMessage(Constants.CONNECT_ACTION_CODE, startId, 0, intent.getExtras()).sendToTarget();
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    class MyWorkerThread extends HandlerThread {

        public MyHandler mHandler;

        public MyWorkerThread() {
            super(TAG);
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
                case Constants.CONNECT_ACTION_CODE:
                    BluetoothAdapter mBluetoothAdapter = ((BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
                    BluetoothDevice mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(((Bundle) msg.obj).getString("address"));
                    BluetoothSocket mBluetoothSocket = null;
                    try {

                        mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
                    } catch (IOException e) {
                        e.printStackTrace();
                        stopSelf(msg.arg1);
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
                        stopSelf(msg.arg1);
                    }

                    MyWorkerThread mConnectedThread = new MyWorkerThread();
                    mConnectedThread.start();
                    mConnectedThread.prepareHandler();
                    MyHandler mConnectedHandler = mConnectedThread.mHandler;
                    mConnectedHandler.obtainMessage(Constants.CONNECTED_ACTION_CODE, mBluetoothSocket).sendToTarget();

                    this.getLooper().getThread().interrupt();
                    break;

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

                    this.getLooper().getThread().interrupt();
            }

        }
    }

}
