package org.marble.model.domain.model;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.marble.model.model.GeoLocation;
import org.marble.model.model.Place;
import org.marble.model.model.Scopes;
import org.marble.model.model.User;
import org.marble.model.serializers.CustomDateAndTimeDeserializer;
import org.marble.util.LongSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Document(collection = "posts")
public class Post {

    @Indexed
    private String topicName;
    
    @JsonDeserialize(using = CustomDateAndTimeDeserializer.class)
    @Indexed
    private Date createdAt;

    @Id
    private String id;

    @JsonSerialize(using = LongSerializer.class)
    private long originalId;
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
    private long currentUserRetweetId = -1L;
    private Scopes scopes;
    private User user = null;
    private String[] withheldInCountries = null;

    @JsonIgnoreProperties(allowGetters = true)
    private Map<String, Integer> polarityTags = new LinkedHashMap<>();

    public Post() {

    }

    public Post(Post status) {
        this.topicName = status.getTopicName();
        this.id = status.getId();
        this.createdAt = status.getCreatedAt();
        this.originalId = status.getOriginalId();
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
        this.currentUserRetweetId = status.getCurrentUserRetweetId();
        this.scopes = status.getScopes();
        this.user = status.getUser();
    }

    public Post(twitter4j.Status status, String topicName) {
        this.composeId(status.getId(), topicName);  
        this.createdAt = status.getCreatedAt();
        this.text = status.getText();
        this.source = status.getSource();
        this.isTruncated = status.isTruncated();
        this.inReplyToStatusId = status.getInReplyToStatusId();
        this.inReplyToUserId = status.getInReplyToUserId();
        this.isFavorited = status.isFavorited();
        this.isRetweeted = status.isRetweeted();
        this.favoriteCount = status.getFavoriteCount();
        this.inReplyToScreenName = status.getInReplyToScreenName();
        this.geoLocation = new GeoLocation(status.getGeoLocation());
        this.place = new Place(status.getPlace());
        this.retweetCount = status.getRetweetCount();
        this.isPossiblySensitive = status.isPossiblySensitive();
        this.lang = status.getLang();
        this.contributorsIDs = status.getContributors();
        if (status.getRetweetedStatus() != null) {
            this.retweetedStatus = new Post(status.getRetweetedStatus(), null);
        }

        this.currentUserRetweetId = status.getCurrentUserRetweetId();
        this.scopes = new Scopes(status.getScopes());
        this.user = new User(status.getUser());

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
    
    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }
    
    public void composeId(long originalId, String topicName) {
      this.originalId = originalId;
      this.topicName = topicName;
      this.id = topicName + "_" + originalId;
    }

    public long getOriginalId() {
        return originalId;
    }

    public void setOriginalId(long originalId) {
        this.originalId = originalId;
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

    public Map<String, Integer> getPolarityTags() {
        return polarityTags;
    }

    public void setPolarityTags(Map<String, Integer> polarityTags) {
        this.polarityTags = polarityTags;
    }

    public void addPolarityTag(String user, Integer polarity) {
        this.polarityTags.put(user, polarity);
    }

    public void removePolarityTag(String user) {
        this.polarityTags.remove(user);
    }
}