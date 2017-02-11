package org.marble.model.model;

import java.util.Date;

public class User {
    private long id;
    private String name;
    private String screenName;
    private String location;
    private String description;
    private boolean contributorsEnabled;
    private String profileImageURL;
    private String biggerProfileImageURL;
    private String miniProfileImageURL;
    private String originalProfileImageURL;
    private String profileImageURLHttps;
    private String biggerProfileImageURLHttps;
    private String miniProfileImageURLHttps;
    private String originalProfileImageURLHttps;
    private boolean defaultProfileImage;
    private String url;
    private boolean isProtected;
    private int followersCount;
    private String profileBackgroundColor;
    private String profileTextColor;
    private String profileLinkColor;
    private String profileSidebarFillColor;
    private String profileSidebarBorderColor;
    private boolean profileUseBackgroundImage;
    private boolean defaultProfile;
    private boolean showAllInlineMedia;
    private int friendsCount;
    private Date createdAt;
    private int favouritesCount;
    private int utcOffset;
    private String timeZone;
    private String profileBackgroundImageURL;
    private String profileBackgroundImageUrlHttps;
    private String profileBannerURL;
    private String profileBannerRetinaURL;
    private String profileBannerIPadURL;
    private String profileBannerIPadRetinaURL;
    private String profileBannerMobileURL;
    private String profileBannerMobileRetinaURL;
    private boolean profileBackgroundTiled;
    private String lang;
    private int statusesCount;
    private boolean isGeoEnabled;
    private boolean isVerified;
    private boolean isTranslator;
    private int listedCount;
    private boolean isFollowRequestSent;
    private String[] withheldInCountries;
    

    public User() {
    
    }
    
    public User(twitter4j.User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.screenName = user.getScreenName();
        this.location = user.getLocation();
        this.description = user.getDescription();
        this.contributorsEnabled = user.isContributorsEnabled();
        this.profileImageURL = user.getProfileImageURL();
        this.biggerProfileImageURL = user.getBiggerProfileImageURL();
        this.miniProfileImageURL = user.getMiniProfileImageURL();
        this.originalProfileImageURL = user.getOriginalProfileImageURL();
        this.profileImageURLHttps = user.getProfileImageURLHttps();
        this.biggerProfileImageURLHttps = user.getBiggerProfileImageURLHttps();
        this.miniProfileImageURLHttps = user.getMiniProfileImageURLHttps();
        this.originalProfileImageURLHttps = user.getOriginalProfileImageURLHttps();
        this.defaultProfileImage = user.isDefaultProfileImage();
        this.url = user.getURL();
        this.isProtected = user.isProtected();
        this.followersCount = user.getFollowersCount();
        this.profileBackgroundColor = user.getProfileBackgroundColor();
        this.profileTextColor = user.getProfileTextColor();
        this.profileLinkColor = user.getProfileLinkColor();
        this.profileSidebarFillColor = user.getProfileSidebarFillColor();
        this.profileSidebarBorderColor = user.getProfileSidebarBorderColor();
        this.profileUseBackgroundImage = user.isProfileUseBackgroundImage();
        this.defaultProfile = user.isDefaultProfile();
        this.showAllInlineMedia = user.isShowAllInlineMedia();
        this.friendsCount = user.getFriendsCount();
        this.createdAt = user.getCreatedAt();
        this.favouritesCount = user.getFavouritesCount();
        this.utcOffset = user.getUtcOffset();
        this.timeZone = user.getTimeZone();
        this.profileBackgroundImageURL = user.getProfileBackgroundImageURL();
        this.profileBackgroundImageUrlHttps = user.getProfileBackgroundImageUrlHttps();
        this.profileBannerURL = user.getProfileBannerURL();
        this.profileBannerRetinaURL = user.getProfileBannerRetinaURL();
        this.profileBannerIPadURL = user.getProfileBannerIPadURL();
        this.profileBannerIPadRetinaURL = user.getProfileBannerIPadRetinaURL();
        this.profileBannerMobileURL = user.getProfileBannerMobileURL();
        this.profileBannerMobileRetinaURL = user.getProfileBannerMobileRetinaURL();
        this.profileBackgroundTiled = user.isProfileBackgroundTiled();
        this.lang = user.getLang();
        this.statusesCount = user.getStatusesCount();
        this.isGeoEnabled = user.isGeoEnabled();
        this.isVerified = user.isVerified();
        this.isTranslator = user.isTranslator();
        this.listedCount = user.getListedCount();
        this.isFollowRequestSent = user.isFollowRequestSent();
        this.withheldInCountries = user.getWithheldInCountries();
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getScreenName() {
        return screenName;
    }
    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public boolean isContributorsEnabled() {
        return contributorsEnabled;
    }
    public void setContributorsEnabled(boolean isContributorsEnabled) {
        this.contributorsEnabled = isContributorsEnabled;
    }
    public String getProfileImageURL() {
        return profileImageURL;
    }
    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }
    public String getBiggerProfileImageURL() {
        return biggerProfileImageURL;
    }
    public void setBiggerProfileImageURL(String biggerProfileImageURL) {
        this.biggerProfileImageURL = biggerProfileImageURL;
    }
    public String getMiniProfileImageURL() {
        return miniProfileImageURL;
    }
    public void setMiniProfileImageURL(String miniProfileImageURL) {
        this.miniProfileImageURL = miniProfileImageURL;
    }
    public String getOriginalProfileImageURL() {
        return originalProfileImageURL;
    }
    public void setOriginalProfileImageURL(String originalProfileImageURL) {
        this.originalProfileImageURL = originalProfileImageURL;
    }
    public String getProfileImageURLHttps() {
        return profileImageURLHttps;
    }
    public void setProfileImageURLHttps(String profileImageURLHttps) {
        this.profileImageURLHttps = profileImageURLHttps;
    }
    public String getBiggerProfileImageURLHttps() {
        return biggerProfileImageURLHttps;
    }
    public void setBiggerProfileImageURLHttps(String biggerProfileImageURLHttps) {
        this.biggerProfileImageURLHttps = biggerProfileImageURLHttps;
    }
    public String getMiniProfileImageURLHttps() {
        return miniProfileImageURLHttps;
    }
    public void setMiniProfileImageURLHttps(String miniProfileImageURLHttps) {
        this.miniProfileImageURLHttps = miniProfileImageURLHttps;
    }
    public String getOriginalProfileImageURLHttps() {
        return originalProfileImageURLHttps;
    }
    public void setOriginalProfileImageURLHttps(String originalProfileImageURLHttps) {
        this.originalProfileImageURLHttps = originalProfileImageURLHttps;
    }
    public boolean isDefaultProfileImage() {
        return defaultProfileImage;
    }
    public void setDefaultProfileImage(boolean isDefaultProfileImage) {
        this.defaultProfileImage = isDefaultProfileImage;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public boolean isProtected() {
        return isProtected;
    }
    public void setProtected(boolean isProtected) {
        this.isProtected = isProtected;
    }
    public int getFollowersCount() {
        return followersCount;
    }
    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }
    public String getProfileBackgroundColor() {
        return profileBackgroundColor;
    }
    public void setProfileBackgroundColor(String profileBackgroundColor) {
        this.profileBackgroundColor = profileBackgroundColor;
    }
    public String getProfileTextColor() {
        return profileTextColor;
    }
    public void setProfileTextColor(String profileTextColor) {
        this.profileTextColor = profileTextColor;
    }
    public String getProfileLinkColor() {
        return profileLinkColor;
    }
    public void setProfileLinkColor(String profileLinkColor) {
        this.profileLinkColor = profileLinkColor;
    }
    public String getProfileSidebarFillColor() {
        return profileSidebarFillColor;
    }
    public void setProfileSidebarFillColor(String profileSidebarFillColor) {
        this.profileSidebarFillColor = profileSidebarFillColor;
    }
    public String getProfileSidebarBorderColor() {
        return profileSidebarBorderColor;
    }
    public void setProfileSidebarBorderColor(String profileSidebarBorderColor) {
        this.profileSidebarBorderColor = profileSidebarBorderColor;
    }
    public boolean isProfileUseBackgroundImage() {
        return profileUseBackgroundImage;
    }
    public void setProfileUseBackgroundImage(boolean isProfileUseBackgroundImage) {
        this.profileUseBackgroundImage = isProfileUseBackgroundImage;
    }
    public boolean isDefaultProfile() {
        return defaultProfile;
    }
    public void setDefaultProfile(boolean isDefaultProfile) {
        this.defaultProfile = isDefaultProfile;
    }
    public boolean isShowAllInlineMedia() {
        return showAllInlineMedia;
    }
    public void setShowAllInlineMedia(boolean isShowAllInlineMedia) {
        this.showAllInlineMedia = isShowAllInlineMedia;
    }
    public int getFriendsCount() {
        return friendsCount;
    }
    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public int getFavouritesCount() {
        return favouritesCount;
    }
    public void setFavouritesCount(int favouritesCount) {
        this.favouritesCount = favouritesCount;
    }
    public int getUtcOffset() {
        return utcOffset;
    }
    public void setUtcOffset(int utcOffset) {
        this.utcOffset = utcOffset;
    }
    public String getTimeZone() {
        return timeZone;
    }
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
    public String getProfileBackgroundImageURL() {
        return profileBackgroundImageURL;
    }
    public void setProfileBackgroundImageURL(String profileBackgroundImageURL) {
        this.profileBackgroundImageURL = profileBackgroundImageURL;
    }
    public String getProfileBackgroundImageUrlHttps() {
        return profileBackgroundImageUrlHttps;
    }
    public void setProfileBackgroundImageUrlHttps(String profileBackgroundImageUrlHttps) {
        this.profileBackgroundImageUrlHttps = profileBackgroundImageUrlHttps;
    }
    public String getProfileBannerURL() {
        return profileBannerURL;
    }
    public void setProfileBannerURL(String profileBannerURL) {
        this.profileBannerURL = profileBannerURL;
    }
    public String getProfileBannerRetinaURL() {
        return profileBannerRetinaURL;
    }
    public void setProfileBannerRetinaURL(String profileBannerRetinaURL) {
        this.profileBannerRetinaURL = profileBannerRetinaURL;
    }
    public String getProfileBannerIPadURL() {
        return profileBannerIPadURL;
    }
    public void setProfileBannerIPadURL(String profileBannerIPadURL) {
        this.profileBannerIPadURL = profileBannerIPadURL;
    }
    public String getProfileBannerIPadRetinaURL() {
        return profileBannerIPadRetinaURL;
    }
    public void setProfileBannerIPadRetinaURL(String profileBannerIPadRetinaURL) {
        this.profileBannerIPadRetinaURL = profileBannerIPadRetinaURL;
    }
    public String getProfileBannerMobileURL() {
        return profileBannerMobileURL;
    }
    public void setProfileBannerMobileURL(String profileBannerMobileURL) {
        this.profileBannerMobileURL = profileBannerMobileURL;
    }
    public String getProfileBannerMobileRetinaURL() {
        return profileBannerMobileRetinaURL;
    }
    public void setProfileBannerMobileRetinaURL(String profileBannerMobileRetinaURL) {
        this.profileBannerMobileRetinaURL = profileBannerMobileRetinaURL;
    }
    public boolean isProfileBackgroundTiled() {
        return profileBackgroundTiled;
    }
    public void setProfileBackgroundTiled(boolean isProfileBackgroundTiled) {
        this.profileBackgroundTiled = isProfileBackgroundTiled;
    }
    public String getLang() {
        return lang;
    }
    public void setLang(String lang) {
        this.lang = lang;
    }
    public int getStatusesCount() {
        return statusesCount;
    }
    public void setStatusesCount(int statusesCount) {
        this.statusesCount = statusesCount;
    }
    public boolean isGeoEnabled() {
        return isGeoEnabled;
    }
    public void setGeoEnabled(boolean isGeoEnabled) {
        this.isGeoEnabled = isGeoEnabled;
    }
    public boolean isVerified() {
        return isVerified;
    }
    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }
    public boolean isTranslator() {
        return isTranslator;
    }
    public void setTranslator(boolean isTranslator) {
        this.isTranslator = isTranslator;
    }
    public int getListedCount() {
        return listedCount;
    }
    public void setListedCount(int listedCount) {
        this.listedCount = listedCount;
    }
    public boolean isFollowRequestSent() {
        return isFollowRequestSent;
    }
    public void setFollowRequestSent(boolean isFollowRequestSent) {
        this.isFollowRequestSent = isFollowRequestSent;
    }
    public String[] getWithheldInCountries() {
        return withheldInCountries;
    }
    public void setWithheldInCountries(String[] withheldInCountries) {
        this.withheldInCountries = withheldInCountries;
    }

}
