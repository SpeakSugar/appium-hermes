package com.ringcentral.hermes.test;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.ringcentral.hermes.client.ContactApiClient;
import com.ringcentral.hermes.factory.HermesClientFactory;
import com.ringcentral.hermes.request.contact.ContactReq;
import com.ringcentral.hermes.response.ResponseBean;
import com.ringcentral.hermes.response.contact.ContactRsp;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.omg.PortableInterceptor.Interceptor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class ContactsApiTest {

    protected static AppiumDriver<?> driver;
    protected static final int PORT = 4723;

    public static AppiumDriver<?> createIOSDriver() {
        try {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "ios");
            capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "15");
            capabilities.setCapability(MobileCapabilityType.UDID, "CD4E977B-C1C0-4249-8689-9F52C345AFE3");
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
            capabilities.setCapability(MobileCapabilityType.UDID, "emulator-5554");
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
        HermesClientFactory hcf = new HermesClientFactory();
        System.out.println(driver.getSessionId());
        hcf.setUp(driver, "/Users/jeffries.yu/IdeaProjects/appium-hermes/HermesApp/platforms/android/app/build/outputs/apk/debug/app-debug.apk");
        ResponseBean<List<ContactRsp>> responseBean = hcf.getContactApiClient().findContact();
        System.out.println(new Gson().toJson(responseBean.getContent()));
        driver.quit();
    }

    @Test
    public void iosFindContactTest() {
        driver = createIOSDriver();
        HermesClientFactory hcf = new HermesClientFactory();
        System.out.println(driver.getSessionId());
        hcf.setUp(driver, "http://mThor_cloud:NextCloud123@cloud-xmn.lab.nordigy.ru/remote.php/webdav/mThor/apps/common/appium-hermes.zip");
        ResponseBean<List<ContactRsp>> responseBean = hcf.getContactApiClient().findContact();
        System.out.println(new Gson().toJson(responseBean.getContent()));
        driver.quit();
    }

    @Test
    public void iosAddContact() throws IOException {
        driver = createIOSDriver();
        HermesClientFactory hcf = new HermesClientFactory();
        System.out.println(driver.getSessionId());
        hcf.setUp(driver, "/Users/jeffries.yu/IdeaProjects/appium-hermes/HermesApp/platforms/android/app/build/outputs/apk/debug/appium-hermes.zip");
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

        ResponseBean responseBean = hcf.getContactApiClient().addContact(contactReq);
        System.out.println(new Gson().toJson(responseBean.getReturnMsg()));
        driver.quit();
    }

    @Test
    public void androidAddContact() throws IOException {
        driver = createAndroidDriver();
        HermesClientFactory hcf = new HermesClientFactory();
        System.out.println(driver.getSessionId());
        hcf.setUp(driver, "/Users/jeffries.yu/IdeaProjects/appium-hermes/HermesApp/platforms/android/app/build/outputs/apk/debug/app-debug.apk");
        ContactReq contactReq = new ContactReq();
        contactReq.setFirstName("Jeffries");
        contactReq.setFamilyName("Yu");
        ContactReq.Email email = new ContactReq.Email();
        email.setType("home");
        email.setValue("296995538@qq.com");
        ContactReq.Email email2 = new ContactReq.Email();
        email2.setType("home");
        email2.setValue("296995536@qq.com");
        contactReq.setEmails(Lists.newArrayList(email, email2));

        // Add avatar
        InputStream inputStream = this.getClass().getResourceAsStream("/Avatar.jpg");
        byte[] bytes = IOUtils.toByteArray(inputStream);
        String s = Base64.getEncoder().encodeToString(bytes);
        contactReq.setAvatar(s);

        ResponseBean responseBean = hcf.getContactApiClient().addContact(contactReq);
        System.out.println(new Gson().toJson(responseBean.getReturnMsg()));
        driver.quit();
    }

    @Test
    public void androidUpdateContact() {
        driver = createAndroidDriver();
        HermesClientFactory hcf = new HermesClientFactory();
        System.out.println(driver.getSessionId());
        hcf.setUp(driver, "/Users/jeffries.yu/IdeaProjects/appium-hermes/HermesApp/platforms/android/app/build/outputs/apk/debug/app-debug.apk");
        ContactReq contactReq = new ContactReq();
        contactReq.setId("1");
        contactReq.setRawId("1");
        ContactReq.Email email = new ContactReq.Email();
        email.setType("work");
        email.setValue("fuck@qq.com");
        contactReq.setEmails(Lists.newArrayList(email));
        ResponseBean responseBean = hcf.getContactApiClient().addContact(contactReq);
        System.out.println(new Gson().toJson(responseBean.getReturnMsg()));
        driver.quit();
    }

    @Test
    public void androidDeleteContactTest() {
        driver = createAndroidDriver();
        HermesClientFactory hcf = new HermesClientFactory();
        System.out.println(driver.getSessionId());
        hcf.setUp(driver, "/Users/jeffries.yu/IdeaProjects/appium-hermes/HermesApp/platforms/android/app/build/outputs/apk/debug/app-debug.apk");
        ResponseBean responseBean = hcf.getContactApiClient().deleteContact("3354");
        System.out.println(responseBean.getReturnMsg());
        driver.quit();
    }

    @Test
    public void imgToStringTest() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("Avatar.jpg");
        byte[] bytes = IOUtils.toByteArray(inputStream);
        String s = Base64.getEncoder().encodeToString(bytes);
        System.out.println(s);
        File file = new File("AvatarCopy.jpg");
        FileUtils.writeByteArrayToFile(file, Base64.getDecoder().decode(s));
    }

    @Test
    public void stringToImg() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("avatar.txt");
        String s = IOUtils.toString(inputStream);
        File file = new File("AvatarCopy.jpg");
        FileUtils.writeByteArrayToFile(file, Base64.getDecoder().decode(s));
    }

    @Test
    public void testStartAndroidAppTime() {
        for (int i = 0; i < 10; i++) {
            driver = createAndroidDriver();
            driver.removeApp("com.glip.mobile.qa");
            driver.installApp("/Users/jeffries.yu/Downloads/ringcentral-21.4.10.003-xmn-up-inhouse-release.apk");
            long startTimeMillis = System.currentTimeMillis();
            driver.activateApp("com.glip.mobile.qa");
            WebDriverWait driverWait = new WebDriverWait(driver, 10, 20);
            ExpectedCondition<WebElement> expectedCondition = ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@text='Sign in' or  @text='Switch accounts']"));
            driverWait.until(expectedCondition);
            System.out.println(System.currentTimeMillis() - startTimeMillis + " ms");
            driver.quit();
        }
    }

    @Test
    public void testStartiOSAppTime() {
        for (int i = 0; i < 10; i++) {
            driver = createIOSDriver();
            driver.removeApp("com.glip.mobile.rc");
            driver.installApp("/Users/jeffries.yu/Downloads/BrandApp/WEB-AQA-XMN-Glip-21.3.20.zip");
            long startTimeMillis = System.currentTimeMillis();
            driver.activateApp("com.glip.mobile.rc");
            WebDriverWait driverWait = new WebDriverWait(driver, 10, 20);
            ExpectedCondition<WebElement> expectedCondition = ExpectedConditions.presenceOfElementLocated(MobileBy.iOSNsPredicateString("name == 'signIn' || name== 'SignInButton'"));
            driverWait.until(expectedCondition);
            System.out.println(System.currentTimeMillis() - startTimeMillis + " ms");
            driver.quit();
        }
    }

    @Test
    public void testPageSource() throws Exception {
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
        new ContactApiClient("http://127.0.0.1:9723").addContact(contactReq);
    }

    @Test
    public void configureNumber() {
        List<Integer> total = new ArrayList<Integer>();
        total.add(2910);
        total.add(3468);
        total.add(3479);
        total.add(3408);
        total.add(3406);
        total.add(3431);
        total.add(2971);
        total.add(2944);
        total.add(2913);
        total.add(2999);
        System.out.println("平均数为: " + average(total));
        System.out.println("中位数为: " + median(total));
        System.out.println("最大值为: " + Collections.max(total));
        System.out.println("最小值为: " + Collections.min(total));
    }

    private static double average(List<Integer> total) {
        int count = 0;
        for (Integer i : total) {
            count = count + i;
        }
        return (count + 0.0) / total.size();
    }

    private static double median(List<Integer> total) {
        double j = 0;
        //集合排序
        Collections.sort(total);
        int size = total.size();
        if (size % 2 == 1) {
            j = total.get((size - 1) / 2);
        } else {
            //加0.0是为了把int转成double类型，否则除以2会算错
            j = (total.get(size / 2 - 1) + total.get(size / 2) + 0.0) / 2;
        }
        return j;
    }
}
