package com.ringcentral.hermes.util;

import com.ringcentral.hermes.client.ShellFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class ShellUtil implements ShellFactory.ShellExec {

    private static final Logger LOG = LoggerFactory.getLogger(ShellUtil.class);

    public String executeCmd(String completeCmd) {
        InputStreamReader isr = null;
        LineNumberReader input = null;
        try {
            LOG.info("execute cmd is: " + completeCmd);
            StringBuilder sb = new StringBuilder();
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", completeCmd}, null, null);
            isr = new InputStreamReader(process.getInputStream());
            input = new LineNumberReader(isr);
            String line;
            process.waitFor();
            while ((line = input.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(isr);
        }
    }
}
