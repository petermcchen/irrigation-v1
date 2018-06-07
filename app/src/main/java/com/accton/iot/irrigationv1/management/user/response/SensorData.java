package com.accton.iot.irrigationv1.management.user.response;

public class SensorData {
    int sensor_no;
    int gateway_no;
    int is_live;
    int is_digital;
    int is_armed;
    int sensor_type;
    String sensor_id;
    String sensor_desc;
    int battery_status;
    int digital_val;
    float analog_val;
    float analog_val_high;
    float analog_val_low;
    int ASH_status;
    String ASH_time;
    int ASL_status;
    String ASL_time;
    String sensor_model;
    String sensor_verion;
    String register_time;
    String last_batt_notice_time;
    String last_update_time;
    int sensor_sub_type;
    int pwm_pct;
    int pwm_freq;
    float analog_offset;
    int remote_status;
    int lqi;
    String geo_loc;
    String location;
    String floor;
    String loop_no;
    // skip some fields: plan_no, plan_pos_x, plan_pos_y, sensor_status
}
