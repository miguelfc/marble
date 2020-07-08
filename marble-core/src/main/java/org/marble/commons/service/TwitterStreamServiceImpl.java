package org.marble.commons.service;

import org.marble.commons.domain.model.TwitterApiKey;
import org.springframework.stereotype.Service;

import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

@Service
public class TwitterStreamServiceImpl implements TwitterStreamService {
   
    private TwitterApiKey apiKey;
    private Configuration configuration;
    
    TwitterStream twitterStream;

    public TwitterStreamServiceImpl(){
        
    }
    
    public TwitterStreamServiceImpl(TwitterApiKey apiKey) {
        this.configure(apiKey);
    }
   
    @Override
    public TwitterStream configure(TwitterApiKey apiKey) {

        this.apiKey = apiKey;

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setJSONStoreEnabled(true);
        configurationBuilder.setOAuthConsumerKey(this.apiKey.getConsumerKey());
        configurationBuilder.setOAuthConsumerSecret(this.apiKey.getConsumerSecret());
        configurationBuilder.setOAuthAccessToken(this.apiKey.getAccessToken());
        configurationBuilder.setOAuthAccessTokenSecret(this.apiKey.getAccessTokenSecret());

        this.configuration = configurationBuilder.build();
        this.twitterStream = new TwitterStreamFactory(configuration).getInstance();
        return this.twitterStream;
    }
    
    @Override
    public void unconfigure() {
        this.apiKey = null;
        this.configuration = null;
        this.twitterStream = null;
    }
    
    @Override
    public TwitterStream addListener(StatusListener listener){
        twitterStream.shutdown();
        twitterStream.addListener(listener);
        twitterStream.sample();
        return this.twitterStream;
    }
    
    @Override
    public TwitterStream removeListener(StatusListener listener){
        twitterStream.shutdown();
        twitterStream.removeListener(listener);
        twitterStream.sample();
        return this.twitterStream;
    }
}
