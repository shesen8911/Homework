package com.example.homework;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FragmentList extends Fragment{
    public static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter;
    private List<DeviceContent> Devices = new ArrayList<>();
    private HashMap<String, DeviceContent> mHashMap = new HashMap<String, DeviceContent>();

    Button scanBtn;
    Adapter adapter;
    RecyclerView DeviceList;
    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothLeScanner mBluetoothLeScanner = null;
    int pos;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DeviceList = (RecyclerView) getActivity().findViewById(R.id.rv);
        pos = 0;
        //????????????????????????????????????????????????
        //?????????0??????????????????add???????????????????????????????????????index=0
        if(Devices.size() > 0) {
            for (int i = 0; i < Devices.size(); i++) {
            Log.i(MainActivity.class.getSimpleName(), "WE HAVE VALUE:"+Devices.get(i).getDaddress());
                DeviceContent temp = Devices.get(0);
                Devices.remove(0);
                adapter.notifyItemRemoved(0);
                Devices.add(temp);
                DeviceList.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapter = new Adapter(getActivity(), Devices);
                DeviceList.setAdapter(adapter);
            }
        }
        //?????????????????????
        int permissionCheck = getActivity().checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += getActivity().checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        if (permissionCheck != 0) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
        }
        //???????????????????????????
        initBluetooth();

        //Set up bluetooth
        //1.Get the BluetoothAdapter
        scanBtn = getActivity().findViewById(R.id.buttonScan);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast toast = Toast.makeText(getActivity(), "?????????????????????", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP| Gravity.LEFT, 0, 0);
            toast.show();
        }

        //2.Enable Bluetooth
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else if (scanBtn.getText().equals("????????????")) {
                    FindDevices();
                } else {
                    StopFindDevices();
//                    DeviceList.removeAllViews();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    //??????????????????????????????????????????
    private void initBluetooth() {
        boolean success = false;
        mBluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager != null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if (mBluetoothAdapter != null) {
                mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
                success = true;
            }
        }
        if (!success) {
            getActivity().finish();
        }
    }

    //????????????
    private void FindDevices() {
        mBluetoothLeScanner.startScan(startScanCallback);
        scanBtn.setText("????????????");
        Toast toast = Toast.makeText(getActivity(), "?????????", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP| Gravity.LEFT, 0, 0);
        toast.show();
    }

    //????????????----????????????!!!!!!
    private final ScanCallback startScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            ScanRecord mScanRecord = result.getScanRecord();
            String address = device.getAddress();
            byte[] content = mScanRecord.getBytes();
            int mRssi = result.getRssi();
            //MAC????????????????????????????????????????????? ???HashMap???
            if (mHashMap.containsKey(device.getAddress())) {
                int temp = 0;
                for (DeviceContent d:Devices) {
                    if (d.getDaddress().equals(device.getAddress())) {
                        temp = d.getDpos();
                        Log.i(MainActivity.class.getSimpleName(), "temp:"+temp);
                    }
                }
                if (mHashMap.get(device.getAddress()).getDrssi() != mRssi) {
                    DeviceContent deviceContent = new DeviceContent(address, mRssi, byteArrayToHexString(content), temp);
                    mHashMap.put(deviceContent.getDaddress(), deviceContent);
                    Devices.set(temp, deviceContent);
                    adapter.notifyItemChanged(temp);
                }
            }
            else {
                DeviceContent deviceContent = new DeviceContent(address, mRssi, byteArrayToHexString(content), pos);
                mHashMap.put(deviceContent.getDaddress(), deviceContent);
                Devices.add(deviceContent);
                DeviceList.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapter = new Adapter(getActivity(), Devices);
                DeviceList.setAdapter(adapter);
                Log.i(MainActivity.class.getSimpleName(), "POS:"+pos);
                pos++;
            }
        }
    };

    //???context???????????????
    public static String byteArrayToHexString(byte[] b){
        int len = b.length;
        String data = new String();
        for(int i = 0;i<len;i++){
            data += Integer.toHexString((b[i]>>4)&0xf);
            data += Integer.toHexString(b[i]&0xf);
        }
        return data;
    }

    //BroadcastReceiver??????context...??????????????????
//    private final BroadcastReceiver receiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
//                DeviceContent deviceContent = new DeviceContent(bluetoothDevice.getAddress(), rssi, pos);
//                if (mHashMap.containsKey(deviceContent.getDaddress())) {
//                    //MAC????????????????????????????????????????????? ???HashMap???
//                    for (DeviceContent d:Devices) {
//                        if (d.getDaddress() == deviceContent.getDaddress()) {
//                            pos = d.getDpos();
//                        }
//                    }
//                    if (mHashMap.get(deviceContent.getDaddress()).getDrssi() != rssi) {
//                        mHashMap.put(deviceContent.getDaddress(), deviceContent);
//                        Devices.set(pos, deviceContent);
//                        adapter.notifyItemChanged(pos);
//                    }
//                } else {
//                    mHashMap.put(deviceContent.getDaddress(), deviceContent);
//                    Devices.add(deviceContent);
//                    DeviceList.setLayoutManager(new LinearLayoutManager(getActivity()));
//                    adapter = new Adapter(getActivity(), Devices);
//                    DeviceList.setAdapter(adapter);
//                    pos++;
//                }
//            }
//        }
//    };

    //??????????????????????????????????????????OK?????????:
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) FindDevices();
    }

    //????????????
    public void StopFindDevices() {
        mBluetoothLeScanner.stopScan(startScanCallback);
        scanBtn.setText("????????????");
        Toast toast = Toast.makeText(getActivity(), "????????????", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 100, 100);
        toast.show();
    }

    //???????????????????????????????????????
    @Override
    public void onStop() {
        super.onStop();
        //?????????????????????????????????????????????
        if (scanBtn.getText().equals("????????????")){
            StopFindDevices();
        }
    }
}