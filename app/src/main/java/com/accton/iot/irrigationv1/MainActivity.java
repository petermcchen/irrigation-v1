package com.accton.iot.irrigationv1;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.accton.iot.irrigationv1.fragment.AboutFragment;
import com.accton.iot.irrigationv1.fragment.DeviceListFragment;
import com.accton.iot.irrigationv1.fragment.UserSignInFragment;
import com.accton.iot.irrigationv1.management.user.UserManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, UserSignInFragment.OnSignInListener, DeviceListFragment.OnSelectionListener {
    private final static String TAG = "MainActivity";
    private final static boolean DEBUG = true;
    // Navigation Drawer
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    // Fragments
    private FragmentManager mFragmentManager;
    private AboutFragment mAboutFragment;
    private UserSignInFragment mUserSignInFragment;
    private DeviceListFragment mDeviceListFragment;

    private void setupLayout() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        // Create Fragment  instances
        mAboutFragment = new AboutFragment();

        mUserSignInFragment = new UserSignInFragment();
        mUserSignInFragment.setToolbar(mToolbar);
        mUserSignInFragment.setOnSignInListener(this);

        mDeviceListFragment = new DeviceListFragment();
        mDeviceListFragment.setToolbar(mToolbar);
        mDeviceListFragment.setOnSelectionListener(this);

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, mAboutFragment);
        fragmentTransaction.add(R.id.container, mUserSignInFragment);
        fragmentTransaction.add(R.id.container, mDeviceListFragment);
        fragmentTransaction.hide(mAboutFragment);
        fragmentTransaction.hide(mUserSignInFragment);
        fragmentTransaction.hide(mDeviceListFragment);
        fragmentTransaction.commit();
    }

    private void resetLayout() {
        MenuItem menuSignin = mNavigationView.getMenu().findItem(R.id.nav_signin);
        MenuItem menuSignout = mNavigationView.getMenu().findItem(R.id.nav_signout);
        if (MainApplication.isUserSignedIn()) {
            if (menuSignin != null) {
                menuSignin.setEnabled(false);
                menuSignin.setVisible(false);
            }
            if (menuSignout != null) {
                menuSignout.setEnabled(true);
                menuSignout.setVisible(true);
            }
        }
        else {
            if (menuSignin != null) {
                menuSignin.setEnabled(true);
                menuSignin.setVisible(true);
            }
            if (menuSignout != null) {
                menuSignout.setEnabled(false);
                menuSignout.setVisible(false);
            }
        }
    }

    private void hideAllFragments()
    {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        fragmentTransaction.hide(mUserSignInFragment);
        fragmentTransaction.hide(mAboutFragment);
        fragmentTransaction.hide(mDeviceListFragment);

        fragmentTransaction.commit();
    }

    private void showAbout()
    {
        if(DEBUG)
            Log.d(TAG, "showAbout");

        hideAllFragments();

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.show(mAboutFragment);
        fragmentTransaction.commit();

        mToolbar.setTitle(R.string.title_about);
    }

    private void showDeviceList()
    {
        if(DEBUG)
            Log.d(TAG, "showDeviceList");

        hideAllFragments();

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.show(mDeviceListFragment);
        fragmentTransaction.commit();

        mToolbar.setTitle(R.string.title_device);
    }

    private void showUserSignIn()
    {
        if(DEBUG)
            Log.d(TAG, "showUserSignIn");

        hideAllFragments();

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.show(mUserSignInFragment);
        fragmentTransaction.commit();

        mToolbar.setTitle(R.string.title_signin);
    }

    private void signOut()
    {
        if(DEBUG)
            Log.d(TAG, "signOut");

        if(!MainApplication.isUserSignedIn())
            return;

        UserManager userManager = MainApplication.getUserManager();

        userManager.xmsSignOut();

        resetLayout();
    }

    private void showSignOutDialog()
    {
        if(DEBUG)
            Log.d(TAG, "showSignOutDialog");

        UserManager userManager = MainApplication.getUserManager();

        if(!MainApplication.isUserSignedIn())
            return;

        if(userManager==null)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.WaltzDialog);

        builder.setIcon(R.drawable.icon_irrigation_small);
        builder.setTitle(R.string.app_name);
        builder.setMessage(getString(R.string.sure_to_sign_out) + " (" + userManager.getUsername() + ") ?");
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int id)
            {
                signOut();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                //
            }
        });

        builder.create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (DEBUG)
            Log.d(TAG, "onCreate" + ", bundle: " + savedInstanceState);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Variables Setup
        mFragmentManager = getSupportFragmentManager();

        // Layout Setup
        setupLayout();
    }

    @Override
    protected void onDestroy() {
        if (DEBUG)
            Log.d(TAG, "onDestroy");

        super.onDestroy();
    }

    @Override
    protected void onStart() {
        if (DEBUG)
            Log.d(TAG, "onStart");
        super.onStart();

    }

    @Override
    protected void onStop() {
        if (DEBUG)
            Log.d(TAG, "onStop");

        super.onStop();
    }

    @Override
    public void onResume() {
        if (DEBUG)
            Log.d(TAG, "onResume");
        super.onResume();

        resetLayout();
    }

    @Override
    public void onPause() {
        if (DEBUG)
            Log.d(TAG, "onPause");

        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (DEBUG)
            Log.d(TAG, "onBackPressed");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (DEBUG)
            Log.d(TAG, "onCreateOptionsMenu");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (DEBUG)
            Log.d(TAG, "onOptionsItemSelected");

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //@SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) { // due to "implements NavigationView.OnNavigationItemSelectedListener"
        if (DEBUG)
            Log.d(TAG, "onNavigationItemSelected");

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_plant) {
            hideAllFragments();
        } else if (id == R.id.nav_device) {
            showDeviceList();
        } else if (id == R.id.nav_signin) {
            showUserSignIn();
        } else if (id == R.id.nav_signout) {
            showSignOutDialog();
        } else if (id == R.id.nav_about) {
            showAbout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSignInSuccess() {
        if (DEBUG)
            Log.d(TAG, "onSignInSuccess");

        resetLayout();
        hideAllFragments();
    }

    @Override
    public void onSignInFail() {
        if (DEBUG)
            Log.d(TAG, "onSignInFail");

        hideAllFragments();
    }

    @Override
    public void onSignInTimeOut() {
        if (DEBUG)
            Log.d(TAG, "onSignInTimeOut");
    }

    @Override
    public void onForgetPassword(String account, String email) {
        if (DEBUG)
            Log.d(TAG, "onForgetPassword");
    }

    @Override
    public void onResetPasswordTimeOut(String errorMessage) {
        if (DEBUG)
            Log.d(TAG, "onForgetPassword");
    }

    @Override
    public void onShowDevice(int index, boolean fullScreen) {
        if(DEBUG)
            Log.d(TAG, "onShowDevice i:" + index);
    }
}
