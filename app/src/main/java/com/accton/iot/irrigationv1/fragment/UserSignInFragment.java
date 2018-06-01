package com.accton.iot.irrigationv1.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.accton.iot.irrigationv1.MainApplication;
import com.accton.iot.irrigationv1.R;
import com.accton.iot.irrigationv1.management.user.UserManager;

public class UserSignInFragment extends Fragment implements View.OnClickListener
{
    private final static String TAG = "UserSignInFragment";
    private final static boolean DEBUG = true;

    public interface OnSignInListener
    {
        void onSignInSuccess();
        void onSignInFail();
        void onSignInTimeOut();
        void onForgetPassword(String account, String email);
        void onResetPasswordTimeOut(String errorMessage);
    }

    private final int INIT_INTERVAL = 1000;
    private final int SIGNIN_INTERVAL = 1000;
    private final int SIGNIN_TIMEOUT_INTERVAL = 30000;

    private Handler mInitHandler = new Handler();
    private Handler mSignInHandler = new Handler();
    private Handler mSignInTimeoutHandler = new Handler();
    private Handler mResetUserPasswordHandler = new Handler();

    private UserManager mUserManager;

    private OnSignInListener mListener;

    private Activity mActivity;
    private Toolbar mToolbar;

    private boolean mSigningIn = false;

    private EditText mNameText;
    private EditText mPasswordText;

    private Button mButtonSignIn;

    private ProgressDialog mProgressDialog;

    private int retry=0;

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

        return inflater.inflate(R.layout.fragment_user_sign_in, container, false);
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

        if(mProgressDialog!=null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();

        mSignInHandler.removeCallbacks(mSignInRunnable);
        mSignInTimeoutHandler.removeCallbacks(mSignInTimeoutRunnable);

        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        if(DEBUG)
            Log.d(TAG, "onDestroy");

        super.onDestroy();
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        if(DEBUG)
            Log.d(TAG, "onClick v:" + view + " i:" + id);

        if(id == R.id.button_sign_in)
        {
            signIn();
        }
        else if(id == R.id.link_forgetpwd)
        {
            String name = mNameText.getText().toString();

            if(mListener!=null)
                mListener.onForgetPassword( name, null );
        }
    }

    public void setOnSignInListener(OnSignInListener listener)
    {
        if(DEBUG)
            Log.d(TAG, "setOnSignInListener");

        mListener = listener;
    }

    public void setToolbar(Toolbar actionBar)
    {
        mToolbar = actionBar;
    }

    public void clearInput()
    {
        if(DEBUG)
            Log.d(TAG, "clearInput");

        if(mNameText!=null)
            mNameText.getText().clear();

        if(mPasswordText!=null)
            mPasswordText.getText().clear();
    }

    public void setInput(String name, String password)
    {
        if(DEBUG)
            Log.d(TAG, "setInput");

        if(mNameText!=null)
        {
            mNameText.getText().clear();
            if (name != null)
                mNameText.getText().append(name);
        }

        if(mPasswordText!=null)
        {
            mPasswordText.getText().clear();
            if (password != null)
                mPasswordText.append(password);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        if (DEBUG)
            Log.d(TAG, "onHiddenChanged h:" + hidden);
        if (hidden == false) {
            if (mUserManager!= null) {
                if (mUserManager.isSignInDone())
                    mButtonSignIn.setEnabled(false);
                else
                    mButtonSignIn.setEnabled(true);
            }
            mToolbar.setTitle(getString(R.string.title_user_sign_in));
        }
    }

    public void onSetForgetPassword(String username, String email) {
        if (DEBUG)
            Log.d(TAG, "onSetForgetPassword username: " + username + ", email: " + email + ".");

        mUserManager.resetUserPassword(username, email);
        // Wait for completeness
        retry=0;
        mProgressDialog.setMessage(getString(R.string.reset_password_success));
        mProgressDialog.show();
        mResetUserPasswordHandler.removeCallbacks(mResetUserPasswordRunnable);
        mResetUserPasswordHandler.postDelayed(mResetUserPasswordRunnable, INIT_INTERVAL);
    }

    private void setupLayout()
    {
        if(DEBUG)
            Log.d(TAG, "setupLayout");

        mUserManager = MainApplication.getUserManager();

        mNameText = (EditText) mActivity.findViewById(R.id.sign_input_name);
        mPasswordText = (EditText) mActivity.findViewById(R.id.sign_input_password);

        mButtonSignIn = (Button) mActivity.findViewById(R.id.button_sign_in);

        mButtonSignIn.setOnClickListener(this);

        TextView mForgetPwdRegister = (TextView) mActivity.findViewById(R.id.link_forgetpwd);

        mForgetPwdRegister.setOnClickListener(this);

        mProgressDialog = new ProgressDialog(mActivity, R.style.WaltzProgessBar);

        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        //mProgressDialog.setMessage(getString(R.string.user_authenticating));
    }

    private void resetLayout()
    {
        if(DEBUG)
            Log.d(TAG, "resetLayout");

        if(mToolbar!=null)
            mToolbar.setTitle(getString(R.string.title_user_sign_in));

        //MainApplication.loadAccountData();

        String name = mUserManager.getUsername();
        String email = mUserManager.getEmail();
        String password = mUserManager.getPassword();

        if(name!=null && password!=null)
        {
            setInput(name, password);

            Handler handler = new Handler();

            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    signIn();
                }
            }, 0);
        }
    }

    private boolean isValid()
    {
        boolean valid = true;

        String name = mNameText.getText().toString();
        String password = mPasswordText.getText().toString();

        if(name.isEmpty() || name.length()<=0)
        {
            mNameText.setError(getString(R.string.user_error_empty_name));
            valid = false;
        }
        else
        {
            mNameText.setError(null);
        }

        if(password.isEmpty() || password.length()<=0)
        {
            mPasswordText.setError(getString(R.string.user_error_empty_password));
            valid = false;
        }
        else
        {
            mPasswordText.setError(null);
        }

        return valid;
    }

    private void signIn()
    {
        if(DEBUG)
            Log.d(TAG, "signIn");

        if(mSigningIn)
            return;

        if(!isValid())
            return;

        if(mUserManager==null)
            return;

        if(mProgressDialog==null)
            return;

        if(DEBUG)
            Log.d(TAG, "Check isInitialized...");
        if(!mUserManager.isInitialized())
        {
            mButtonSignIn.setEnabled(false);
            mProgressDialog.show();

            mInitHandler.removeCallbacks(mInitRunnable);
            mInitHandler.postDelayed(mInitRunnable, INIT_INTERVAL);

            return;
        }
        else if(!mUserManager.isInitializeSuccess())
        {
            mSigningIn = false;

            mInitHandler.removeCallbacks(mInitRunnable);
            mButtonSignIn.setEnabled(true);
            mProgressDialog.dismiss();

            if(mListener!=null)
                mListener.onSignInFail();

            mUserManager.reinitialize();

            return;
        }

        mInitHandler.removeCallbacks(mInitRunnable);
        mButtonSignIn.setEnabled(false);
        mProgressDialog.show();

        String name = mNameText.getText().toString();
        String password = mPasswordText.getText().toString();

        //userManager.localSignIn("peterchen", "peter.mc.chen@gmail.com", "peterchen");
        //userManager.localSignIn("test1234", "minstrelsy@gmail.com", "test1234");
        //userManager.localSignIn("test1235", "minstrelsy@gmail.com", "test1235");

        // TODO...
        //mUserManager.localSignIn(name, null, password);
        mUserManager.xmsSignIn(name, name, password);

        mSignInHandler.removeCallbacks(mSignInRunnable);
        mSignInHandler.postDelayed(mSignInRunnable, SIGNIN_INTERVAL);

        mSignInTimeoutHandler.removeCallbacks(mSignInTimeoutRunnable);
        mSignInTimeoutHandler.postDelayed(mSignInTimeoutRunnable, SIGNIN_TIMEOUT_INTERVAL);

        mSigningIn = true;
    }

    private Runnable mInitRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if(DEBUG)
                Log.d(TAG, "mInitRunnable run");

            mInitHandler.removeCallbacks(mInitRunnable);

            signIn();
        }
    };

    private Runnable mSignInTimeoutRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            mSignInHandler.removeCallbacks(mSignInRunnable);
            mSignInTimeoutHandler.removeCallbacks(mSignInTimeoutRunnable);

            mSigningIn = false;
            mButtonSignIn.setEnabled(true);
            mProgressDialog.dismiss();

            if(DEBUG)
                Log.d(TAG, "mSignInTimeoutRunnable sign in timeout");

            if(mListener!=null)
                mListener.onSignInTimeOut();
        }
    };

    private Runnable mSignInRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if(DEBUG)
                Log.d(TAG, "mSignInRunnable run");

            if(!mUserManager.isSignInDone())
            {
                mSignInHandler.removeCallbacks(mSignInRunnable);
                mSignInHandler.postDelayed(mSignInRunnable, SIGNIN_INTERVAL);
            }
            else
            {
                mSigningIn = false;

                mSignInHandler.removeCallbacks(mSignInRunnable);
                mSignInTimeoutHandler.removeCallbacks(mSignInTimeoutRunnable);
                mButtonSignIn.setEnabled(true);
                mProgressDialog.dismiss();

                if(mUserManager.isSignInSuccess())
                {
                    if(DEBUG)
                        Log.d(TAG, "mSignInRunnable sign in success");
                    mButtonSignIn.setEnabled(false);

                    if(mListener!=null)
                        mListener.onSignInSuccess();
                }
                else
                {
                    if(DEBUG)
                        Log.d(TAG, "mSignInRunnable sign in fail");

                    if(mListener!=null)
                        mListener.onSignInFail();
                }
            }
        }
    };

    private Runnable mResetUserPasswordRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if(DEBUG)
                Log.d(TAG, "mResetUserPasswordRunnable run");

            if(!mUserManager.isResetUserPasswordDone())
            {
                if(DEBUG)
                    Log.d(TAG, "isResetUserPasswordDone is false");
                mResetUserPasswordHandler.removeCallbacks(mResetUserPasswordRunnable);
                mResetUserPasswordHandler.postDelayed(mResetUserPasswordRunnable, INIT_INTERVAL);
                mProgressDialog.show();
                if (retry++ > 10) { // simple timeout implementation
                    mResetUserPasswordHandler.removeCallbacks(mResetUserPasswordRunnable);
                    mProgressDialog.dismiss();
                    if (mListener != null) {
                        mListener.onResetPasswordTimeOut(mUserManager.getErrorMessage());
                    }
                }
            }
            else
            {
                if(DEBUG)
                    Log.d(TAG, "isResetUserPasswordDone is true");

                mResetUserPasswordHandler.removeCallbacks(mResetUserPasswordRunnable);
                mProgressDialog.dismiss();
            }
        }
    };
}
