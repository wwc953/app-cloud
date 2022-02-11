package com.example.apputil.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Tracer {
    public static Tracer.Builder builder() {
        return new Tracer.Builder();
    }

    public static class Builder {
        private Span span = new Span();

        public Span getSpan() {
            return span;
        }

        public void setSpan(Span span) {
            this.span = span;
        }

        public void startRpc(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        }

        public void endRpc() {

        }
    }
}
