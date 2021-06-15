package com.ringcentral.hermes.test;

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



}
