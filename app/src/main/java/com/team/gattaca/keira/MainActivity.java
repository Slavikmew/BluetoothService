package com.team.gattaca.keira;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

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
