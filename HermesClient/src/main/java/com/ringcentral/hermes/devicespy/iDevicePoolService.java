package com.ringcentral.hermes.devicespy;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.Map;

public interface iDevicePoolService {

    @POST("api/v1/hosts/exec_cmd")
    Call<CommonDevicePoolResponse> executeCommand(@Body Map<String, Object> requestMap);

    @GET("api/v1/capabilities")
    Call<DevicePoolResponse> fetchDevices(@Query("debug") boolean debug);

}
