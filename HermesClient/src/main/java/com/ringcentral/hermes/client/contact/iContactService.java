package com.ringcentral.hermes.client.contact;

import com.ringcentral.hermes.request.contact.ContactReq;
import com.ringcentral.hermes.response.ResponseBean;
import com.ringcentral.hermes.response.contact.ContactRsp;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;

public interface iContactService {

    @GET("/testApi")
    Call<ResponseBean> testApi();

    @POST("/addContact")
    Call<ResponseBean> addContact(@Body ContactReq contactReq);

    @GET("/findContact")
    Call<ResponseBean<List<ContactRsp>>> findContact();

    @POST("/deleteContact")
    Call<ResponseBean> deleteContact(@Body String id);

}
