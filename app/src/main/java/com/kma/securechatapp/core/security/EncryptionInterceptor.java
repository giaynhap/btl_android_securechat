package com.kma.securechatapp.core.security;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class EncryptionInterceptor implements Interceptor {
    private static final String TAG = EncryptionInterceptor.class.getSimpleName();

    static final MediaType audio = MediaType.parse("audio/aac");

    @Override
    public Response intercept(Chain chain) throws IOException {
        //Timber.i("===============ENCRYPTING REQUEST===============");
        Request request = chain.request();
        RequestBody rawBody = request.body();
        byte []data;

        final RequestBody copy = rawBody;
        final Buffer buffer = new Buffer();
        copy.writeTo(buffer);
        data = buffer.readByteArray();


        RequestBody body = RequestBody.create(rawBody.contentType(), data);

        //build new request
        request = request.
                newBuilder()
                .header("Content-Type", body.contentType().toString())
                .header("Content-Length", String.valueOf(body.contentLength()))
                .method(request.method(), body).build();

        return chain.proceed(request);
    }

}
