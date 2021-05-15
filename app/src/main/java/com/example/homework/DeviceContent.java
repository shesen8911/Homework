package com.example.homework;

public class DeviceContent {
    String Daddress;
    int Drssi;
    int Dpos;
    String Dcontent;

    public DeviceContent(String daddress, int drssi, String dcontent, int dpos) {
        Daddress = daddress;
        Drssi = drssi;
        Dcontent = dcontent;
        Dpos = dpos;
    }

    public String getDaddress() {
        return Daddress;
    }

    public int getDrssi() {
        return Drssi;
    }

    public String getDcontent() {
        return Dcontent;
    }

    public int getDpos() {
        return Dpos;
    }
}
