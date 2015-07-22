package razvan.leucrecords.net.liventav2.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import razvan.leucrecords.net.liventav2.R;



/**
 * Created by Razvan on 17.07.2015.
 */
public class BluetoothFragment extends Fragment {



    private BluetoothAdapter myBA;
    private Switch onOffSwitch;
    private ListView listView;
    private ListView listView2;
    private ArrayAdapter<String> searchAdapter, pairedAdapter;
    private Button searchButton;


    public BluetoothFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(razvan.leucrecords.net.liventav2.R.layout.fragment_bluetooth,container, false);


        myBA = BluetoothAdapter.getDefaultAdapter();
        onOffSwitch = (Switch) rootView.findViewById(R.id.onOff);

        onOffSwitch.setChecked(false);

        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    myBA.enable();
                } else {
                    myBA.disable();
                }
            }
        });


        listView2 = (ListView) rootView.findViewById(R.id.listView2);
        searchAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1);
        listView2.setAdapter(searchAdapter);

        //registerReceiver
        getActivity().registerReceiver(myBluetoothReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        //searchButton
        searchButton = (Button) rootView.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAdapter.clear();
                myBA.startDiscovery();

            }
        });















        return rootView;

    }




    private final BroadcastReceiver myBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                searchAdapter.add(device.getName());
                searchAdapter.notifyDataSetChanged();
            }

        }
    };







    @Override
    public void onDetach() {
        super.onDetach();
    }








}
