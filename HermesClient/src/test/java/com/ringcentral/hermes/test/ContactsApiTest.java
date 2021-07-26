package com.ringcentral.hermes.test;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.ringcentral.hermes.client.HermesClientFactory;
import com.ringcentral.hermes.request.contact.ContactReq;
import com.ringcentral.hermes.response.ResponseBean;
import com.ringcentral.hermes.response.contact.ContactRsp;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ContactsApiTest {

    protected static AppiumDriver<?> driver;
    protected static final int PORT = 4723;

    public static AppiumDriver<?> createIOSDriver() {
        try {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "ios");
            capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "14");
            capabilities.setCapability(MobileCapabilityType.UDID, "b5583a038634ad52c57b7be22d7af246fdc30bfa");
//            capabilities.setCapability(MobileCapabilityType.APP, "/Users/jeffries.yu/Downloads/BrandApp/WEB-AQA-XMN-Glip.zip");
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "1");
            capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.IOS_XCUI_TEST);
            capabilities.setCapability(IOSMobileCapabilityType.LAUNCH_TIMEOUT, 500000);
            capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 600000);
            capabilities.setCapability(IOSMobileCapabilityType.WDA_LOCAL_PORT, 9992);
            capabilities.setCapability("autoLaunch", false);
            return new IOSDriver<>(new URL("http://" + "127.0.0.1" + ":" + PORT + "/wd/hub"), capabilities);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static AppiumDriver<?> createAndroidDriver() {
        try {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "android");
            capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "14");
            capabilities.setCapability(MobileCapabilityType.UDID, "D0AA003065JB1902461");
//            capabilities.setCapability(MobileCapabilityType.APP, "/Users/jeffries.yu/Downloads/BrandApp/WEB-AQA-XMN-Glip.zip");
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "1");
            capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.ANDROID_UIAUTOMATOR2);
            capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 600000);
            capabilities.setCapability("systemPort", 9991);
            capabilities.setCapability("autoLaunch", false);
            return new IOSDriver<>(new URL("http://" + "127.0.0.1" + ":" + PORT + "/wd/hub"), capabilities);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void androidFindContactTest() {
        driver = createAndroidDriver();
        System.out.println(driver.getSessionId());
        HermesClientFactory.INSTANCE.setUp(driver, "/Users/jeffries.yu/IdeaProjects/appium-hermes/HermesApp/platforms/android/app/build/outputs/apk/debug/app-debug.apk");
        ResponseBean<List<ContactRsp>> responseBean = HermesClientFactory.INSTANCE.getContactApiClient().findContact();
        System.out.println(new Gson().toJson(responseBean.getContent()));
        driver.quit();
    }

    @Test
    public void iosFindContactTest() {
        driver = createIOSDriver();
        System.out.println(driver.getSessionId());
        HermesClientFactory.INSTANCE.setUp(driver, "/Users/jeffries.yu/IdeaProjects/appium-hermes/HermesApp/platforms/android/app/build/outputs/apk/debug/Hermes.zip");
        ResponseBean<List<ContactRsp>> responseBean = HermesClientFactory.INSTANCE.getContactApiClient().findContact();
        System.out.println(new Gson().toJson(responseBean.getContent()));
        driver.quit();
    }

    @Test
    public void iosAddContact() throws IOException {
        driver = createIOSDriver();
        System.out.println(driver.getSessionId());
        HermesClientFactory.INSTANCE.setUp(driver, "/Users/jeffries.yu/IdeaProjects/appium-hermes/HermesApp/platforms/android/app/build/outputs/apk/debug/Hermes.ipa");
        ContactReq contactReq = new ContactReq();
        contactReq.setFirstName("Jeffries");
        contactReq.setFamilyName("Yu");
        ContactReq.Email email = new ContactReq.Email();
        email.setType("work");
        email.setValue("296995537@qq.com");
        contactReq.setEmails(Lists.newArrayList(email));

        // Add avatar
        InputStream inputStream = this.getClass().getResourceAsStream("/Avatar.jpg");
        byte[] bytes = IOUtils.toByteArray(inputStream);
        String s = Base64.getEncoder().encodeToString(bytes);
        contactReq.setAvatar(s);

        ResponseBean responseBean = HermesClientFactory.INSTANCE.getContactApiClient().addContact(contactReq);
        System.out.println(new Gson().toJson(responseBean.getReturnMsg()));
        driver.quit();
    }

    @Test
    public void androidAddContact() throws IOException {
        driver = createAndroidDriver();
        System.out.println(driver.getSessionId());
        HermesClientFactory.INSTANCE.setUp(driver, "/Users/jeffries.yu/IdeaProjects/appium-hermes/HermesApp/platforms/android/app/build/outputs/apk/debug/app-debug.apk");
        ContactReq contactReq = new ContactReq();
        contactReq.setFirstName("Jeffries");
        contactReq.setFamilyName("Yu");
        ContactReq.Email email = new ContactReq.Email();
        email.setType("work");
        email.setValue("296995537@qq.com");
        contactReq.setEmails(Lists.newArrayList(email));

        // Add avatar
        InputStream inputStream = this.getClass().getResourceAsStream("/Avatar.jpg");
        byte[] bytes = IOUtils.toByteArray(inputStream);
        String s = Base64.getEncoder().encodeToString(bytes);
        contactReq.setAvatar(s);


        ResponseBean responseBean = HermesClientFactory.INSTANCE.getContactApiClient().addContact(contactReq);
        System.out.println(new Gson().toJson(responseBean.getReturnMsg()));
        driver.quit();
    }

    @Test
    public void androidDeleteContactTest() {
        driver = createAndroidDriver();
        System.out.println(driver.getSessionId());
        HermesClientFactory.INSTANCE.setUp(driver, "/Users/jeffries.yu/IdeaProjects/appium-hermes/HermesApp/platforms/android/app/build/outputs/apk/debug/app-debug.apk");
        ResponseBean responseBean = HermesClientFactory.INSTANCE.getContactApiClient().deleteContact("3354");
        System.out.println(responseBean.getReturnMsg());
        driver.quit();
    }

    @Test
    public void imgToStringTest() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("Avatar.jpg");
        byte[] bytes = IOUtils.toByteArray(inputStream);
        String s = Base64.getEncoder().encodeToString(bytes);
        File file = new File("AvatarCopy.jpg");
        FileUtils.writeByteArrayToFile(file, Base64.getDecoder().decode(s));
    }

}
