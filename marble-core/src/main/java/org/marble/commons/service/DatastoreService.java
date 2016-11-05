package org.marble.commons.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Query;
import org.marble.commons.domain.model.Post;

import com.mongodb.DBCursor;
import com.mongodb.MongoException;

public interface DatastoreService {

    <T> void save(T object);

    <T> void removeCollection(Class<T> entityClass);
    
    <T> void findAllAndRemove(Query query, Class<T> entityClass);

    <T> T findOneByQuery(Query query, Class<T> entityClass) throws MongoException;

    <T> List<T> findByTopicId(String topicName, Class<T> entityClass) throws MongoException;

    <T> List<T> findByQuery(Query query, Class<T> entityClass) throws MongoException;

    <T> T findOneByText(String text, Class<T> entityClass) throws MongoException;

    <T> List<T> findAll(Class<T> entityClass);

    
    <T> long countByTopicId(String topicName, Class<T> entityClass);

    <T> DBCursor findCursorByTopicName(String topicName, Class<T> entityClass);

    MongoConverter getConverter();

    <T> T findOneByTopicIdSortBy(String topicName, String field, Direction direction, Class<T> entityClass);

    <T> DBCursor findCursorByQuery(Map<String, Object> queryParameters, Class<T> entityClass);

    <T> DBCursor findCursorForAll(Class<T> entityClass);

}