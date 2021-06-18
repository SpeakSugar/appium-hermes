# Appium-hermes

> This project goal to create a app which can access android/ios native method and can be manipulated by your appium testing framework.

# Overview

![overview](overview.png)

> **Hermes-client** is a java project, use to send http request to **Hermes-app**
> 
> **Hermes-app** is a app install in android/ios device or simulator, it's a http server recevice http request from **Hermes-client**, and access native method according to request. This app build with cordova, you can refer https://cordova.apache.org/ for more information

# Current Support Native Method
✅ &nbsp; Native Contact Api

❎ &nbsp; Native Calendar Api

❎ &nbsp; Native Camera Api

❎ &nbsp; Native File Api

❎ &nbsp; Native Geolocation Api

❎ &nbsp; InAppBrower Api

❎ &nbsp; Others Native Api...

# Requirements
- **ios** `>= 9`
- **android** `>= 22`

# Usage
First, you should download **appium-hermes.apk**, **appium-hermes.ipa**, **appium-hermes.zip** from https://github.com/SpeakSugar/appium-hermes/releases

And you can intergrate **Hermes-client** in your java appium testing framework project, like below:
```xml
  <dependency>
      <groupId>com.github.SpeakSugar</groupId>
      <artifactId>appium-hermes</artifactId>
      <version>a7671b47da</version>
  </dependency>
```
```java
 AppiumDriver appiumDriver = createYourSelfAppiumDriver();
 String hermesAppPath = configHermesAppDownloadPath();
 HermesClientFactory.INSTANCE.setUp(appiumDriver, hermesAppPath);
 Thread.sleep(5000); // wait 5s to ensure Hermes-app is running
```

And use contact api like below:
```java
 ContactApiClient contactApiClient = HermesClientFactory.INSTANCE.getContactApiClient();
 
 //find contacts
 ResponseBean<List<ContactRsp>> responseBean = contactApiClient.findContact();
 
 //delete contact
 String id = responseBean.getContent().get(0).getId();
 contactApiClient.deleteContact(id);
 
 //add contact
 List<ContactReq.Email> emailList = new ArrayList<>();
 List<ContactReq.PhoneNumber> phoneNumberList = new ArrayList<>();
 ContactReq.Email email = new ContactReq.Email();
 email.setType("work");
 email.setValue("ABCabc@gmail.com");
 emailList.add(email);
 ContactReq.PhoneNumber phoneNumber = new ContactReq.PhoneNumber();
 phoneNumber.setType("work");
 phoneNumber.setValue("123456");
 phoneNumberList.add(phoneNumber);
 ContactReq contactReq = new ContactReq();
 contactReq.setFirstName("Jeffries");
 contactReq.setFamilyName("Yu");
 contactReq.setEmails(emailList);
 contactReq.setPhoneNumbers(phoneNumberList);
 contactApiClient.addContact(contactReq);
```
# License

See [LICENSE](LICENSE).
