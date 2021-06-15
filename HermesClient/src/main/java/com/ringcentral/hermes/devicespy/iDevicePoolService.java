package com.ringcentral.hermes.devicespy;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.Map;

public interface iDevicePoolService {

    @POST("api/v1/hosts/exec_cmd")
    Call<CommonDevicePoolResponse> executeCommand(@Body Map<String, Object> requestMap);

}
