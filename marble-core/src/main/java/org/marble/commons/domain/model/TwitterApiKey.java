package org.marble.commons.domain.model;

import java.io.Serializable;
import java.math.BigInteger;

import javax.validation.constraints.NotNull;

import org.marble.util.BigIntegerSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Document(collection = "twitter_api_keys")
public class TwitterApiKey implements Serializable {

    private static final long serialVersionUID = -6137928572799267601L;

    @Id
    @JsonSerialize(using = BigIntegerSerializer.class)
    private BigInteger id;

    private String description;

    @NotNull
    private String consumerKey;

    @NotNull
    private String consumerSecret;

    @NotNull
    private String accessToken;

    @NotNull
    private String accessTokenSecret;

    @NotNull
    private Boolean enabled;

    public TwitterApiKey() {
        this.description = null;
        this.consumerKey = null;
        this.consumerSecret = null;
        this.accessToken = null;
        this.accessTokenSecret = null;
        this.setEnabled(Boolean.FALSE);
    }

    public TwitterApiKey(String description, String consumerKey, String consumerSecret, String accessToken,
            String accessTokenSecret) {
        this.description = description;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
        this.setEnabled(Boolean.FALSE);
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    public void setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String toString() {
        return ("Consumer Key: " + this.consumerKey + ", Consumer Secret: " + this.consumerSecret
                + ", Access Token: " + this.accessToken + ", Access Token Secret: " + this.accessTokenSecret);
    }
}

