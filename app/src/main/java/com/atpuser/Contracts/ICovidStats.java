package com.atpuser.Contracts;

import com.atpuser.ContractModels.CovidStats.StatsResponse;
import com.atpuser.ContractModels.CovidStats.SurigaoSurResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ICovidStats {
    @GET("stats/quick")
    Call<StatsResponse> getResponse();

    @GET("/api/surigao/sur/covid/stat")
    Call<SurigaoSurResponse> getStat();
}
