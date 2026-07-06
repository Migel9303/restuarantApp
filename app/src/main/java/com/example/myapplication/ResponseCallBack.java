package com.example.myapplication;

public interface ResponseCallBack {
    void onResponse(String response);

    void onError(Throwable t);
}
