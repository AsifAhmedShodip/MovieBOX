package com.example.asif.movies.Account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by asif on 30-Mar-18.
 */

public class Avatar {
    @SerializedName("gravatar")
    @Expose
    private Gravatar gravatar;

    public Gravatar getGravatar() {
        return gravatar;
    }

    public void setGravatar(Gravatar gravatar) {
        this.gravatar = gravatar;
    }
}
