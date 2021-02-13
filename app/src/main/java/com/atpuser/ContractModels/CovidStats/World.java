package com.atpuser.ContractModels.CovidStats;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class World {

    @SerializedName("total")
    @Expose
    private Long total;
    @SerializedName("deaths")
    @Expose
    private Long deaths;
    @SerializedName("recovered")
    @Expose
    private Long recovered;
    @SerializedName("id")
    @Expose
    private Double id;


    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getDeaths() {
        return deaths;
    }

    public void setDeaths(Long deaths) {
        this.deaths = deaths;
    }

    public Long getRecovered() {
        return recovered;
    }

    public void setRecovered(Long recovered) {
        this.recovered = recovered;
    }


}