package com.ravi.tablayoutviewpage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("/photos")
    Call<List<ImageResponse>> getData();
}
