package com.hitherejoe.hackernews.data.remote;

import com.google.gson.GsonBuilder;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class RetrofitHelper {

    public HackerNewsService setupHackerNewsService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(HackerNewsService.ENDPOINT)
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .setConverter(new GsonConverter(new GsonBuilder().create()))
                .build();
        return restAdapter.create(HackerNewsService.class);
    }

}
