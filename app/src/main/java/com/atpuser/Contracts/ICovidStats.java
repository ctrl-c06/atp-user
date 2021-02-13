package com.atpuser.Contracts;

import com.atpuser.ContractModels.CovidStats.StatsResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ICovidStats {
    @GET("stats/quick")
    Call<StatsResponse> getResponse();
}
