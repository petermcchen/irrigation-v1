package com.accton.iot.irrigationv1.fragment;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accton.iot.irrigationv1.MainApplication;
import com.accton.iot.irrigationv1.R;

public class AboutFragment extends Fragment
{
    private final static String TAG = "AboutFragment";
    private final static boolean DEBUG = true;

    private Activity mActivity;

    private TextView mWebsiteView;
    private TextView mPrivacyView;
    private TextView mVersionView;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if(DEBUG)
            Log.d(TAG, "onCreateView");

        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        if(DEBUG)
            Log.d(TAG, "onActivityCreated");

        super.onActivityCreated(savedInstanceState);

        setupLayout();
    }

    @Override
    public void onResume()
    {
        if(DEBUG)
            Log.d(TAG, "onResume");

        super.onResume();

        resetLayout();
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
    }

    private void setupLayout()
    {
        if(DEBUG)
            Log.d(TAG, "setupLayout");

        mWebsiteView = (TextView) mActivity.findViewById(R.id.website);
        mPrivacyView = (TextView) mActivity.findViewById(R.id.privacy);
        mVersionView = (TextView) mActivity.findViewById(R.id.version);
    }

    private void resetLayout()
    {
        if(DEBUG)
            Log.d(TAG, "resetLayout");

        mWebsiteView.setText(Html.fromHtml("<a href=\"" + getResources().getString(R.string.about_website) + "\">" + getResources().getString(R.string.about_website) + "</a>"));
        mWebsiteView.setMovementMethod(LinkMovementMethod.getInstance());

        mPrivacyView.setText(Html.fromHtml("<a href=\"" + getResources().getString(R.string.about_privacy_website) + "\">" + getResources().getString(R.string.about_privacy) + "</a>"));
        mPrivacyView.setMovementMethod(LinkMovementMethod.getInstance());

        mVersionView.setText(getResources().getString(R.string.about_version) + " " + getVersion());
    }

    //
    //
    // Version Info
    //
    //

    private String getVersion()
    {
        String version = null;

        try
        {
            PackageInfo packageInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);

            String name = packageInfo.versionName;

            version = name;
        }
        catch(NameNotFoundException e)
        {
            e.printStackTrace();
        }

        return version;
    }
}