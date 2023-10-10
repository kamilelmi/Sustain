package com.sustain.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SalonAPI
{

    //Stripe API
    @FormUrlEncoded
    @POST("/v1/payment_intents")
    Call<ResponseBody> createPaymentIntent(
            @Field("amount") int amount,
            @Field("currency") String currency,
            @Field("customer") String customerId
    );

    @FormUrlEncoded
    @POST("/v1/customers")
    Call<ResponseBody> createCustomer(
            @Field("name") String name
    );


    @FormUrlEncoded
    @POST("/v1/ephemeral_keys")
    Call<ResponseBody> createCustomerKey(
            @Field("customer") String customerId
    );



}
