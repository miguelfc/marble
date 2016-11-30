package org.marble.commons.domain.repository;

import java.math.BigInteger;
import java.util.List;

import org.marble.model.domain.model.Job;
import org.marble.model.domain.model.Chart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface ChartRepository extends PagingAndSortingRepository<Chart,BigInteger> {
    Page<Chart> findByTopic_name(@Param("name") String name, Pageable pageable);
	
    // Auto query doesn't work at the topic level for Matches
    @Query("{ 'topic.$id': { $regex: '?0' } }")
    Page<Chart> findByTopic_nameMatches(@Param("name") String name, Pageable pageable);

    Long deleteByTopic_name(String name);
    
    Long countByTopic_name(String name);
}
