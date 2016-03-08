package com.team.gattaca.keira;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import java.util.UUID;

/**
 * Created by Robert on 04.03.2016.
 */
public class BluetoothService extends Service {

    final static UUID MY_UUID = UUID.fromString("ziga_zaga_hoy_hoy_hoy");
    final static String TAG = "MyWorkerThread";

    private MyHandler mRequestHandler;
    private Handler mResponseHandler;
    private MyWorkerThread mWorkerThread;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    public void onCreate() {

        super.onCreate();
        mWorkerThread = new MyWorkerThread();
        mRequestHandler = mWorkerThread.getHandler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()) {
            case Constants.SCAN_ACTION:
                mRequestHandler.obtainMessage(Constants.SCAN_ACTION_CODE, startId, 0).sendToTarget();
                break;
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

        private MyHandler mHandler;

        public MyWorkerThread() {
            super(TAG);
        }

        public void prepareHandler() {
            mHandler = new MyHandler(getLooper());

        }

        public MyHandler getHandler() {
            return mHandler;
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
                case Constants.SCAN_ACTION_CODE:

                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            }


            /*Обработка сообщения*/
        }
    }

}
