package com.ringcentral.hermes.util;

import com.ringcentral.hermes.exception.HermesException;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.appmanagement.AndroidInstallApplicationOptions;
import io.appium.java_client.appmanagement.ApplicationState;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class DriverUtil {

    public static Logger LOG = LoggerFactory.getLogger(DriverUtil.class);

    public static void clickUntilDisappear(AppiumDriver<?> driver, By by) {
        WebDriverWait driverWait = new WebDriverWait(driver, 5);
        ExpectedCondition<WebElement> condition = ExpectedConditions.presenceOfElementLocated(by);
        RetryUtil.call(() -> {
            try {
                driverWait.until(condition).click();
                return driverWait.until(condition).isDisplayed();
            } catch (Exception e) {
                return false;
            }
        }, Predicate.isEqual(true));
    }

    public static void launch(AppiumDriver<?> driver, String bundleId) {
        RetryUtil.call(() -> {
            driver.activateApp(bundleId);
            Thread.sleep(2000);
            return driver.queryAppState(bundleId) == ApplicationState.RUNNING_IN_FOREGROUND;
        }, Predicate.isEqual(false), 10, new HermesException(bundleId + " activate failed."));
        LOG.info(bundleId + " activate success.");
    }

    public static void updateAndLaunch(AppiumDriver<?> driver, String bundleId, String hermesAppPath) {
        if (driver.isAppInstalled(bundleId)) {
            LOG.info("start to delete " + bundleId + "...");
            driver.removeApp(bundleId);
            LOG.info("delete " + bundleId + " success.");
        }
        LOG.info("start to install " + bundleId + "...");
        RetryUtil.call(() -> {
            driver.installApp(hermesAppPath, new AndroidInstallApplicationOptions().withGrantPermissionsEnabled());
            return true;
        }, Predicate.isEqual(false));
        LOG.info(bundleId + " install success.");
        RetryUtil.call(() -> {
            driver.activateApp(bundleId);
            Thread.sleep(2000);
            return driver.queryAppState(bundleId) == ApplicationState.RUNNING_IN_FOREGROUND;
        }, Predicate.isEqual(false), 10, new HermesException(bundleId + " activate failed."));
        LOG.info(bundleId + " activate success.");
    }
}
