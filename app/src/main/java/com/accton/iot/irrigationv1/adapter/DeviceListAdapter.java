package com.accton.iot.irrigationv1.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.accton.iot.irrigationv1.MainApplication;
import com.accton.iot.irrigationv1.R;
import com.accton.iot.irrigationv1.management.device.SensorDevice;

public class DeviceListAdapter extends ArrayAdapter<SensorDevice>
{
    private final String TAG = "DeviceListAdapter";
    private final boolean DEBUG = true;

    public interface OnMenuListener
    {
        void onShowMenu(int index, View view);
    }

    private OnMenuListener mListener;

    private final int mResourceId;
    private final LayoutInflater mLayoutInflator;

    private final Context mContext;
    private List<SensorDevice> mContents;
    private int mCount;

    public DeviceListAdapter(Context context, int textViewResourceId, List<SensorDevice> objects)
    {
        super(context, textViewResourceId, objects);

        mContext = context;
        mResourceId = textViewResourceId;
        mContents = objects;

        if(DEBUG)
            Log.d(TAG, "DeviceListAdapter c:" + context + " r:" + textViewResourceId);

        mLayoutInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnMenuListener(OnMenuListener listener)
    {
        if(DEBUG)
            Log.d(TAG, "setOnMenuListener");

        mListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(DEBUG)
            Log.d(TAG, "getView p:" + position + " v:" + convertView + " p:" + parent + " r:" + mResourceId);

        ViewHolder holder = null;

        if(convertView == null) {
            convertView = mLayoutInflator.inflate(mResourceId, null);
            holder = new ViewHolder();
            holder.sensorId = (TextView) convertView.findViewById(R.id.sensor_id);
            holder.gatewayId = (TextView) convertView.findViewById(R.id.gateway_id);
            holder.buttonMore = (ImageView) convertView.findViewById(R.id.button_more_vert);
            holder.sensorType = (ImageView) convertView.findViewById(R.id.sensor_type);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag ();
        }

        if(position<0 || position>=getCount())
            return convertView;

        SensorDevice device = getItem(position);

        if(device==null)
            return convertView;

        String sid = device.getSensorId();
        String gid = device.getGatewayId();

        holder.sensorId.setText(sid);
        holder.gatewayId.setText(gid);

        holder.buttonMore.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mListener!=null)
                    mListener.onShowMenu(position, view);
            }
        });

        holder.sensorType.setImageResource(R.drawable.icon_irrigation_small);

        return convertView;
    }

    private class ViewHolder
    {
        public TextView sensorId;
        public TextView gatewayId;
        public ImageView buttonMore;
        public ImageView sensorType;
    }
}