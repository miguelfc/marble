package org.marble.processor.stanford.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.DBCursor;
import com.mongodb.MongoException;

public interface DatastoreService {
    <T> void removeCollection(Class<T> entityClass);

    <T> void save(T object);

    <T> List<T> findAll(Class<T> entityClass);
}