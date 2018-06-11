package com.accton.iot.irrigationv1.management.device;

public class SensorDevice {
    private final static String TAG = "SensorDevice";
    private final static boolean DEBUG = true;

    private int sensor_no;
    private int gateway_no;
    private int is_live;
    private int sensor_type;
    private String sensor_id;
    private String sensor_desc;
    private String sensor_model;

    public SensorDevice() {
    }

    public String getSensorId() {
        return sensor_id;
    }

    public void setSensorId(String sid) {
        sensor_id = sid;
    }

    public String getSensorNo() {
        return String.valueOf(sensor_no);
    }

    public void setSensorNo(int sno) {
        sensor_no = sno;
    }

    public int getSensorType() {
        return sensor_type;
    }

    public void setSensorType(int type) {
        sensor_type = type;
    }

    public String getGatewayId() {
        return String.valueOf(gateway_no);
    }

    public void setGatewayId(int gno) {
        gateway_no = gno;
    }

    public String getSensorDesc() {
        return sensor_desc;
    }

    public void setSensorDesc(String desc) {
        sensor_desc = desc;
    }

    public String getSensorModel() {
        return sensor_model;
    }

    public void setSensorModel(String model) {
        sensor_model = model;
    }
}
