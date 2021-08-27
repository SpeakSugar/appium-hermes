package com.ringcentral.hermes.exception;

import com.ringcentral.hermes.client.ShellFactory;

public class HermesAndroidPortMappingException extends HermesException {

    private ShellFactory.ShellExec shellExec;

    private String udid;

    public HermesAndroidPortMappingException(ShellFactory.ShellExec shellExec, String udid) {
        super("");
        this.shellExec = shellExec;
        this.udid = udid;
    }

    public ShellFactory.ShellExec getShellExec() {
        return shellExec;
    }

    public String getUdid() {
        return udid;
    }
}
