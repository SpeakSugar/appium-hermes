package com.ringcentral.hermes.client

import com.ringcentral.hermes.devicespy.DevicePoolApiClient
import com.ringcentral.hermes.exception.HermesAndroidPortMappingException
import com.ringcentral.hermes.exception.HermesException
import com.ringcentral.hermes.util.EnvUtil
import com.ringcentral.hermes.util.ReflectUtil
import com.ringcentral.hermes.util.RetryUtil
import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.appmanagement.AndroidInstallApplicationOptions
import io.appium.java_client.appmanagement.ApplicationState
import org.apache.commons.lang3.StringUtils
import org.openqa.selenium.By
import org.openqa.selenium.Capabilities
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.concurrent.Callable
import java.util.function.Predicate

class HermesClientFactory {

    private val LOG: Logger = LoggerFactory.getLogger(HermesClientFactory::class.java)

    lateinit var contactApiClient: ContactApiClient

    lateinit var calendarApiClient: CalendarApiClient

    fun setUp(driver: AppiumDriver<*>, hermesAppPath: String) {
        try {
            val bundleId = "org.ringcentral.hermes"
            //1. install hermes app, and launch it
            if (driver.isAppInstalled(bundleId)) {
                LOG.info("start to delete hermes app...")
                driver.removeApp(bundleId)
                LOG.info("delete hermes app success.")
            }
            LOG.info("start to install hermes app...")
            RetryUtil.call(Callable {
                driver.installApp(hermesAppPath, AndroidInstallApplicationOptions().withGrantPermissionsEnabled())
                return@Callable true
            }, Predicate.isEqual(false))
            LOG.info("hermes app install success.")
            RetryUtil.call(Callable {
                driver.activateApp(bundleId)
                Thread.sleep(2000)
                return@Callable driver.queryAppState(bundleId) == ApplicationState.RUNNING_IN_FOREGROUND
            }, Predicate.isEqual<Boolean>(false), 10, HermesException("hermes app activate failed."))
            LOG.info("hermes app activate success.")
            //2. port mapping, ios permission grant
            val appiumUrl = ReflectUtil.getValueFromParentClass(driver, "io.appium.java_client.AppiumDriver", "remoteAddress") as URL
            val capabilities = ReflectUtil.getValueFromParentClass(driver, "org.openqa.selenium.remote.RemoteWebDriver", "capabilities") as Capabilities
            var udid = capabilities.getCapability("udid") as String
            val platformName = driver.platformName
            var hermesPort = appiumUrl.port + 5000
            var hostName = appiumUrl.host
            if (platformName == "ios") {
                val shellExec = ShellFactory.getShellExec(hostName)
                val driverWait = WebDriverWait(driver, 5)
                val okButtonConditions = ExpectedConditions.presenceOfElementLocated(By.xpath("//XCUIElementTypeButton[@name='OK']"))
                RetryUtil.call(Callable {
                    try {
                        driverWait.until(okButtonConditions).click()
                        return@Callable driverWait.until(okButtonConditions).isDisplayed
                    } catch (e: Exception) {
                        return@Callable false
                    }
                }, Predicate.isEqual(true))
                val wlanCellularButtonConditions = ExpectedConditions.presenceOfElementLocated(By.xpath("//XCUIElementTypeButton[@name='WLAN & Cellular']"))
                RetryUtil.call(Callable {
                    try {
                        driverWait.until(wlanCellularButtonConditions).click()
                        return@Callable driverWait.until(wlanCellularButtonConditions).isDisplayed
                    } catch (e: Exception) {
                        return@Callable false
                    }
                }, Predicate.isEqual(true))
                if (hermesAppPath.contains(".zip")) {
                    hermesPort = 8080
                } else {
                    shellExec.executeCmd("iproxy -u $udid -s 0.0.0.0 $hermesPort:8080 &")
                    RetryUtil.call(Callable {
                        return@Callable StringUtils.isBlank(shellExec.executeCmd("lsof -i:$hermesPort | grep iproxy | awk '{print $2}'"))
                    }, Predicate.isEqual<Boolean>(true))
                    LOG.info("iproxy $hermesPort 8080 $udid pid is: " + shellExec.executeCmd("lsof -i:$hermesPort | grep iproxy | awk '{print $2}'"))
                }
            } else {
                val shellExec = ShellFactory.getShellExec("127.0.0.1")
                if (hostName != "127.0.0.1") {
                    val adbPort = DevicePoolApiClient(hostName).getAdbPort(udid)
                    udid = "$hostName:$adbPort"
                    hermesPort = adbPort.toInt() + 1000
                    hostName = "127.0.0.1"
                }
                val adbPath = EnvUtil.getAdbPath()
                RetryUtil.call(Callable {
                    shellExec.executeCmd("${adbPath}adb connect $udid")
                    return@Callable shellExec.executeCmd("${adbPath}adb devices").contains(udid)
                }, Predicate.isEqual<Boolean>(false), 10, HermesException("${adbPath}adb connect $udid failed"))
                RetryUtil.call(Callable {
                    val cmd = "lsof -i:$hermesPort | grep adb | awk '{print $2}'"
                    val pid = shellExec.executeCmd(cmd)
                    if (StringUtils.isNotBlank(pid)) {
                        shellExec.executeCmd("${adbPath}adb -s $udid forward --remove tcp:$hermesPort")
                    }
                    return@Callable StringUtils.isBlank(shellExec.executeCmd(cmd))
                }, Predicate.isEqual(false))
                RetryUtil.call(Callable {
                    shellExec.executeCmd("${adbPath}adb -s $udid forward tcp:$hermesPort tcp:8080")
                    Thread.sleep(2000)
                    return@Callable StringUtils.isBlank(shellExec.executeCmd("lsof -i:$hermesPort | grep adb | awk '{print $2}'"))
                }, Predicate.isEqual<Boolean>(true), 15, HermesAndroidPortMappingException(shellExec, udid))
                LOG.info("${adbPath}adb -s $udid forward tcp:$hermesPort tcp:8080 pid is: " + shellExec.executeCmd("lsof -i:$hermesPort | grep adb | awk '{print $2}'"))
            }
            val hermesUrl = "http://$hostName:$hermesPort"
            //3. init singleton api clients
            contactApiClient = ContactApiClient(hermesUrl)
            calendarApiClient = CalendarApiClient(hermesUrl)
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