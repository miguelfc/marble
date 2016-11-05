package org.marble.commons.service;

import java.util.List;

import org.marble.commons.domain.model.Topic;
import org.marble.commons.exception.InvalidTopicException;
import org.marble.commons.model.TopicStats;

public interface TopicService {

	public Topic save(Topic topic) throws InvalidTopicException;

	public Topic findOne(String name) throws InvalidTopicException;

	List<Topic> findAll();

	public void delete(String name);

    Long count();

	TopicStats getStats(String name) throws InvalidTopicException;	

}
