package com.example.appstaticutil.response;

import java.io.Serializable;

public class ResponseResult<T> implements Serializable {
    private String code;
    private String message;
    private transient T data;
    private long total;
    private String traceID;

    public ResponseResult() {

    }

    public ResponseResult(T data) {
        this(ResponseContant.SUCCESS, "success", data);
    }

    public ResponseResult(String code, String message) {
        this(code, message, null);
    }

    public ResponseResult(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(data);
    }

    public static <T> ResponseResult<T> success() {
        return new ResponseResult<T>(null);
    }

    public static <T> ResponseResult<T> fail(String code, String message, T data) {
        return new ResponseResult<T>(code, message, data);
    }

    public static <T> ResponseResult<T> fail(String code, String message) {
        return new ResponseResult<T>(code, message);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getTraceID() {
        return traceID;
    }

    public void setTraceID(String traceID) {
        this.traceID = traceID;
    }


}
