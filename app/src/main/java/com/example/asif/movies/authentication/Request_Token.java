package com.example.asif.movies.authentication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by asif on 30-Mar-18.
 */

public class Request_Token {

        @SerializedName("success")
        @Expose
        private Boolean success;
        @SerializedName("expires_at")
        @Expose
        private String expiresAt;
        @SerializedName("request_token")
        @Expose
        private String requestToken;

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public String getExpiresAt() {
            return expiresAt;
        }

        public void setExpiresAt(String expiresAt) {
            this.expiresAt = expiresAt;
        }

        public String getRequestToken() {
            return requestToken;
        }

        public void setRequestToken(String requestToken) {
            this.requestToken = requestToken;
        }
}
