package com.example.apputil.ons.api;

public interface MessageHandle {
    boolean handle(String msgKey, String msg);
}
