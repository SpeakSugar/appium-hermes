package com.ringcentral.hermes.util;

import com.ringcentral.hermes.exception.HermesAndroidPortMappingException;
import com.ringcentral.hermes.exception.HermesException;
import com.ringcentral.hermes.factory.ShellFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class CmdUtil {

    public Logger LOG = LoggerFactory.getLogger(CmdUtil.class);

    public ShellFactory.ShellExec shellExec;

    public CmdUtil(ShellFactory.ShellExec shellExec) {
        this.shellExec = shellExec;
    }

    public static class AndroidCmdUtil extends CmdUtil {

        public AndroidCmdUtil(ShellFactory.ShellExec shellExec) {
            super(shellExec);
        }

        public void adbConnect(String udid) {
            String adbPath = EnvUtil.getAdbPath();
            RetryUtil.call(() -> {
                shellExec.executeCmd(adbPath + "adb connect " + udid);
                return shellExec.executeCmd(adbPath + "adb devices").contains(udid);
            }, Predicate.isEqual(false), 10, new HermesException(adbPath + "adb connect " + udid + " failed"));
        }

        public void removeForwardPort(String udid, String port) {
            String adbPath = EnvUtil.getAdbPath();
            RetryUtil.call(() -> {
                String cmd = "lsof -i:" + port + " | grep adb | awk '{print $2}'";
                String pid = shellExec.executeCmd(cmd);
                if (StringUtils.isNotBlank(pid)) {
                    shellExec.executeCmd(adbPath + "adb -s " + udid + " forward --remove tcp:" + port);
                }
                return StringUtils.isBlank(shellExec.executeCmd(cmd));
            }, Predicate.isEqual(false));
        }

        public void forwardPort(String udid, String port) {
            String adbPath = EnvUtil.getAdbPath();
            RetryUtil.call(() -> {
                shellExec.executeCmd(adbPath + "adb -s " + udid + " forward tcp:" + port + " tcp:8080");
                Thread.sleep(2000);
                return StringUtils.isBlank(shellExec.executeCmd("lsof -i:" + port + " | grep adb | awk '{print $2}'"));
            }, Predicate.isEqual(true), 15, new HermesAndroidPortMappingException(shellExec, udid));
            LOG.info(adbPath + "adb -s " + udid + " forward tcp:" + port + " tcp:8080 pid is: "
                    + shellExec.executeCmd("lsof -i:" + port + " | grep adb | awk '{print $2}'"));
        }

        public String getAppVersion(String udid, String packageName) {
            String adbPath = EnvUtil.getAdbPath();
            String cmd = adbPath + "adb -s %s shell dumpsys package %s | grep versionName";
            String output = shellExec.executeCmd(String.format(cmd, udid, packageName));
            if (StringUtils.isNotBlank(output)) {
                return output.split("=")[1].trim();
            } else {
                return "";
            }
        }
    }

    public static class IOSCmdUtil extends CmdUtil {

        public IOSCmdUtil(ShellFactory.ShellExec shellExec) {
            super(shellExec);
        }

        public void removeForwardPort(String udid, String port) {
            RetryUtil.call(() -> {
                String cmd = "lsof -i:" + port + " | grep iproxy | awk '{print $2}'";
                String pid = shellExec.executeCmd(cmd);
                if (StringUtils.isNotBlank(pid)) {
                    shellExec.executeCmd("kill -9 " + pid);
                    Thread.sleep(2000);
                }
                return StringUtils.isBlank(shellExec.executeCmd(cmd));
            }, Predicate.isEqual(false));
        }

        public void forwardPort(String udid, String port) {
            shellExec.executeCmd("iproxy -u " + udid + " -s 0.0.0.0 " + port + ":8080 &");
            RetryUtil.call(() -> {
                return StringUtils.isBlank(shellExec.executeCmd("lsof -i:" + port + " | grep iproxy | awk '{print $2}'"));
            }, Predicate.isEqual(true));
            LOG.info("iproxy " + port + " 8080 " + udid + " pid is: "
                    + shellExec.executeCmd("lsof -i:" + port + " | grep iproxy | awk '{print $2}'"));
        }

        public String getAppVersion(String udid, String packageName, boolean isSimulator) {
            String cmd = isSimulator ? "xcrun simctl appinfo %s %s | grep CFBundleVersion"
                    : "ideviceinstaller -u %s -l | grep %s";
            String output = shellExec.executeCmd(String.format(cmd, udid, packageName));
            if (StringUtils.isNotBlank(output)) {
                return RegexUtil.getMatchString(output, "\"([^\"]*)\"", 1).replace("\"", "");
            } else {
                return "";
            }
        }
    }

}
