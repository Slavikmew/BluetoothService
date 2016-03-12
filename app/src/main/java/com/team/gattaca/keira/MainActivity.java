package com.team.gattaca.keira;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.team.gattaca.keira.db.HealthContract;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements BluetoothService.Callback {

    public BluetoothService mBluetoothService;
    private Boolean mBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, new MonitorFragment()).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        bindService(new Intent(this, BluetoothService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {

        super.onStop();
        if (mBound){
            unbindService(mServiceConnection);
            mBound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.connect_scan_menu:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new ScanDevicesFragment()).commit();
                return true;
            case R.id.monitor_menu:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new MonitorFragment()).commit();
                return true;
            default:
                return true;
        }


    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder)service;
            mBluetoothService = binder.getService();
            mBluetoothService.registerClient( MainActivity.this, new Handler());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            mBound = false;
        }
    };

    public void onBluetoothError() {

        Toast.makeText(this, "Can not connect!", Toast.LENGTH_LONG).show();
    }

    public void onDataReceived(DataAtom data) {

        ContentValues temp = new ContentValues();
        Calendar calendar = Calendar.getInstance();
        String time = calendar.getTime().toString();
        temp.put(HealthContract.BodyTemperatureEntry.COLUMN_DATE, time);
        temp.put(HealthContract.BodyTemperatureEntry.COLUMN_TEMP, data.getTemperatute());

        this.getContentResolver().insert(HealthContract.BodyTemperatureEntry.CONTENT_URI, temp);

        ContentValues pulse = new ContentValues();
        pulse.put(HealthContract.PulseEntry.COLUMN_DATE, time);
        pulse.put(HealthContract.PulseEntry.COLUMN_PULSE, data.getPulse());
        Toast.makeText(this, data.getPulse() + " " + data.getTemperatute(), Toast.LENGTH_LONG).show();
    }

/*
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.connect_scan_menu).setEnabled(false);
        //menu.getItem(R.id.connect_scan_menu).setEnabled(false);
      //  menu.getItem(R.id.monitor_menu).setEnabled(true);
        return true;
    }
    */
}
