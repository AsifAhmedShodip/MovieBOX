package com.example.asif.movies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by asif on 01-Apr-18.
 */

public class MediaID {
    @SerializedName("media_id")
    @Expose
    private Integer mediaId;

    public Integer getMediaId() {
        return mediaId;
    }

    public void setMediaId(Integer mediaId) {
        this.mediaId = mediaId;
    }
}
