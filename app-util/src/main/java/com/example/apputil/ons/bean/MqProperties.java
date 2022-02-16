package com.example.apputil.ons.bean;

import com.example.apputil.ons.constant.MqConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class MqProperties {
    private String binder;
    private String accessKey;
    private String secretKey;
    private String sendMsgTimeoutMillis;
    private String namesrvAddr;
    private String consumeThreadNums;
    private String connect;
    private String consumeIdempotency;
    private String redisId;
    private String redisMsgConsumeTimeout;

    public String getBinder() {
        return binder;
    }

    public void setBinder(String binder) {
        this.binder = binder;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSendMsgTimeoutMillis() {
        return sendMsgTimeoutMillis;
    }

    public void setSendMsgTimeoutMillis(String sendMsgTimeoutMillis) {
        this.sendMsgTimeoutMillis = sendMsgTimeoutMillis;
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public String getConsumeThreadNums() {
        return consumeThreadNums;
    }

    public void setConsumeThreadNums(String consumeThreadNums) {
        this.consumeThreadNums = consumeThreadNums;
    }

    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }

    public String getConsumeIdempotency() {
        return consumeIdempotency;
    }

    public void setConsumeIdempotency(String consumeIdempotency) {
        this.consumeIdempotency = consumeIdempotency;
    }

    public String getRedisId() {
        return redisId;
    }

    public void setRedisId(String redisId) {
        this.redisId = redisId;
    }

    public String getRedisMsgConsumeTimeout() {
        return redisMsgConsumeTimeout;
    }

    public void setRedisMsgConsumeTimeout(String redisMsgConsumeTimeout) {
        this.redisMsgConsumeTimeout = redisMsgConsumeTimeout;
    }

    public boolean hasNullProperties() {
        boolean flag = false;
        if (MqConstant.BINDER_ONS.equalsIgnoreCase(binder)) {
            if (StringUtils.isBlank(accessKey)) {
                log.error("accessKey is null");
                return true;
            }
            if (StringUtils.isBlank(secretKey)) {
                log.error("secretKey is null");
                return true;
            }
            if (StringUtils.isBlank(sendMsgTimeoutMillis)) {
                log.error("sendMsgTimeoutMillis is null");
                return true;
            }
            if (StringUtils.isBlank(namesrvAddr)) {
                log.error("namesrvAddr is null");
                return true;
            }
        }
        if (MqConstant.BINDER_KAFKA.equalsIgnoreCase(binder) && StringUtils.isBlank(connect)) {
            log.error("connect is null");
            return true;
        }
        return flag;
    }
}
