package org.marble.commons.service;

import java.util.List;

import org.marble.commons.domain.model.TwitterApiKey;

import twitter4j.Status;
import twitter4j.TwitterException;

public interface TwitterService {

    Integer getStatusesPerCall();

    void setStatusesPerCall(Integer statusesPerCall);

    void configure(TwitterApiKey apiKey);

    void unconfigure();

    List<Status> search(String keyword) throws TwitterException;

    List<Status> search(String keyword, long maxId) throws TwitterException;

}
