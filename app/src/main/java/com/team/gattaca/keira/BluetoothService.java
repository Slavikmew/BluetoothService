package com.team.gattaca.keira;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
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


    private final static UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private final IBinder mBinder = new LocalBinder();
    private Callback mCallback;
    private Handler mResponseHandler;

    public interface Callback {
        public void onDataReceived(DataAtom data);

        public void onBluetoothError();
    }

    public class LocalBinder extends Binder {

        public BluetoothService getService() {

            return BluetoothService.this;
        }
    }

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

        return mBinder;
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
                mResponseHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onBluetoothError();
                    }
                });
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

                        byte[] buffer = new byte[1024];
                        int bytes;
                        while (true) {
                            try {
                                bytes = inputStream.read(buffer);
                                int i = 0;
                                if (bytes > 0) {
                                    final DataAtom result = new DataAtom(buffer);
                                    mResponseHandler.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            mCallback.onDataReceived(result);
                                        }
                                    });
                                }
                                break;
                            } catch (IOException e) {
                                break;
                            }
                        }




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

    public void registerClient(Callback activity, Handler UIHandler) {

        mCallback = activity;
        mResponseHandler = UIHandler;
    }

    private void initWorkerThread(BluetoothSocket bluetoothSocket) {

        MyWorkerThread myWorkerThread = new MyWorkerThread();
        myWorkerThread.start();
        myWorkerThread.prepareHandler();
        MyHandler myHandler = myWorkerThread.mHandler;
        myHandler.obtainMessage(Constants.CONNECTED_ACTION_CODE, bluetoothSocket).sendToTarget();

    }

}