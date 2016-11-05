package org.marble.commons.domain.repository;

import java.math.BigInteger;
import java.util.List;

import org.marble.commons.domain.model.TwitterApiKey;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface TwitterApiKeyRepository extends PagingAndSortingRepository<TwitterApiKey,BigInteger> {
	List<TwitterApiKey> findAll();
	List<TwitterApiKey> findByEnabled(Boolean enabled);
}
