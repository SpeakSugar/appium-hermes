package com.ringcentral.hermes.test;

import com.ringcentral.hermes.devicespy.DevicePoolApiClient;
import org.junit.Test;

public class DeviceSpyClientTest {

    @Test
    public void cmdTest() {
        System.out.println(new DevicePoolApiClient("http://aqa01-i01-xta02.lab.nordigy.ru:10000/","10.32.46.148").executeCmd("lsof -i:7614 | awk '{print $2}' | sed -n '2p'"));
    }

    @Test
    public void spyTest() {
        System.out.println(new DevicePoolApiClient("http://aqa01-i01-xta02.lab.nordigy.ru:10000/","10.32.46.148").getAdbPort("RF8M41HVHKK"));
    }

}
