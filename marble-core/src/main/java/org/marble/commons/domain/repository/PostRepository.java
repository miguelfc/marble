package org.marble.commons.domain.repository;

import java.util.List;

import org.marble.model.domain.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

public interface PostRepository
        extends PagingAndSortingRepository<Post, Long> {
    List<Post> findAll();

    Page<Post> findByTopicName(@Param("name") String name, Pageable pageable);

    Page<Post> findByTopicNameMatches(@Param("name") String name, Pageable pageable);

    @RestResource(exported = false)
    Long deleteByTopicName(@Param("name") String name);
}
