package org.marble.commons.service;

import org.marble.commons.domain.model.TwitterApiKey;

import twitter4j.FilterQuery;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;

public interface TwitterStreamService {

    TwitterStream configure(TwitterApiKey apiKey);

    void unconfigure();

    TwitterStream addListener(StatusListener listener);

    TwitterStream removeListener(StatusListener listener);

}