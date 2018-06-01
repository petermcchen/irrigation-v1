package com.accton.iot.irrigationv1.management.user.request;

public class CreateUserSessionRequest {
    String func;
    String TransactionID;
    String UserID;
    String Password;
    String IP;
    public CreateUserSessionRequest(String func, String tid, String uid, String pwd, String ip) {
        this.func = func;
        this.TransactionID = tid;
        this.UserID = uid;
        this.Password = pwd;
        this.IP = ip;
    }
}
