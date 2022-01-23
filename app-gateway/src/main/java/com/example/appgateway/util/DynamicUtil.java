package com.example.appgateway.util;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class DynamicUtil {
    public static final String HTTP = "http";

    public static URI getUri(String uriStr) {
        URI uri = null;
        if (uriStr.startsWith(HTTP)) {
            uri = UriComponentsBuilder.fromHttpUrl(uriStr).build().toUri();
        } else {
            uri = UriComponentsBuilder.fromUriString(uriStr).build().toUri();
        }
        return uri;
    }
}
