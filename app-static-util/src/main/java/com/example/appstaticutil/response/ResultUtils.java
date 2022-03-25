package com.example.appstaticutil.response;

public class ResultUtils {
    public static <T> ResponseResult<T> warpResult(T t) {
        return new ResponseResult<>(t);
    }

    public static <T> ResponseResult<T> warpResult(T t, int total) {
        ResponseResult<T> tResponseResult = new ResponseResult<>(t);
        tResponseResult.setCode("00000");
        tResponseResult.setData(t);
        tResponseResult.setTotal(total);
        return tResponseResult;
    }

    public static <T> ResponseResult<T> warpResult(String code, String message) {
        return new ResponseResult<>(code, message);
    }
}
