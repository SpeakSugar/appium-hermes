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

    @Test
    public void testIOSCmdUtil2() {
        ShellFactory.ShellExec shellExec = ShellFactory.getShellExec("http://aqa01-i01-xta02.lab.nordigy.ru:10000/", "10.32.46.227");
        CmdUtil.IOSCmdUtil iosCmdUtil = new CmdUtil.IOSCmdUtil(shellExec);
        try {
            iosCmdUtil.removeForwardPort("00008030-000A70583A2B402E", "9734");
            iosCmdUtil.forwardPort("00008030-000A70583A2B402E", "9734");
        } finally {
            iosCmdUtil.removeForwardPort("00008030-000A70583A2B402E", "9734");
        }
    }

    @Test
    public void testIOSCmdUtil3() {
        ShellFactory.ShellExec shellExec = ShellFactory.getShellExec("http://aqa01-i01-xta02.lab.nordigy.ru:10010/", "10.32.35.12");
//        CmdUtil.IOSCmdUtil iosCmdUtil = new CmdUtil.IOSCmdUtil(shellExec);
//        iosCmdUtil.getAppVersion("15d9c4e8845e008aac0c0a7efb8405d0fba2ed08", "org.ringcentral.hermes", false);
        shellExec.executeCmd("iproxy -u 15d9c4e8845e008aac0c0a7efb8405d0fba2ed08 -s 0.0.0.0 9734:8080");
//        shellExec.executeCmd("iproxy -v");
    }

    @Test
    public void testIOSCmdUtil4() {
        ShellFactory.ShellExec shellExec = ShellFactory.getShellExec();
        CmdUtil.IOSCmdUtil iosCmdUtil = new CmdUtil.IOSCmdUtil(shellExec);
        try {
            iosCmdUtil.removeForwardPort("00008020-000D495E1AD0003A", "9738");
            iosCmdUtil.forwardPort("00008020-000D495E1AD0003A", "9738");
        } finally {
            iosCmdUtil.removeForwardPort("00008020-000D495E1AD0003A", "9738");
        }
    }
}
