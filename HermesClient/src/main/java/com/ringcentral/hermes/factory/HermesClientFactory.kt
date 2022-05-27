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
import io.appium.java_client.AppiumDriver
import org.openqa.selenium.By
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch

class HermesClientFactory {

    private val LOG: Logger = LoggerFactory.getLogger(HermesClientFactory::class.java)

    private val latch: CountDownLatch = CountDownLatch(1)

    private lateinit var contactApiClient: ContactApiClient

    private lateinit var calendarApiClient: CalendarApiClient

    private lateinit var browserApiClient: BrowserApiClient

    fun getContactApiClient(): ContactApiClient {
        latch.await()
        return contactApiClient
    }

    fun getCalendarApiClient(): CalendarApiClient {
        latch.await()
        return calendarApiClient
    }

    fun getBrowserApiClient(): BrowserApiClient {
        latch.await()
        return browserApiClient
    }

    fun setUp(driver: AppiumDriver<*>, hermesAppPath: String, deviceSpyUrl: String) {
        Thread {
            try {
                val bundleId = "org.ringcentral.hermes"
                val appiumUrl = DriverUtil.getAppiumUrl(driver)
                val capabilities = DriverUtil.getCapabilities(driver)
                var udid = capabilities.getCapability("udid") as String
                val platformName = if (hermesAppPath.contains(".apk")) "android" else "ios"
                var hermesPort = appiumUrl.port + 5000
                var hostName = appiumUrl.host
                val shellExec = if (platformName == "ios") ShellFactory.getShellExec(deviceSpyUrl, hostName)
                else ShellFactory.getShellExec()
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
                        val adbPort = DevicePoolApiClient(deviceSpyUrl, hostName).getAdbPort(udid)
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
                        LOG.error("adb port mapping failed, device connected is $isConnect")
                    } else {
                        LOG.error("HermesException", e)
                    }
                } else {
                    LOG.error("HermesUndefineException", e)
                }
            } finally {
                latch.countDown()
            }
        }.start()
    }

}