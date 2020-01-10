package com.kma.securechatapp.core.api;

import com.kma.securechatapp.core.AppData;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UnsafeOkHttpClient {
    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            builder.addInterceptor(new Interceptor() {
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
            builder.readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60,TimeUnit.SECONDS)
                    .build();

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}