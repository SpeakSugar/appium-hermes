package com.ringcentral.hermes.client

import com.ringcentral.hermes.util.ReflectUtil
import com.ringcentral.hermes.util.RetryUtil
import com.ringcentral.hermes.util.ShellUtil
import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.appmanagement.AndroidInstallApplicationOptions
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

object HermesClientFactory {

    private val LOG: Logger = LoggerFactory.getLogger(HermesClientFactory::class.java)

    lateinit var contactApiClient: ContactApiClient

    lateinit var calendarApiClient: CalendarApiClient

    fun setUp(driver: AppiumDriver<*>, hermesAppPath: String) {
        //1. install hermes app, and launch it
        if (driver.isAppInstalled("org.ringcentral.hermes")) {
            LOG.info("start to delete hermes app...")
            driver.removeApp("org.ringcentral.hermes")
            LOG.info("delete hermes app success.")
        }
        LOG.info("start to install hermes app...")
        driver.installApp(hermesAppPath, AndroidInstallApplicationOptions().withGrantPermissionsEnabled())
        LOG.info("hermes app install success.")
        driver.activateApp("org.ringcentral.hermes")
        LOG.info("hermes app activate success.")
        //2. port mapping, ios permission grant
        val appiumUrl = ReflectUtil.getValueFromParentClass(driver, "io.appium.java_client.AppiumDriver", "remoteAddress") as URL
        val capabilities = ReflectUtil.getValueFromParentClass(driver, "org.openqa.selenium.remote.RemoteWebDriver", "capabilities") as Capabilities
        val udid = capabilities.getCapability("udid")
        val platformName = driver.platformName
        val hermesPort = appiumUrl.port + 5000
        val hostName = appiumUrl.host
        var hermesUrl = "http://$hostName:$hermesPort"
        val shellExec = ShellFactory.getShellExec(hostName)
        RetryUtil.call(Callable {
            val cmd = "lsof -i:$hermesPort | awk '{print $2}' | sed -n '2p'"
            val pid = shellExec.executeCmd(cmd)
            if (StringUtils.isNotBlank(pid)) {
                shellExec.executeCmd("kill -9 $pid")
            }
            return@Callable StringUtils.isBlank(shellExec.executeCmd(cmd))
        }, Predicate.isEqual(false))
        if (platformName == "ios") {
            val driverWait = WebDriverWait(driver, 5)
            val expectedConditions = ExpectedConditions.presenceOfElementLocated(By.xpath("//XCUIElementTypeButton[@name='OK']"))
            RetryUtil.call(Callable {
                try {
                    driverWait.until(expectedConditions).click()
                    return@Callable driverWait.until(expectedConditions).isDisplayed
                } catch (e: Exception) {
                    return@Callable false
                }
            }, Predicate.isEqual(true))
            if (hermesAppPath.contains(".zip")) {
                hermesUrl = "http://$hostName:8080"
            } else {
                shellExec.executeCmd("iproxy -u $udid -s 0.0.0.0 $hermesPort:8080 &")
                RetryUtil.call(Callable {
                    return@Callable StringUtils.isBlank(shellExec.executeCmd("lsof -i:$hermesPort | awk '{print $2}' | sed -n '2p'"))
                }, Predicate.isEqual<Boolean>(true))
                LOG.info("iproxy $hermesPort 8080 $udid pid is: " + shellExec.executeCmd("lsof -i:$hermesPort | awk '{print $2}' | sed -n '2p'"))
            }
        } else {
            val cmd = "lsof -i:5037 | awk '{print $2}' | sed -n '2p'"
            val pid = shellExec.executeCmd(cmd)
            if (StringUtils.isNotBlank(pid)) {
                shellExec.executeCmd("kill -9 $pid")
            }
            shellExec.executeCmd("adb -a nodaemon server &")
            shellExec.executeCmd("adb -s $udid forward tcp:$hermesPort tcp:8080")
            RetryUtil.call(Callable {
                return@Callable StringUtils.isBlank(shellExec.executeCmd("lsof -i:$hermesPort | awk '{print $2}' | sed -n '2p'"))
            }, Predicate.isEqual<Boolean>(true))
            LOG.info("adb -s $udid forward tcp:$hermesPort tcp:8080 pid is: " + shellExec.executeCmd("lsof -i:$hermesPort | awk '{print $2}' | sed -n '2p'"))
        }
        //3. init singleton api clients
        contactApiClient = ContactApiClient(hermesUrl)
        calendarApiClient = CalendarApiClient(hermesUrl)
    }

}