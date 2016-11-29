package org.marble.commons.domain.repository;

import java.util.List;

import org.marble.model.domain.model.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

public interface TopicRepository extends PagingAndSortingRepository<Topic, String>{
	List<Topic> findAll();
	Page<Topic> findByNameMatches(@Param("name") String name, Pageable pageable);
	
	// The delete operation is defined in the custom rest controller 
    @Override
    @RestResource(exported = false)
    public void delete(String name);
}
