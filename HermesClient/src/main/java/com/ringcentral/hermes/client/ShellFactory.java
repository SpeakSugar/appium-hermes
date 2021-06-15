package com.ringcentral.hermes.client;

import com.ringcentral.hermes.devicespy.DevicePoolApiClient;
import com.ringcentral.hermes.util.ShellUtil;

public class ShellFactory {

    public static ShellExec getShellExec(String hostName) {
        if (hostName.equals("127.0.0.1")) {
            return new ShellUtil();
        } else {
            return new DevicePoolApiClient(hostName);
        }
    }

    public interface ShellExec {
        String executeCmd(String command);
    }
}
