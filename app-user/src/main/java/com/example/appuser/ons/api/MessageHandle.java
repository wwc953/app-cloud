package com.example.appuser.ons.api;

public interface MessageHandle {
    boolean handle(String msgKey, String msg);
}
