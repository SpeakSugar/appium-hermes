package com.ringcentral.hermes.client;

import com.ringcentral.hermes.client.contact.iContactService;
import com.ringcentral.hermes.client.interceptor.HttpLogger;
import com.ringcentral.hermes.exception.HermesException;
import com.ringcentral.hermes.request.contact.ContactReq;
import com.ringcentral.hermes.response.ResponseBean;
import com.ringcentral.hermes.response.contact.ContactRsp;
import com.ringcentral.hermes.util.APIHelper;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ContactApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(ContactApiClient.class);

    private iContactService contactService;

    public ContactApiClient(String url) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor(new HttpLogger()).setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        contactService = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build().create(iContactService.class);
    }

    public ResponseBean testApi() {
        Call<ResponseBean> call = contactService.testApi();
        return APIHelper.callWithRetry(call).body();
    }

    public ResponseBean<List<ContactRsp>> findContact() {
        Call<ResponseBean<List<ContactRsp>>> call = contactService.findContact();
        ResponseBean<List<ContactRsp>> responseBean = APIHelper.callWithRetry(call).body();
        if (responseBean.getReturnCode() == 200) {
            return responseBean;
        } else {
            throw new HermesException(responseBean.getReturnMsg());
        }
    }

    public ResponseBean addContact(ContactReq contactReq) {
        Call<ResponseBean> call = contactService.addContact(contactReq);
        ResponseBean<List<ContactRsp>> responseBean = APIHelper.callWithRetry(call).body();
        if (responseBean.getReturnCode() == 200) {
            return responseBean;
        } else {
            throw new HermesException(responseBean.getReturnMsg());
        }
    }

    public ResponseBean deleteContact(String id) {
        Call<ResponseBean> call = contactService.deleteContact(id);
        ResponseBean<List<ContactRsp>> responseBean = APIHelper.callWithRetry(call).body();
        if (responseBean.getReturnCode() == 200) {
            return responseBean;
        } else {
            throw new HermesException(responseBean.getReturnMsg());
        }
    }
}
