package com.ringcentral.hermes.devicespy;

import java.util.List;
import java.util.Map;

public class DeviceCapabilities {

    private Capabilities capabilities;
    private String appiumUrl;
    private String capabilityToken;
    private String hostId;
    private long systemPort;
    private int version;
    private Map<String, Object> customizedCapabilities;
    private String capabilityReleaseToken;
    private String sessionId;

    public Capabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Capabilities capabilities) {
        this.capabilities = capabilities;
    }

    public String getAppiumUrl() {
        return appiumUrl;
    }

    public void setAppiumUrl(String appiumUrl) {
        this.appiumUrl = appiumUrl;
    }

    public String getCapabilityToken() {
        return capabilityToken;
    }

    public void setCapabilityToken(String capabilityToken) {
        this.capabilityToken = capabilityToken;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public long getSystemPort() {
        return systemPort;
    }

    public void setSystemPort(long systemPort) {
        this.systemPort = systemPort;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Map<String, Object> getCustomizedCapabilities() {
        return customizedCapabilities;
    }

    public void setCustomizedCapabilities(Map<String, Object> customizedCapabilities) {
        this.customizedCapabilities = customizedCapabilities;
    }

    public String getCapabilityReleaseToken() {
        return capabilityReleaseToken;
    }

    public void setCapabilityReleaseToken(String capabilityReleaseToken) {
        this.capabilityReleaseToken = capabilityReleaseToken;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public static class Capabilities {

        private String platformName;
        private String deviceName;
        private String version;
        private String udid;
        private boolean isSimulator;
        private String hostname;
        private long adbPort;
        private String platformVersion;
        private List<String> labels;
        private String ip;
        private String code;
        private String deviceModel;

        public String getPlatformName() {
            return platformName;
        }

        public void setPlatformName(String platformName) {
            this.platformName = platformName;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getUdid() {
            return udid;
        }

        public void setUdid(String udid) {
            this.udid = udid;
        }

        public boolean isSimulator() {
            return isSimulator;
        }

        public void setSimulator(boolean simulator) {
            isSimulator = simulator;
        }

        public String getHostname() {
            return hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        public long getAdbPort() {
            return adbPort;
        }

        public void setAdbPort(long adbPort) {
            this.adbPort = adbPort;
        }

        public String getPlatformVersion() {
            return platformVersion;
        }

        public void setPlatformVersion(String platformVersion) {
            this.platformVersion = platformVersion;
        }

        public List<String> getLabels() {
            return labels;
        }

        public void setLabels(List<String> labels) {
            this.labels = labels;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDeviceModel() {
            return deviceModel;
        }

        public void setDeviceModel(String deviceModel) {
            this.deviceModel = deviceModel;
        }
    }
}
