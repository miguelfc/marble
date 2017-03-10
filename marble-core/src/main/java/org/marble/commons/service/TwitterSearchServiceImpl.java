package org.marble.commons.service;

import java.util.List;

import org.springframework.stereotype.Service;

import org.marble.commons.domain.model.TwitterApiKey;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.Query.Unit;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

@Service
public class TwitterSearchServiceImpl implements TwitterSearchService {

    final public long DEFAULT_MAX_ID = 0;
    final public int DEFAULT_COUNT = 100;

    private TwitterApiKey apiKey;
    private Integer statusesPerCall = DEFAULT_COUNT;
    private Configuration configuration;

    Twitter twitter;

    public TwitterSearchServiceImpl() {

    }

    public TwitterSearchServiceImpl(TwitterApiKey apiKey) {
        this.configure(apiKey);
    }

    @Override
    public Integer getStatusesPerCall() {
        return statusesPerCall;
    }

    @Override
    public void setStatusesPerCall(Integer statusesPerCall) {
        this.statusesPerCall = statusesPerCall;
    }

    @Override
    public void configure(TwitterApiKey apiKey) {

        this.apiKey = apiKey;

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setJSONStoreEnabled(true);

        configurationBuilder.setOAuthConsumerKey(this.apiKey.getConsumerKey());
        configurationBuilder.setOAuthConsumerSecret(this.apiKey.getConsumerSecret());
        configurationBuilder.setOAuthAccessToken(this.apiKey.getAccessToken());
        configurationBuilder.setOAuthAccessTokenSecret(this.apiKey.getAccessTokenSecret());

        this.configuration = configurationBuilder.build();

        TwitterFactory factory = new TwitterFactory(configuration);
        this.twitter = factory.getInstance();

    }

    @Override
    public void unconfigure() {
        this.apiKey = null;
        this.configuration = null;
        this.twitter = null;
    }

    @Override
    public List<Status> search(String keywords) throws TwitterException {
        return this.search(keywords, this.DEFAULT_MAX_ID);
    }

    @Override
    public List<Status> search(String keywords, long maxId) throws TwitterException {
        return this.search(keywords, maxId, null);
    }

    @Override
    public List<Status> search(String keywords, long maxId, String language) throws TwitterException {
        return this.search(keywords, maxId, language);
    }

    @Override
    public List<Status> search(String keywords, long maxId, String language, GeoLocation geoLocation, Double radius) throws TwitterException {

        QueryResult result = null;

        Query query = new Query(keywords);

        if (maxId != this.DEFAULT_MAX_ID) {
            query.setMaxId(maxId - 1);
        }
        if (language != null) {
            query.setLang(language);
        }

        if (geoLocation != null && radius != null) {
            query.setGeoCode(geoLocation, radius.doubleValue(), Unit.km);
        }

        query.setCount(this.statusesPerCall);

        result = twitter.search(query);

        return result.getTweets();
    }

}
