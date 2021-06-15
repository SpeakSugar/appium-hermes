package com.ringcentral.hermes.devicespy;

import com.ringcentral.hermes.client.ShellFactory;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DevicePoolApiClient implements ShellFactory.ShellExec {

    private static final Logger LOG = LoggerFactory.getLogger(DevicePoolApiClient.class);

    private iDevicePoolService devicePoolService;

    private String hostName;

    public DevicePoolApiClient(String hostName) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        devicePoolService = new Retrofit.Builder()
                .baseUrl("http://aqa01-i01-xta02.lab.nordigy.ru:10000/")
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
            requestBody.put("timeout", 60);
            Call<CommonDevicePoolResponse> call = devicePoolService.executeCommand(requestBody);
            Response<CommonDevicePoolResponse> response = call.execute();
            if (response.code() >= 200 && response.code() < 400) {
                return String.valueOf(response.body().getData().get("res"));
            } else {
                throw new RuntimeException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
