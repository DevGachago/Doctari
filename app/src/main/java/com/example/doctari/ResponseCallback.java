package com.example.doctari;

public interface ResponseCallback {
    void onResponse(String response);
    void onError(Throwable throwable);
}

