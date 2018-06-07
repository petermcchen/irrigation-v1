package com.accton.iot.irrigationv1.management.user.api;

import android.util.Log;

import com.accton.iot.irrigationv1.management.user.request.CreateUserSessionRequest;
//import com.accton.iot.irrigationv1.management.user.request.UserAuthenticationRequest;
//import com.accton.iot.irrigationv1.management.user.request.UserChangePasswordRequest;
//import com.accton.iot.irrigationv1.management.user.request.UserRegisterRequest;
//import com.accton.iot.irrigationv1.management.user.request.UserResetPasswordRequest;
import com.accton.iot.irrigationv1.management.user.response.CreateUserSessionResponse;
import com.accton.iot.irrigationv1.management.user.response.DestroySessionResponse;
import com.accton.iot.irrigationv1.management.user.response.DeviceQueryResponse;
//import com.accton.iot.irrigationv1.management.user.response.DeviceQueryResponse;
//import com.accton.iot.irrigationv1.management.user.response.UserQueryResponse; // Add Device's member list
//import com.accton.iot.irrigationv1.management.user.request.UserUpdateScopeRequest; // Update Device's member scope
//import com.accton.iot.irrigationv1.management.user.request.UserUpdateDeviceNameRequest; // User update device name
//import com.accton.iot.irrigationv1.management.user.response.UserAuthenticationResponse;
//import com.accton.iot.irrigationv1.management.user.response.UserAuthenticationServiceResponse;
//import com.accton.iot.irrigationv1.management.user.response.UserRegisterResponse;
//import com.accton.iot.irrigationv1.management.user.response.UserQueryDeviceIdResponse;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public class UserRestCommand
{
    private final String TAG = "UserRestCommand";
    private final boolean DEBUG = true;
    private final boolean DEBUG_DETAIL = true;

    private final String DEFAULT_URL = "api.xms.test.metalligence.com";
    private final String DEFAULT_HTTP_PORT = "80";

    private final int DEFAULT_TIMEOUT_CONNECTION = 30;
    private final int DEFAULT_TIMEOUT_READ = 30;

    // Retrofit
    private Retrofit mRetrofit;
    private RestfulService mRestfulService;

    private OkHttpClient.Builder mHttpClientBuilder = new OkHttpClient.Builder();
    private OkHttpClient mHttpClient;
    private UserResponseInterceptor mResponseInterceptor = new UserResponseInterceptor();
    private HttpLoggingInterceptor mLoggingInterceptor = new HttpLoggingInterceptor();

    private UserCookieJar mUserCookieJar = new UserCookieJar();

    // TODO, Custom Trust.

    private interface RestfulService
    {
        //@GET("/um/authlist")
        //Observable<List<UserAuthenticationServiceResponse>> userAuthenticationService(@Query("credential") String credential, @Query("accessid") String accessid);

        //@POST("/um/users")
        //Observable<UserRegisterResponse> userRegister(@Body UserRegisterRequest body);

        @FormUrlEncoded
        @POST("/WS_Services.php?mod=LoginManager&csm=REST")
        Observable<CreateUserSessionResponse> userAuthentication(@Field("func") String func,
                                                                 @Field("TransactionID") String tid,
                                                                 @Field("UserID") String account,
                                                                 @Field("Password") String password,
                                                                 @Field("IP") String ip);
        //Observable<CreateUserSessionResponse> userAuthentication(@Body CreateUserSessionRequest body);

        @FormUrlEncoded
        @POST("/WS_Services.php?mod=LoginManager&csm=REST")
        Observable<DestroySessionResponse> delAuthentication(@Field("func") String func,
                                                             @Field("TransactionID") String tid,
                                                             @Field("SessionID") String sid,
                                                             @Field("RoleLevel") int level,
                                                             @Field("RoleNo") int user);

        //@POST("/um/users/password") // Reset user password
        //Observable<ResponseBody> userResetPassword(@Body UserResetPasswordRequest body);

        //@PUT("/um/users/password")
        //Observable<String> userChangePassword(@Body UserChangePasswordRequest body);

        @FormUrlEncoded
        @POST("/WS_Services.php?mod=SensorManager&csm=REST")
        Observable<DeviceQueryResponse> deviceQuery(@Field("func") String func,
                                                    @Field("TransactionID") String tid,
                                                    @Field("SessionID") String sid,
                                                    @Field("RoleLevel") int level,
                                                    @Field("RoleNo") int user,
                                                    @Field("CustomerUserNo") int customer,
                                                    @Field("SensorType") String type);

        //@GET("/dm/devices/{id}/account") // Add Device's member list
        //Observable<List<UserQueryResponse>> userQuery(@Path("id") int deviceId, @Query("usertoken") String userToken);

        //@DELETE("/dm/devices/{did}/account/{uid}") // Delete Device's member
        //Observable<ResponseBody> removeUser(@Path("did") int deviceId, @Path("uid") int userId, @Query("usertoken") String userToken);

        //@PUT("/dm/devices/{did}/account/{uid}") // Update Device's member scope
        //Observable<ResponseBody> updateUserScope(@Path("did") int deviceId, @Path("uid") int userId, @Body UserUpdateScopeRequest body);

        //@DELETE("/dm/devices/{did}") // Delete device from user ot reset all links from owner
        //Observable<ResponseBody> resetDevice(@Path("did") int deviceId, @Query("usertoken") String userToken);

        //@PUT("/dm/devices/{did}/devicename") // User update device name
        //Observable<ResponseBody> updateDeviceName(@Path("did") int deviceId, @Body UserUpdateDeviceNameRequest body);

        //@GET("/dm/devices/deviceid") // TODO, [Anonymous User], new api for application.
        //Observable<UserQueryDeviceIdResponse> userQueryDeviceId(@Query("mac") String mac, @Query("sno") String sno);
    }

    public UserRestCommand()
    {
        if(DEBUG)
            Log.d(TAG, "call UserRestCommand\n");
        mHttpClientBuilder.connectTimeout(DEFAULT_TIMEOUT_CONNECTION, TimeUnit.SECONDS);
        mHttpClientBuilder.readTimeout(DEFAULT_TIMEOUT_READ, TimeUnit.SECONDS);

        mHttpClientBuilder.addInterceptor(mResponseInterceptor);

        mHttpClientBuilder.cookieJar(mUserCookieJar);

        // TODO, Custom Trust.

        if(DEBUG_DETAIL)
        {
            mLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            mHttpClientBuilder.addInterceptor(mLoggingInterceptor);
        }

        mHttpClient = mHttpClientBuilder.build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://" + DEFAULT_URL + ":" + DEFAULT_HTTP_PORT)
                .client(mHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        mRestfulService = mRetrofit.create(RestfulService.class);
    }

    private class UserCookieJar implements CookieJar
    {
        private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies)
        {
            cookieStore.put(url.host(), cookies);
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url)
        {
            List<Cookie> cookies = cookieStore.get(url.host());
            return cookies != null ? cookies : new ArrayList<Cookie>();
        }
    };

    //
    //
    // Redirect
    //
    //

    //public Observable<List<UserAuthenticationServiceResponse>> getUserAuthenticationServiceList()
    //{
    //    return mRestfulService.userAuthenticationService(null, null);
    //}

    //public Observable<List<UserAuthenticationServiceResponse>> getUserAuthenticationServiceList(String credential, String accessid)
    ///{
    //    return mRestfulService.userAuthenticationService(credential, accessid);
    //}

    //public Observable<UserRegisterResponse> registerAccount(String type, String user, String email)
    //{
    //    return mRestfulService.userRegister(new UserRegisterRequest(type, user, email));
    //}

    public Observable<CreateUserSessionResponse> grantUserAuthentication(String tid, String account, String password)
    {
        //return mRestfulService.userAuthentication(new CreateUserSessionRequest("CreateUserSession", tid, account, password,"127.0.0.1"));
        return mRestfulService.userAuthentication("CreateUserSession", tid, account, password,"127.0.0.1");
    }

    public Observable<DestroySessionResponse> dismissUserAuthentication(String tid, String sid, int roleno)
    {
        return mRestfulService.delAuthentication("DestorySession", tid, sid, 1, roleno); // TODO, use DestorySession!!!
    }

    //public Observable<ResponseBody> resetUserPassword(String username, String email)
    //{
    //    return mRestfulService.userResetPassword(new UserResetPasswordRequest(username, email));
    //}

    //public Observable<String> changeUserPassword(String credential, String accessid, String password)
    //{
    //    return mRestfulService.userChangePassword(new UserChangePasswordRequest(credential, accessid, password));
    //}

    public Observable<DeviceQueryResponse> deviceQuery(String tid, String sid, int roleno, int customer, String type)
    {
        return mRestfulService.deviceQuery("QuerySensorsByUser", tid, sid, 1, roleno, customer, type);
    }

    //public Observable<List<UserQueryResponse>> userQuery(int deviceId, String userToken) // Add Device's member list
    //{
    //    if(DEBUG)
    //        Log.d(TAG, "call userQuery n: " + deviceId + ", u:" +userToken);

    //    return mRestfulService.userQuery(deviceId, userToken);
    //}

    //public Observable<ResponseBody> removeUser(int deviceId, int userId, String userToken) // Delete Device's member
    //{
    //    if(DEBUG)
    //        Log.d(TAG, "call removeUser");
    //    // Non-body HTTP method cannot contain @Body or @TypedOutput constraint!
    //    //return mRestfulService.removeUser(deviceId, userId, new UserRemoveMemberRequest(userToken));
    //    return mRestfulService.removeUser(deviceId, userId, userToken);
    //}

    //public Observable<ResponseBody> updateUserScope(int deviceId, int userId, String usertoken, String scope) // Update Device's member scop
    //{
    //    if(DEBUG)
    //        Log.d(TAG, "call updateUser");
    //    return mRestfulService.updateUserScope(deviceId, userId, new UserUpdateScopeRequest(usertoken, scope));
    //}

    //public Observable<ResponseBody> resetDevice(int deviceId, String userToken) // User remove device link or owner reset all device links.
    //{
    //    if(DEBUG)
    //        Log.d(TAG, "call resetDevice");
    //    return mRestfulService.resetDevice(deviceId, userToken);
    ///}

    //public Observable<ResponseBody> updateDeviceName(int deviceId, String usertoken, String devicename) // User update device name
    //{
    //    if(DEBUG)
    //        Log.d(TAG, "call updateDeviceName");
    //    return mRestfulService.updateDeviceName(deviceId, new UserUpdateDeviceNameRequest(usertoken, devicename));
    //}

    // [Anonymous User], new api for application.
    //public Observable<UserQueryDeviceIdResponse> userQueryDeviceId(String mac, String sno) // User query device id
    //{
    //    if(DEBUG)
    //        Log.d(TAG, "call userQueryDeviceId m: " + mac + ", s:" + sno);

    //    return mRestfulService.userQueryDeviceId(mac, sno);
    //}

    // New public for access success code

    public int getSuccessCode()
    {
        if(DEBUG)
            Log.d(TAG, "getSuccessCode");

        if (mResponseInterceptor != null)
            return mResponseInterceptor.getSuccessCode();
        return (-1); // error check
    }
}