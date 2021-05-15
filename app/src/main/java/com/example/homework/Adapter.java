package com.example.homework;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Set;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

    private List<DeviceContent> Devices;
    private LayoutInflater      layoutInflater;

    public Adapter(Context context, List<DeviceContent> list) {
        this.Devices        = list;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mDeviceName;
        public final ImageView mDeviceBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mDeviceName = (TextView) itemView.findViewById(R.id.textView2);
            mDeviceBtn  = (ImageView)   itemView.findViewById(R.id.buttonDetail);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater LI = LayoutInflater.from(parent.getContext());
        View view         = LI.inflate(R.layout.fragment_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int index) {
        String Daddress = Devices.get(index).getDaddress();
        int    Drssi    = Devices.get(index).getDrssi();
        String Dcontent = Devices.get(index).getDcontent();
        viewHolder.mDeviceName.setText(Daddress + '\n' + Drssi);
        viewHolder.mDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListDirections.ActionFragmentListToDetailFragment action = FragmentListDirections.actionFragmentListToDetailFragment(Drssi, Daddress, Dcontent);
                action.setRssi(Drssi);
                action.setMac(Daddress);
                action.setContent(Dcontent);
                Navigation.findNavController(v).navigate(action);
            }
        });
    }

    @Override
    public int getItemCount() {return Devices.size();}
}
