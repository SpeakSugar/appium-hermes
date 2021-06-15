package com.ringcentral.hermes.test;

import com.ringcentral.hermes.devicespy.DevicePoolApiClient;
import org.junit.Test;

public class DeviceSpyClientTest {

    @Test
    public void cmdTest() {
        System.out.println(new DevicePoolApiClient("10.32.46.148").executeCmd("lsof -i:7614 | awk '{print $2}' | sed -n '2p'"));
    }


}
