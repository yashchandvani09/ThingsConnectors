package com.gears42.thingdemo;


import com.gears42.iot.webthing.CredentialsIx;

public class Credential implements CredentialsIx {
    static String EMAIL ;
    static String PASSWORD ;
    static String KEY ;

    @Override
    public void setEmail(String email) {
        EMAIL=email;
    }

    @Override
    public void setPassword(String password) {
        PASSWORD=password;
    }

    @Override
    public void setKey(String key) {
        KEY=key;
    }

    @Override
    public String getEmail() {
        return EMAIL;
    }

    @Override
    public String getPassword() {
        return PASSWORD;
    }

    @Override
    public String getKey() {
        return KEY;
    }
}

