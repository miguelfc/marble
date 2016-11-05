package org.marble.commons.domain.repository;

import java.util.List;

import org.marble.commons.domain.model.GeneralProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface GeneralPropertyRepository extends PagingAndSortingRepository<GeneralProperty,String> {
    List<GeneralProperty> findAll();
    Page<GeneralProperty> findByNameMatches(@Param("name") String name, Pageable pageable);
}
