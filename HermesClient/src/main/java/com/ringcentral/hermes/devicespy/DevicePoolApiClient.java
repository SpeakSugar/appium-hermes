package com.ringcentral.hermes.devicespy;

import com.ringcentral.hermes.factory.ShellFactory;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DevicePoolApiClient implements ShellFactory.ShellExec {

    private static final Logger LOG = LoggerFactory.getLogger(DevicePoolApiClient.class);

    private iDevicePoolService devicePoolService;

    private String hostName;

    public DevicePoolApiClient(String baseUrl, String hostName) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        devicePoolService = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build().create(iDevicePoolService.class);
        this.hostName = hostName;
    }

    public String executeCmd(String command) {
        LOG.info("The command is: " + command);
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("hostname", this.hostName);
            requestBody.put("cmd", command);
            requestBody.put("timeout", 10);
            Call<CommonDevicePoolResponse> call = devicePoolService.executeCommand(requestBody);
            Response<CommonDevicePoolResponse> response = call.execute();
            if (response.code() >= 200 && response.code() < 400) {
                String res = String.valueOf(response.body().getData().get("res"));
                LOG.info("The command result is: " + res);
                return res;
            } else {
                String res = response.errorBody().string();
                LOG.info("The command result is: " + res);
                return res;
            }
        } catch (IOException e) {
            return null;
        }
    }

    public String getAdbPort(String udid) {
        try {
            Call<DevicePoolResponse> call = devicePoolService.fetchDevices(true);
            Response<DevicePoolResponse> response = call.execute();
            if (response.code() >= 200 && response.code() < 400) {
                List<DeviceCapabilities> deviceCapabilitiesList = response.body().getData();
                for (DeviceCapabilities deviceCapabilities : deviceCapabilitiesList) {
                    if (deviceCapabilities.getCapabilities().getUdid().equals(udid)) {
                        return String.valueOf(deviceCapabilities.getCapabilities().getAdbPort());
                    }
                }
                throw new RuntimeException("Can't find device with udid: " + udid);
            } else {
                throw new RuntimeException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
