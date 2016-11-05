package org.marble.commons.service;

import java.math.BigInteger;
import java.util.List;

import org.marble.commons.domain.model.TwitterApiKey;
import org.marble.commons.exception.InvalidTwitterApiKeyException;

public interface TwitterApiKeyService {

	public TwitterApiKey updateTwitterApiKey(TwitterApiKey twitterApiKey) throws InvalidTwitterApiKeyException;

	public TwitterApiKey getTwitterApiKey(BigInteger id) throws InvalidTwitterApiKeyException;

	List<TwitterApiKey> getTwitterApiKeys();
	
	List<TwitterApiKey> getEnabledTwitterApiKeys();

	public void deleteTwitterApiKey(BigInteger id);

	public TwitterApiKey createTwitterApiKey(TwitterApiKey twitterApiKey) throws InvalidTwitterApiKeyException;

}
