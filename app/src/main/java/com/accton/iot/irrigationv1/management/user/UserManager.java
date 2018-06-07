package com.accton.iot.irrigationv1.management.user;

import android.util.Log;

import com.accton.iot.irrigationv1.management.device.SensorDevice;
import com.accton.iot.irrigationv1.management.user.api.UserRestCommand;
import com.accton.iot.irrigationv1.management.user.response.CreateUserSessionResponse;
import com.accton.iot.irrigationv1.management.user.response.DestroySessionResponse;
import com.accton.iot.irrigationv1.management.user.response.DeviceQueryResponse;
import com.accton.iot.irrigationv1.management.user.response.SensorArrayData;
import com.accton.iot.irrigationv1.management.user.response.SensorData;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.schedulers.Schedulers;

public class UserManager {
    private final String TAG = "UserManager";
    private final boolean DEBUG = true;

    // Error Code
    public static final int ERROR_NETWORK = 0xff;

    private UserRestCommand mRestCommand = new UserRestCommand();
    private boolean mInitialized = false;
    private boolean mInitializeSuccess = false;
    private String mUsername = null;
    private String mEmail = null;
    private String mPassword = null;
    private boolean mSignInDone = false;
    private boolean mSignInSuccess = false;
    private boolean mResetUserPasswordDone = false;
    private String mSessionID = null;
    private int mUserNo = -1;
    private int mCustomerUserNo = -1;

    private int mError = -1;
    private String mErrorMessage = "TBD...";

    private List<SensorDevice> mDeviceInfoList = new ArrayList<SensorDevice>();

    private void setError(Throwable throwable)
    {
        String err_code;
        HttpException exception = null;

        if (DEBUG)
            Log.d(TAG, "setError t:" + throwable);

        if(throwable instanceof HttpException)
        {
            exception = (HttpException) throwable;
            if (exception == null)
                return;
            Response response = exception.response();
            String ss=throwable.getMessage();

            if(DEBUG)
                Log.d(TAG, "setError, response:" + response);
            if (response == null)
                return;

            int status = 400; // default error response code
            status = response.code();
            if(DEBUG)
                Log.d(TAG, "setError status:" + status);

            mError = ERROR_NETWORK; // default error_code
            mErrorMessage = throwable.toString();
            try {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                err_code = jObjError.getString("error_code");
                mError = Integer.parseInt(err_code.substring(2), 16); // exclude "0x" characters
                mErrorMessage = jObjError.getString("error_message");
                if(DEBUG) {
                    Log.d(TAG, "setError error message:" + jObjError.getString("error_message"));
                    Log.d(TAG, "setError error error_code:" + jObjError.getString("error_code"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
        }
    }

    private void addDeviceInfo(int sno, int gno, int live, int type, String sid, String desc)
    {
        if (DEBUG)
            Log.d(TAG, "addDeviceInfo sno:" + sno + " gid:" + gno + " type:" + type + " sid: " + sid);
        if (DEBUG)
            Log.d(TAG, "addDeviceInfo desc: " + desc);

        SensorDevice deviceInfo = new SensorDevice();

        deviceInfo.setSensorNo(sno);
        deviceInfo.setSensorId(sid);
        deviceInfo.setGatewayId(gno);
        deviceInfo.setSensorType(type);

        mDeviceInfoList.add(deviceInfo);
    }

    private void queryDevices()
    {
        if (DEBUG)
            Log.d(TAG, "queryDevices" + ", sid: " + mSessionID + ", uid: " + mUserNo + ", cid: " + mCustomerUserNo);

        mDeviceInfoList.clear();

        mRestCommand.deviceQuery(ganerateTransactionID(), mSessionID, mUserNo, mCustomerUserNo, "")
                .subscribeOn(Schedulers.newThread())
                //.flatMap(device -> Observable.from(device))
                .doOnCompleted(() -> {})
                .subscribe(response -> saveDeviceQueryResponse(response), response -> setError(response));
    }

    private void signInSuccess()
    {
        if (DEBUG)
            Log.d(TAG, "signInSuccess");

        mSignInDone = true;
        mSignInSuccess = true;

        queryDevices();
    }

    private void signInFail(Throwable throwable)
    {
        if (DEBUG)
            Log.d(TAG, "signInFail t:" + throwable);

        mSignInDone = true;
        mSignInSuccess = false;

        mUsername = null;
        mEmail = null;
        mPassword = null;

        setError(throwable);
    }

    private void onCompleted()
    {
        if (DEBUG)
            Log.d(TAG, "onCompleted");
    }

    public UserManager()
    {
        initialize();
    }

    public void initialize() {
        mInitialized = true;
        mInitializeSuccess = true;
    }

    public void reinitialize()
    {
        if (DEBUG)
            Log.d(TAG, "reinitialize");

        mInitialized = false;
        mInitializeSuccess = false;

        initialize();
    }

    private void initializeSuccess()
    {
        if (DEBUG)
            Log.d(TAG, "initializeSuccess");

        mInitialized = true;
        mInitializeSuccess = true;
    }

    private void initializeFail()
    {
        if (DEBUG)
            Log.d(TAG, "initializeFail");

        mInitialized = true;
        mInitializeSuccess = false;
    }

    private static String ganerateTransactionID() {
        return Integer.toString((int) (Math.random() * 10000));
    }

    private void saveSession(CreateUserSessionResponse rsp) {
        Gson gson = new Gson();
        String theResponse = gson.toJson(rsp, CreateUserSessionResponse.class);
        if (DEBUG)
            Log.d(TAG, "saveSession, rsp: " + theResponse.toString());
        try {
            JSONObject obj = new JSONObject(theResponse);
            if (obj.getInt("error_code") != 1) {
                if (DEBUG)
                    Log.d(TAG, "saveSession, signin failed");
            } else {
                mSessionID = obj.getString("SessionID");
                mUserNo = obj.getJSONObject("data").getInt("role_no");
                mCustomerUserNo = obj.getJSONObject("data").getInt("customer_user_no");
                //mUserNo = 3; // FixedMe => done.
                if (DEBUG)
                    Log.d(TAG, "mSessionID: " + mSessionID + ", mUserNo: " + mUserNo + ", customer: " + mCustomerUserNo);
                signInSuccess();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (DEBUG)
            Log.d(TAG, "saveSession, SessionID: " + mSessionID + ".");
    }

    private void printDestroySessionResponse(DestroySessionResponse rsp) {
        Gson gson = new Gson();
        String theResponse = gson.toJson(rsp, DestroySessionResponse.class);
        if (DEBUG)
            Log.d(TAG, "printDestroySessionResponse, rsp: " + theResponse.toString());
    }

    private void saveDeviceQueryResponse(DeviceQueryResponse rsp) {
        Gson gson = new Gson();
        String theResponse = gson.toJson(rsp, DeviceQueryResponse.class);
        try {
            JSONObject obj = new JSONObject(theResponse);
            if (obj.getInt("error_code") != 1001) {
                if (DEBUG)
                    Log.d(TAG, "printDeviceQueryResponse, request failed");
            } else {
                int mSensorNo = obj.getJSONObject("data").getInt("num");
                if (DEBUG)
                    Log.d(TAG, "mSensorNo: " + mSensorNo);
                JSONArray mSensorArray = obj.getJSONObject("data").getJSONArray("body");
                if (DEBUG)
                    Log.d(TAG, "sensors.length(): " + mSensorArray.length());
                for (int i = 0; i < mSensorArray.length(); i++) {
                    JSONObject mSensor = mSensorArray.getJSONObject(i);
                    if (DEBUG)
                        Log.d(TAG, "sensor_id: " + mSensor.getString("sensor_id"));

                    addDeviceInfo(mSensor.getInt("sensor_no"),
                            mSensor.getInt("gateway_no"),
                            mSensor.getInt("is_live"),
                            mSensor.getInt("sensor_type"),
                            mSensor.getString("sensor_id"),
                            mSensor.getString("sensor_desc") );
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isInitialized()
    {
        return mInitialized;
    }

    public boolean isInitializeSuccess()
    {
        return mInitializeSuccess;
    }

    public void xmsSignOut()
    {
        if (DEBUG)
            Log.d(TAG, "localSignOut");

        if (!mInitialized)
            return;

        if (!mSignInDone)
            return;

        if (!mSignInSuccess)
            return;

        if (DEBUG)
            Log.d(TAG, "dismissUserAuthentication, sid: " + mSessionID);
        mRestCommand.dismissUserAuthentication(ganerateTransactionID(), mSessionID, mUserNo)
                .subscribeOn(Schedulers.newThread())
                //.doOnCompleted(() -> signInSuccess())
                .doOnCompleted(() -> onCompleted())
                .subscribe(response -> printDestroySessionResponse(response), response -> setError(response));

        mSignInDone = false;
        mSignInSuccess = false;

        mUsername = null;
        //mRephraseUserName = null;
        mEmail = null;
        mPassword = null;

    }

    public void xmsSignIn(String user, String email, String password)
    {
        if (DEBUG)
            Log.d(TAG, "localSignIn u:" + user + " m:" + email + " p:" + password);

        mSignInDone = false;
        mSignInSuccess = false;

        //mDeviceInfoList.clear();

        mUsername = user;
        mEmail = email;
        mPassword = password;

        if (DEBUG)
            Log.d(TAG, "grantUserAuthentication, email: " + email);
        mRestCommand.grantUserAuthentication(ganerateTransactionID(), email, password)
                .subscribeOn(Schedulers.newThread())
                //.doOnCompleted(() -> signInSuccess())
                .doOnCompleted(() -> onCompleted()) // TODO... not really successful, must check response error_code.
                .subscribe(response -> saveSession(response), response -> signInFail(response));
                //.doOnCompleted(() -> queryDevices())
                //.subscribe(response -> setToken(response.usertoken, response.accesstoken), response -> signInFail(response));
    }

    public boolean isSignInDone()
    {
        if (DEBUG)
            Log.d(TAG, "isSignInDone, done: " + mSignInDone);
        return mSignInDone;
    }

    public boolean isSignInSuccess()
    {
        if (DEBUG)
            Log.d(TAG, "isSignInSuccess, ok: " + mSignInSuccess);
        return mSignInSuccess;
    }

    public void setUsername(String name)
    {
        mUsername = name;
    }

    public void setEmail(String email)
    {
        mEmail = email;
    }

    public void setPassword(String password)
    {
        mPassword = password;
    }

    public String getUsername()
    {
        return mUsername;
    }

    public String getEmail()
    {
        return mEmail;
    }

    public String getPassword()
    {
        return mPassword;
    }

    public void resetUserPassword(String name, String email)
    {
        if (DEBUG)
            Log.d(TAG, "setForgetPassword, name: " + name + " & email: " + email);
    }

    public boolean isResetUserPasswordDone()
    {
        if (DEBUG)
            Log.d(TAG, "call isResetUserPasswordDone.");
        return mResetUserPasswordDone;
    }

    public String getErrorMessage()
    {
        return mErrorMessage;
    }
}
