package com.team.gattaca.keira;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by Robert on 06.03.2016.
 */
public class ScanDevicesFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static BluetoothAdapter mBluetoothAdapter;
    private static final String TAG = "SCAN_FRAGMENT";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_COARSE_LOCATION_PERMISSIONS = 2;
    private static ArrayAdapter<String> mArrayAdapter;
    private static ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getContext().registerReceiver(mReceiver, filter);

        mBluetoothAdapter = ((BluetoothManager) getContext().getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(getContext(), getString(R.string.bluetooth_not_supported), Toast.LENGTH_LONG).show();
            //протестировать методы остановки фрагмента
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.scan_fragment, container, false);
        listView = (ListView) v.findViewById(R.id.scan_list_view);
        mArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_device);
        listView.setAdapter(mArrayAdapter);
        listView.setOnItemClickListener(this);

        if (mBluetoothAdapter.isEnabled()) {

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            for (BluetoothDevice device : pairedDevices) {
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            getActivity().startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        v.findViewById(R.id.scan_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                doDiscovery();
                /*
                Intent connectIntent = new Intent(getContext(), BluetoothService.class);
                connectIntent.setAction(Constants.CONNECT_ACTION);
                getContext().startService(connectIntent);*/
            }
        });



               /*
                Intent scanIntent = new Intent(getActivity(), BluetoothService.class);
                scanIntent.setAction(Constants.SCAN_ACTION);
                getContext().startService(scanIntent);
              }*/
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        mBluetoothAdapter.cancelDiscovery();
        String item = (String)parent.getItemAtPosition(position);
        Intent connectIntent = new Intent(getContext(), BluetoothService.class);
        connectIntent.setAction(Constants.CONNECT_ACTION);
        connectIntent.putExtra("device_name", item.split("\n+")[0]);
        connectIntent.putExtra("address", item.split("\n+")[1]);
        getContext().startService(connectIntent);

    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.connect_scan_menu).setVisible(false);
        menu.findItem(R.id.connect_scan_menu).setEnabled(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }

        getContext().unregisterReceiver(mReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) mBluetoothAdapter.startDiscovery();
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    };

    public void doDiscovery() {
        int hasPermission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
            mBluetoothAdapter.startDiscovery();
            return;
        }
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_COARSE_LOCATION_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION_PERMISSIONS: {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doDiscovery();
                }
                return;
            }
        }
    }

}
