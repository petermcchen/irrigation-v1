package com.accton.iot.irrigationv1.fragment;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.accton.iot.irrigationv1.MainApplication;
import com.accton.iot.irrigationv1.R;
import com.accton.iot.irrigationv1.adapter.DeviceListAdapter;
//import com.accton.iot.irrigationv1.event.DeviceNumberChangedEvent;
//import com.accton.iot.irrigationv1.event.user.UserQueryDeviceIdDoneEvent;
//import com.accton.iot.irrigationv1.event.user.UserQueryDoneEvent;
import com.accton.iot.irrigationv1.management.device.SensorDevice;
//import com.accton.iot.irrigationv1.management.user.DeviceUserInfo;
import com.accton.iot.irrigationv1.management.user.UserManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DeviceListFragment extends ListFragment implements DeviceListAdapter.OnMenuListener, PopupMenu.OnMenuItemClickListener
{
    private static final String TAG = "DeviceListFragment";
    private static final boolean DEBUG = true;

    public interface OnSelectionListener
    {
        //void onAddDevice();
        //void onRefreshDevice();
        void onShowDevice(int index, boolean fullScreen);
        //void onDeleteDevice(int index);
        //void onRenameDevice(int index);
    }

    private static final int MENU_RENAME = 2;
    private static final int MENU_DELETE = 3;
    //private static final int MENU_ACCESS_CODE = 4;
    //private static final int MENU_CHANGE_ACCESS_CODE = 5;
    private static final int MENU_WIFI = 6;
    private static final int MENU_WIFI_MODE_CLIENT = 7;
    private static final int MENU_WIFI_MODE_AP = 8;
    //private static final int MENU_USER_LIST = 9; // Add for Device's member list

    private OnSelectionListener mListener;

    private Activity mActivity;

    private DeviceListAdapter mAdapter = null;
    private ListView mListView = null;

    private ArrayList<SensorDevice> mDeviceList = new ArrayList<SensorDevice>();

    private int mSelectedDevice = -1;
    private Toolbar mToolbar;

    // To query member count.
    private UserManager mUserManager = null; // valid when not null
    private int mSavedSelectedDevice = -1; // valid when != -1 & is used to filter out otto message
    private Handler mDeleteDeviceHandler = new Handler(); // For getting device context

    @Override
    public void onAttach(Activity activity)
    {
        if(DEBUG)
            Log.d(TAG, "onAttach activity:" + activity);

        super.onAttach(activity);

        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        if(DEBUG)
            Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        if(DEBUG)
            Log.d(TAG, "onActivityCreated");

        super.onActivityCreated(savedInstanceState);

        setupLayout();

        // To query member count.
        mUserManager = MainApplication.getUserManager();
    }

    @Override
    public void onStart()
    {
        if(DEBUG)
            Log.d(TAG, "onStart");

        super.onStart();

        //EventBus.getDefault().register(this);
    }

    @Override
    public void onStop()
    {
        if(DEBUG)
            Log.d(TAG, "onStop");

        //EventBus.getDefault().unregister(this);

        super.onStop();
    }

    @Override
    public void onResume()
    {
        if(DEBUG)
            Log.d(TAG, "onResume");

        super.onResume();

        resetLayout();

        loadDeviceList();
    }

    @Override
    public void onPause()
    {
        if(DEBUG)
            Log.d(TAG, "onPause");

        super.onPause();
    }

    @Override
    public void onDestroyView()
    {
        if(DEBUG)
            Log.d(TAG, "onDestroyView");

        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        if(DEBUG)
            Log.d(TAG, "onDestroy");

        super.onDestroy();

        // TODO... leakWatch...
        //MainApplication.leakWatch(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        if(DEBUG)
            Log.d(TAG, "onCreateOptionsMenu m:" + menu + " i:" + inflater);

        if(menu!=null)
            menu.clear();

        inflater.inflate(R.menu.device_list, menu);

        MenuItem itemNew = menu.findItem(R.id.action_device_new);
        MenuItem itemRefresh = menu.findItem(R.id.action_device_refresh);

        MenuItemCompat.setShowAsAction(itemNew, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setShowAsAction(itemRefresh, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(DEBUG)
            Log.d(TAG, "onOptionsItemSelected i:" + item);

        int id = item.getItemId();

        if(id == R.id.action_device_new)
        {
            //if(mListener!=null)
            //    mListener.onAddDevice();
        }
        else if(id == R.id.action_device_refresh)
        {
            //if(mListener!=null)
            //    mListener.onRefreshDevice();
        }

        return true;
    }

    public void setToolbar(Toolbar actionBar)
    {
        mToolbar = actionBar;
    }

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        if(DEBUG)
            Log.d(TAG, "onHiddenChanged h:" + hidden);

        if(!hidden)
        {
            resetLayout();
        }
        else {
        }
    }

    @Override
    public void onConfigurationChanged(Configuration config)
    {
        if(DEBUG)
            Log.d(TAG, "onConfigurationChanged c:" + config);

        super.onConfigurationChanged(config);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id)
    {
        if(DEBUG)
            Log.d(TAG, "onListItemClick p:" + position + " i:" + id);

        super.onListItemClick(listView, view, position, id);

        //fix popup alert delete dialog with wrong serial number
        mSavedSelectedDevice = -1;
        mSelectedDevice = -1;

        if(mListener!=null) {
            mListener.onShowDevice(position, false);
        }
    }

    @Override
    public void onShowMenu(int index, View view)
    {
        if(DEBUG)
            Log.d(TAG, "onShowMenu i:" + index + " v:" + view);

        mSavedSelectedDevice = mSelectedDevice = index;

        PopupMenu popupMenu = new PopupMenu(mActivity, view);
        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            Object menuPopupHelper = field.get(popupMenu);
            Method setForceIcons = menuPopupHelper.getClass().getDeclaredMethod("setForceShowIcon", Boolean.TYPE);
            setForceIcons.invoke(menuPopupHelper, true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        popupMenu.setOnMenuItemClickListener(this);

        SensorDevice device = mDeviceList.get(index);

        popupMenu.getMenu().add(Menu.NONE, MENU_RENAME, Menu.NONE, R.string.menu_rename).setIcon(R.drawable.menu_rename);

        popupMenu.getMenu().add(Menu.NONE, MENU_DELETE, Menu.NONE, R.string.menu_remove).setIcon(R.drawable.menu_delete);

        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        if(DEBUG)
            Log.d(TAG, "onMenuItemClick i:" + item + " s:" + mSelectedDevice);

        int id = item.getItemId();

        if(id == MENU_RENAME)
        {
            //if(mListener!=null)
            //    mListener.onRenameDevice(mSelectedDevice);
        }
        else if(id == MENU_DELETE)
        {
            //if(mListener!=null) {
            //    mListener.onDeleteDevice(mSelectedDevice);
            //}
        }

        mSelectedDevice = -1;

        return true;
    }

    //
    //
    // Listener
    //
    //

    public void setOnSelectionListener(OnSelectionListener listener)
    {
        if(DEBUG)
            Log.d(TAG, "setOnSelectionListener");

        mListener = listener;
    }

    //
    //
    // Layout Setting
    //
    //

    private void setupLayout()
    {
        mListView = getListView();
        mListView.setDivider(null);
        mListView.setDividerHeight(0);
        mAdapter = new DeviceListAdapter(getActivity(), R.layout.fragment_device_list_item, mDeviceList);

        setListAdapter(mAdapter);

        mAdapter.setOnMenuListener(this);

        mListView.setFastScrollEnabled(false);
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setSmoothScrollbarEnabled(true);

//		mListView.setCacheColorHint(android.R.color.transparent);
        mListView.setBackgroundResource(R.drawable.background_gradlient);

        //setListShownNoAnimation(false);
    }

    private void resetLayout()
    {
        if(DEBUG)
            Log.d(TAG, "resetLayout");

        if(mToolbar!=null)
            mToolbar.setTitle(R.string.title_device);

        updateAllItem();
    }

    //
    //
    // Device Event
    //
    //

    // TODO...
    //@Subscribe(threadMode = ThreadMode.MAIN)
    //public void onDeviceNumberChanged(DeviceNumberChangedEvent event)
    //{
    //    loadDeviceList();
    //}


    //
    //
    // Device List Data
    //
    //

    // TODO...
    private void loadDeviceList()
    {
        if(DEBUG)
            Log.d(TAG, "loadDeviceList n:" + MainApplication.getSensorDeviceNumber());

        mDeviceList.clear();

        for(int i=0; i<MainApplication.getSensorDeviceNumber(); i++)
        {
            SensorDevice device = MainApplication.getSensorDevice(i);
            mDeviceList.add(device);
        }

        if(mAdapter!=null)
            mAdapter.notifyDataSetChanged();
    }

    public void updateAllItem()
    {
        if(DEBUG)
            Log.d(TAG, "updateAllItem");

        for(int i=mListView.getFirstVisiblePosition(); i<=mListView.getLastVisiblePosition(); i++)
        {
            updateItemAtPosition(i);
        }
    }

    private void updateItemAtPosition(int position)
    {
        if(DEBUG)
            Log.d(TAG, "updateItemAtPosition p:" + position);

        int visiblePosition = mListView.getFirstVisiblePosition();

        View view = mListView.getChildAt(position - visiblePosition);

        mAdapter.getView(position, view, mListView);
    }

    public void notifyDataSetChanged()
    {
        if(DEBUG)
            Log.d(TAG, "notifyDataSetChanged");

        setListShownNoAnimation(true);

        if(mAdapter!=null)
            mAdapter.notifyDataSetChanged();

        updateAllItem();
    }
}