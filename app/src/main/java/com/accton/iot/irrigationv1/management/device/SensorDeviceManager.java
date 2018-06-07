package com.accton.iot.irrigationv1.management.device;

import android.util.Log;
import com.accton.iot.irrigationv1.management.device.SensorDevice;
import java.util.ArrayList;

public class SensorDeviceManager {
    private final static String TAG = "SensorDeviceManager";
    private final static boolean DEBUG = false;

    private static ArrayList<SensorDevice> mDeviceList = new ArrayList<SensorDevice>();

    public SensorDeviceManager()
    {
        //EventBus.getDefault().register(this);
    }

    public int getDeviceNumber()
    {
        return mDeviceList.size();
    }

    public boolean hasDevices()
    {
        return (mDeviceList.size()>0);
    }


}
