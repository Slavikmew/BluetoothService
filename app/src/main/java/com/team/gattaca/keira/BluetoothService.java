package com.team.gattaca.keira;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.UUID;

/**
 * Created by Robert on 04.03.2016.
 */
public class BluetoothService extends Service {

    final static UUID MY_UUID = UUID.fromString("ziga_zaga_hoy_hoy_hoy");
    private Handler mWorkerHandler;
    private Handler mResponseHandler;
    private BluetoothAdapter mBlueToothAdapter;

    /*class MyWorkerThread extends HandlerThread{
        public MyWorkerThread(String name, Handler.Callback callback) {
            super(name);
        }
    }
    */

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
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
}
