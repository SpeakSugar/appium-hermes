package com.ringcentral.hermes.client.browser;

import com.ringcentral.hermes.response.ResponseBean;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface iBrowserService {

    @POST("/openLink")
    Call<ResponseBean> openLink(@Body String url);

}
