package com.ringcentral.hermes.factory

import com.ringcentral.hermes.client.BrowserApiClient
import com.ringcentral.hermes.client.CalendarApiClient
import com.ringcentral.hermes.client.ContactApiClient
import com.ringcentral.hermes.devicespy.DevicePoolApiClient
import com.ringcentral.hermes.exception.HermesAndroidPortMappingException
import com.ringcentral.hermes.exception.HermesException
import com.ringcentral.hermes.util.CmdUtil
import com.ringcentral.hermes.util.DriverUtil
import com.ringcentral.hermes.util.EnvUtil
import com.ringcentral.hermes.util.ReflectUtil
import io.appium.java_client.AppiumDriver
import org.openqa.selenium.By
import org.openqa.selenium.Capabilities
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URL

class HermesClientFactory {

    private val LOG: Logger = LoggerFactory.getLogger(HermesClientFactory::class.java)

    lateinit var contactApiClient: ContactApiClient

    lateinit var calendarApiClient: CalendarApiClient

    lateinit var browserApiClient: BrowserApiClient

    fun setUp(driver: AppiumDriver<*>, hermesAppPath: String) {
        try {
            val bundleId = "org.ringcentral.hermes"
            val appiumUrl = ReflectUtil.getValueFromParentClass(driver, "io.appium.java_client.AppiumDriver", "remoteAddress") as URL
            val capabilities = ReflectUtil.getValueFromParentClass(driver, "org.openqa.selenium.remote.RemoteWebDriver", "capabilities") as Capabilities
            var udid = capabilities.getCapability("udid") as String
            val platformName = driver.platformName
            var hermesPort = appiumUrl.port + 5000
            var hostName = appiumUrl.host
            val shellExec = if (platformName == "ios") ShellFactory.getShellExec(hostName) else ShellFactory.getShellExec()
            val isIOSSimulator = hermesAppPath.contains(".zip")
            //1. install/update hermes app, and launch it
            val appVersion = if (platformName == "ios") {
                CmdUtil.IOSCmdUtil(shellExec).getAppVersion(udid, bundleId, isIOSSimulator)
            } else {
                CmdUtil.AndroidCmdUtil(shellExec).getAppVersion(udid, bundleId)
            }
            if (appVersion == "1.0.1") {
                DriverUtil.launch(driver, bundleId)
            } else {
                DriverUtil.updateAndLaunch(driver, bundleId, hermesAppPath)
                // ios permission grant
                if (platformName == "ios") {
                    DriverUtil.clickUntilDisappear(driver, By.xpath("//XCUIElementTypeButton[@name='OK']"))
                    DriverUtil.clickUntilDisappear(driver, By.xpath("//XCUIElementTypeButton[@name='WLAN & Cellular']"))
                }
            }
            //2. port mapping
            if (platformName == "ios") {
                if (isIOSSimulator) {
                    hermesPort = 8080
                } else {
                    val iosCmdUtil = CmdUtil.IOSCmdUtil(shellExec)
                    iosCmdUtil.removeForwardPort(udid, hermesPort.toString())
                    iosCmdUtil.forwardPort(udid, hermesPort.toString())
                }
            } else {
                if (hostName != "127.0.0.1") {
                    val adbPort = DevicePoolApiClient(hostName).getAdbPort(udid)
                    udid = "$hostName:$adbPort"
                    hermesPort = adbPort.toInt() + 1000
                    hostName = "127.0.0.1"
                }
                val androidCmdUtil = CmdUtil.AndroidCmdUtil(shellExec)
                androidCmdUtil.adbConnect(udid)
                androidCmdUtil.removeForwardPort(udid, hermesPort.toString())
                androidCmdUtil.forwardPort(udid, hermesPort.toString())
            }
            val hermesUrl = "http://$hostName:$hermesPort"
            //3. init singleton api clients
            contactApiClient = ContactApiClient(hermesUrl)
            calendarApiClient = CalendarApiClient(hermesUrl)
            browserApiClient = BrowserApiClient(hermesUrl)
        } catch (e: Exception) {
            if (e is HermesException) {
                if (e is HermesAndroidPortMappingException) {
                    val isConnect = e.shellExec.executeCmd("${EnvUtil.getAdbPath()}adb devices").contains(e.udid)
                    throw HermesException("adb port mapping failed, device connected is $isConnect")
                } else {
                    throw e
                }
            } else {
                throw HermesException("Undefine Exception", e)
            }
        }
    }

}