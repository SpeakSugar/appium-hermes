package com.ringcentral.hermes.client;

import com.ringcentral.hermes.client.browser.iBrowserService;
import com.ringcentral.hermes.response.ResponseBean;
import com.ringcentral.hermes.util.APIHelper;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class BrowserApiClient {

    private iBrowserService browserService;

    public BrowserApiClient(String url) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        browserService = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build().create(iBrowserService.class);
    }

    public ResponseBean openLink(String url) {
        Call<ResponseBean> call = browserService.openLink(url);
        return APIHelper.callWithRetry(call).body();
    }

}
