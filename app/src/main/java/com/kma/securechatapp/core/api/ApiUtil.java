package com.kma.securechatapp.core.api;

import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.BuildConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
 
public class ApiUtil {
    private static Retrofit retrofit = null;

    public static ApiInterface getChatApi(){
        return getClient().create(ApiInterface.class);
    }
    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request.Builder requestBuilder =  chain.request().newBuilder();
                if (AppData.getInstance().getToken() != null){
                    requestBuilder.addHeader("Authorization","Bearer "+ AppData.getInstance().getToken());
                }
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        OkHttpClient client =httpClientBuilder.addInterceptor(interceptor)
                .readTimeout(60,TimeUnit.SECONDS)
                .connectTimeout(60,TimeUnit.SECONDS)
                .build();


        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();



        return retrofit;
    }
}
