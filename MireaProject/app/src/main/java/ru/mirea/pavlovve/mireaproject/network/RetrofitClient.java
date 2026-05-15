package ru.mirea.pavlovve.mireaproject.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitClient {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    private static JsonPlaceholderApi api;

    private RetrofitClient() {
    }

    public static JsonPlaceholderApi getApi() {
        if (api == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            api = retrofit.create(JsonPlaceholderApi.class);
        }
        return api;
    }
}
