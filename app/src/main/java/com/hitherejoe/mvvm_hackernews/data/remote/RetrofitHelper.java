package com.hitherejoe.mvvm_hackernews.data.remote;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public class RetrofitHelper {

    private HttpLoggingInterceptor httpLoggingInterceptor = null;

    private OkHttpClient okHttpClient = null;

    private OkHttpClient setUpClient(){
        if (httpLoggingInterceptor == null) {
            httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();
        }

        return okHttpClient;
    }

    public HackerNewsService newHackerNewsService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HackerNewsService.ENDPOINT)
                .client(setUpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
        return retrofit.create(HackerNewsService.class);
    }

}
