package com.ringcentral.hermes.test;

import com.ringcentral.hermes.devicespy.DevicePoolApiClient;
import com.ringcentral.hermes.util.ShellUtil;
import org.junit.Test;

public class ShellCommandTest {

    @Test
    public void shellTest1() {
        System.out.println(new ShellUtil().executeCmd("iproxy 4376 8080 &"));
        String pid = new ShellUtil().executeCmd("lsof -i:4376 | awk '{print $2}' | sed -n '2p'");
        System.out.println("pid = " + pid);
        new ShellUtil().executeCmd("kill -9 " + pid);
    }

    @Test
    public void shellTest2() {
        System.out.println(new ShellUtil().executeCmd("adb devices"));
    }

    @Test
    public void testDeviceSpyClient() {
        DevicePoolApiClient client = new DevicePoolApiClient("http://aqa01-i01-xta02.lab.nordigy.ru:10000/", "10.32.46.225");
        client.executeCmd("iproxy -u 00008110-00014D542638801E -s 0.0.0.0 9732:8080 & \r");
        String result = client.executeCmd("lsof -i:9732 | grep iproxy | awk '{print $2}'");
        System.out.println(result);
    }

}
