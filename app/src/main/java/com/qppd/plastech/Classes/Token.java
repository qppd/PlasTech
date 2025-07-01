package com.qppd.plastech.Classes;
public class Token {

    private String device_token;

    public Token(){

    }

    public Token(String device_token) {
        this.device_token = device_token;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }
}

