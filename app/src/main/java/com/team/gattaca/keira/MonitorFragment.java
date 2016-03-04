package com.team.gattaca.keira;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Robert on 03.03.2016.
 */
public class MonitorFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        ;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String[] data = {
                "Pulse",
                "Blood Pressure",
                "Body Temperature",
                "Respiratory Rate",
                "Glucose Level"
        };
        List<String> sampleMesurements = new ArrayList<String>(Arrays.asList(data));
        ListView root = (ListView)inflater.inflate(R.layout.monitor_fragment,container,false);
        root.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.list_item_measurement, sampleMesurements));
        return root;

    }
}

