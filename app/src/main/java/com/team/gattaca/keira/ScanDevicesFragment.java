package com.team.gattaca.keira;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Robert on 06.03.2016.
 */
public class ScanDevicesFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String[] data = {
                "Angel Sensor",
                "Fitbit",
                "Garmin"
        };

        setHasOptionsMenu(true);

        List<String> list = new ArrayList<String>(Arrays.asList(data));
        View v = inflater.inflate(R.layout.scan_fragment, container, false);
        ListView listView = (ListView) v.findViewById(R.id.scan_list_view);
        listView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.list_item_device, list));
        return v;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.connect_scan_menu).setVisible(false);
        menu.findItem(R.id.connect_scan_menu).setEnabled(false);
    }

}
