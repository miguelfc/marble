package org.marble.commons.domain.repository;

import java.math.BigInteger;

import org.marble.model.domain.model.Job;
import org.marble.model.model.JobStatus;
import org.marble.model.model.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface JobRepository extends PagingAndSortingRepository<Job, BigInteger> {
    Page<Job> findByTopic_name(@Param("name") String name, Pageable pageable);

    Page<Job> findByTopic_nameAndTypeAndStatus(@Param("name") String name, @Param("type") JobType type, @Param("status") JobStatus status, Pageable pageable);

    Page<Job> findByStatus(@Param("status") JobStatus status, Pageable pageable);

    Page<Job> findByTypeAndStatus(@Param("type") JobType type, @Param("status") JobStatus status, Pageable pageable);

    // Auto query doesn't work at the topic level for Matches
    @Query("{ 'topic.$id': { $regex: '?0' } }")
    Page<Job> findByTopic_nameMatches(@Param("name") String name, Pageable pageable);

    Long deleteByTopic_name(String name);

    Long countByTopic_name(String name);
}
