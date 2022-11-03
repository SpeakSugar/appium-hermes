package com.ringcentral.hermes.test;

import com.ringcentral.hermes.factory.ShellFactory;
import com.ringcentral.hermes.util.CmdUtil;
import org.junit.Test;

public class CmdUtilTest {

    @Test
    public void testIOSCmdUtil() {
        ShellFactory.ShellExec shellExec = ShellFactory.getShellExec("http://aqa01-i01-xta02.lab.nordigy.ru:10010/", "10.32.35.12");
        CmdUtil.IOSCmdUtil iosCmdUtil = new CmdUtil.IOSCmdUtil(shellExec);
        try {
            iosCmdUtil.removeForwardPort("15d9c4e8845e008aac0c0a7efb8405d0fba2ed08", "9734");
            iosCmdUtil.forwardPort("15d9c4e8845e008aac0c0a7efb8405d0fba2ed08", "9734");
        } finally {
            iosCmdUtil.removeForwardPort("15d9c4e8845e008aac0c0a7efb8405d0fba2ed08", "9734");
        }
    }

}
