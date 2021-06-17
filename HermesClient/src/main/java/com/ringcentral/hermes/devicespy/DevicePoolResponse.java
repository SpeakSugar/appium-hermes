package com.ringcentral.hermes.devicespy;

import java.util.List;

public class DevicePoolResponse {

    private boolean success;
    private double code;
    List<DeviceCapabilities> data;
    int version;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public double getCode() {
        return code;
    }

    public void setCode(double code) {
        this.code = code;
    }

    public List<DeviceCapabilities> getData() {
        return data;
    }

    public void setData(List<DeviceCapabilities> data) {
        this.data = data;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
