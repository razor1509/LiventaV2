package razvan.leucrecords.net.liventav2.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    private BluetoothDevice device;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothSocket bluetoothSocket;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

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
//Turn BT ON
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

//Show Found Devices
        listView2 = (ListView) rootView.findViewById(R.id.listView2);
        searchAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.list_items);
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

//pair devices







//show paired devices

        pairedAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.list_items);

        listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setAdapter(pairedAdapter);

        Set<BluetoothDevice> pairedDevices = myBA.getBondedDevices();

        // Add previosuly paired devices to the array
        if (pairedDevices.size() > 0) {
          // rootView.findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);//make title viewable
            for (BluetoothDevice device : pairedDevices) {
                pairedAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
//            String noDevices = getResources().getText(R.string.none_paired).toString();
  //          mPairedDevicesArrayAdapter.add(noDevices);

            Toast.makeText(getActivity(), "No devices paired", Toast.LENGTH_LONG).show();
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Get the device MAC address, which is the last 17 chars in the View
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);


                //connect the device when item is click
                BluetoothDevice connect_device = myBA.getRemoteDevice(address);

                try {
                    bluetoothSocket = connect_device.createRfcommSocketToServiceRecord(MY_UUID);
                    bluetoothSocket.connect();

                    if (bluetoothSocket.isConnected()){

                        Toast.makeText(getActivity(), "Connected", Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
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


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                pairedAdapter.add(device.getName());

            }
        }
    };





    @Override
    public void onDetach() {
        super.onDetach();
    }



}
