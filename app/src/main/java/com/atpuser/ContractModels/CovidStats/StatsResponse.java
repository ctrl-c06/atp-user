package com.atpuser.ContractModels.CovidStats;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatsResponse {

    @SerializedName("cases")
    @Expose
    private Cases cases;
    @SerializedName("world")
    @Expose
    private World world;

    public Cases getCases() {
        return cases;
    }

    public void setCases(Cases cases) {
        this.cases = cases;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}

