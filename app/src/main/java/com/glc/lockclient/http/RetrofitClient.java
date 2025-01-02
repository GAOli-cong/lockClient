package com.glc.lockclient.http;


import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static final String BASE_URL = "http://192.168.137.1:8085/";

    private RetrofitClient() {
        // 私有构造函数，防止外部实例化
    }

    public static synchronized Retrofit getInstance() {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // 设置日志级别为 BODY，即包含请求和响应数据
            // 创建一个 OkHttpClient 客户端，并添加日志拦截器
            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
            httpClientBuilder.addInterceptor(interceptor);
            httpClientBuilder.addNetworkInterceptor(new TokenHeaderInterceptor());
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClientBuilder.build())
                    .build();
        }
        return retrofit;
    }
    public static ApiService getApiService() {
        return getInstance().create(ApiService.class);
    }
}
