package com.ringcentral.hermes.client.interceptor;

import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpLogger implements HttpLoggingInterceptor.Logger {

    public Logger LOG = LoggerFactory.getLogger(HttpLogger.class);

    @Override
    public void log(String message) {
        LOG.info("RetrofitLog = " + message);
    }
}
