package com.example.asif.movies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by asif on 01-Apr-18.
 */

public class CheckItemStatus {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("item_present")
    @Expose
    private Boolean itemPresent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getItemPresent() {
        return itemPresent;
    }

    public void setItemPresent(Boolean zitemPresent) {
        this.itemPresent = itemPresent;
    }
}
