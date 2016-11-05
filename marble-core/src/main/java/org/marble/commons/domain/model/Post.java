package org.marble.commons.domain.model;

import java.util.Date;

import org.marble.commons.model.SymplifiedProcessingItem;
import org.marble.commons.util.LongSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.Scopes;
import twitter4j.SymbolEntity;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

@Document(collection = "posts")
public class Post {

    @Indexed
    private String topicName;
    private Date createdAt;

    @Id
    @JsonSerialize(using = LongSerializer.class)
    private long id;
    private String text;
    private String source;
    private boolean isTruncated;
    private long inReplyToStatusId;
    private long inReplyToUserId;
    private boolean isFavorited;

    @Indexed
    private boolean isRetweeted;
    private int favoriteCount;
    private String inReplyToScreenName;
    private GeoLocation geoLocation = null;
    private Place place = null;
    private long retweetCount;
    private boolean isPossiblySensitive;
    private String lang;
    private long[] contributorsIDs;
    private Post retweetedStatus;
    private UserMentionEntity[] userMentionEntities;
    private URLEntity[] urlEntities;
    private HashtagEntity[] hashtagEntities;
    private MediaEntity[] mediaEntities;
    private MediaEntity[] extendedMediaEntities;
    private SymbolEntity[] symbolEntities;
    private long currentUserRetweetId = -1L;
    private Scopes scopes;
    private User user = null;
    private String[] withheldInCountries = null;

    public Post() {

    }

    public Post(Post status) {
        this.topicName = status.getTopicName();
        this.createdAt = status.getCreatedAt();
        this.id = status.getId();
        this.text = status.getText();
        this.source = status.getSource();
        this.isTruncated = status.isTruncated();
        this.inReplyToStatusId = status.getInReplyToStatusId();
        this.inReplyToUserId = status.getInReplyToUserId();
        this.isFavorited = status.isFavorited();
        this.isRetweeted = status.isRetweeted();
        this.favoriteCount = status.getFavoriteCount();
        this.inReplyToScreenName = status.getInReplyToScreenName();
        this.geoLocation = status.getGeoLocation();
        this.place = status.getPlace();
        this.retweetCount = status.getRetweetCount();
        this.isPossiblySensitive = status.isPossiblySensitive();
        this.lang = status.getLang();
        this.contributorsIDs = status.getContributorsIDs();
        this.retweetedStatus = status.getRetweetedStatus();
        this.userMentionEntities = status.getUserMentionEntities();
        this.urlEntities = status.getUrlEntities();
        this.hashtagEntities = status.getHashtagEntities();
        this.mediaEntities = status.getMediaEntities();
        this.extendedMediaEntities = status.getExtendedMediaEntities();
        this.symbolEntities = status.getSymbolEntities();
        this.currentUserRetweetId = status.getCurrentUserRetweetId();
        this.scopes = status.getScopes();
        this.user = status.getUser();
    }

    public Post(twitter4j.Status status, String topicName) {
        this.topicName = topicName;
        this.createdAt = status.getCreatedAt();
        this.id = status.getId();
        this.text = status.getText();
        this.source = status.getSource();
        this.isTruncated = status.isTruncated();
        this.inReplyToStatusId = status.getInReplyToStatusId();
        this.inReplyToUserId = status.getInReplyToUserId();
        this.isFavorited = status.isFavorited();
        this.isRetweeted = status.isRetweeted();
        this.favoriteCount = status.getFavoriteCount();
        this.inReplyToScreenName = status.getInReplyToScreenName();
        this.geoLocation = status.getGeoLocation();
        this.place = status.getPlace();
        this.retweetCount = status.getRetweetCount();
        this.isPossiblySensitive = status.isPossiblySensitive();
        this.lang = status.getLang();
        this.contributorsIDs = status.getContributors();
        if (status.getRetweetedStatus() != null) {
            this.retweetedStatus = new Post(status.getRetweetedStatus(), null);
        }
        this.userMentionEntities = status.getUserMentionEntities();
        this.urlEntities = status.getURLEntities();
        this.hashtagEntities = status.getHashtagEntities();
        this.mediaEntities = status.getMediaEntities();
        this.extendedMediaEntities = status.getExtendedMediaEntities();
        this.symbolEntities = status.getSymbolEntities();
        this.currentUserRetweetId = status.getCurrentUserRetweetId();
        this.scopes = status.getScopes();
        this.user = status.getUser();

        // TODO Fix this (it throws an exception when filled)
        this.mediaEntities = null;
        if (this.retweetedStatus != null) {
            this.retweetedStatus.mediaEntities = null;
        }
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicId(String topicName) {
        this.topicName = topicName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isTruncated() {
        return isTruncated;
    }

    public void setTruncated(boolean isTruncated) {
        this.isTruncated = isTruncated;
    }

    public long getInReplyToStatusId() {
        return inReplyToStatusId;
    }

    public void setInReplyToStatusId(long inReplyToStatusId) {
        this.inReplyToStatusId = inReplyToStatusId;
    }

    public long getInReplyToUserId() {
        return inReplyToUserId;
    }

    public void setInReplyToUserId(long inReplyToUserId) {
        this.inReplyToUserId = inReplyToUserId;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean isFavorited) {
        this.isFavorited = isFavorited;
    }

    public boolean isRetweeted() {
        return isRetweeted;
    }

    public void setRetweeted(boolean isRetweeted) {
        this.isRetweeted = isRetweeted;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public String getInReplyToScreenName() {
        return inReplyToScreenName;
    }

    public void setInReplyToScreenName(String inReplyToScreenName) {
        this.inReplyToScreenName = inReplyToScreenName;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public long getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(long retweetCount) {
        this.retweetCount = retweetCount;
    }

    public boolean isPossiblySensitive() {
        return isPossiblySensitive;
    }

    public void setPossiblySensitive(boolean isPossiblySensitive) {
        this.isPossiblySensitive = isPossiblySensitive;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public long[] getContributorsIDs() {
        return contributorsIDs;
    }

    public void setContributorsIDs(long[] contributorsIDs) {
        this.contributorsIDs = contributorsIDs;
    }

    public Post getRetweetedStatus() {
        return retweetedStatus;
    }

    public void setRetweetedStatus(Post retweetedStatus) {
        this.retweetedStatus = retweetedStatus;
    }

    public UserMentionEntity[] getUserMentionEntities() {
        return userMentionEntities;
    }

    public void setUserMentionEntities(UserMentionEntity[] userMentionEntities) {
        this.userMentionEntities = userMentionEntities;
    }

    public URLEntity[] getUrlEntities() {
        return urlEntities;
    }

    public void setUrlEntities(URLEntity[] urlEntities) {
        this.urlEntities = urlEntities;
    }

    public HashtagEntity[] getHashtagEntities() {
        return hashtagEntities;
    }

    public void setHashtagEntities(HashtagEntity[] hashtagEntities) {
        this.hashtagEntities = hashtagEntities;
    }

    public MediaEntity[] getMediaEntities() {
        return mediaEntities;
    }

    public void setMediaEntities(MediaEntity[] mediaEntities) {
        this.mediaEntities = mediaEntities;
    }

    public MediaEntity[] getExtendedMediaEntities() {
        return extendedMediaEntities;
    }

    public void setExtendedMediaEntities(MediaEntity[] extendedMediaEntities) {
        this.extendedMediaEntities = extendedMediaEntities;
    }

    public SymbolEntity[] getSymbolEntities() {
        return symbolEntities;
    }

    public void setSymbolEntities(SymbolEntity[] symbolEntities) {
        this.symbolEntities = symbolEntities;
    }

    public long getCurrentUserRetweetId() {
        return currentUserRetweetId;
    }

    public void setCurrentUserRetweetId(long currentUserRetweetId) {
        this.currentUserRetweetId = currentUserRetweetId;
    }

    public Scopes getScopes() {
        return scopes;
    }

    public void setScopes(Scopes scopes) {
        this.scopes = scopes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String[] getWithheldInCountries() {
        return withheldInCountries;
    }

    public void setWithheldInCountries(String[] withheldInCountries) {
        this.withheldInCountries = withheldInCountries;
    }
    
    public SymplifiedProcessingItem getSymplifiedProcessingItem() {
        SymplifiedProcessingItem symplifiedProcessingItem = new SymplifiedProcessingItem();
        symplifiedProcessingItem.setId(this.id);
        symplifiedProcessingItem.setCreatedAt(this.createdAt);
        symplifiedProcessingItem.setText(this.text);
        return symplifiedProcessingItem;
    }

}