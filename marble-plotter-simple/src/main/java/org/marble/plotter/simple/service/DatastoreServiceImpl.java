package org.marble.plotter.simple.service;

import java.util.List;
import java.util.Map;

import javax.swing.RowFilter.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoException;

@Service
public class DatastoreServiceImpl implements DatastoreService {

    @Autowired
    MongoOperations mongoOperations;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(DatastoreServiceImpl.class);

    @Override
    public <T> T findOneByTopicName(String topicName, Class<T> entityClass) throws MongoException {
        return this.findOneById(topicName, entityClass);
    }
    // Not used
    
    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        return mongoOperations.findAll(entityClass);
    }

    @Override
    public <T> void findAllAndRemove(Query query, Class<T> entityClass) {
        mongoOperations.findAllAndRemove(query, entityClass);
    }

    @Override
    public <T> List<T> findByQuery(Query query, Class<T> entityClass) throws MongoException {
        List<T> result = mongoOperations.find(query, entityClass);
        if (result == null) {
            throw new MongoException("Object <" + entityClass.getName() + "> with query <" + query + "> not found.");
        }
        return result;
    }

    @Override
    public <T> DBCursor findCursorByQuery(Map<String, Object> queryParameters, Class<T> entityClass) {
        Document document = entityClass.getAnnotation(Document.class);
        DBCollection collection = mongoOperations.getCollection(document.collection());
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.putAll(queryParameters);
        DBCursor cursor = collection.find(searchQuery);
        return cursor;
    }

    @Override
    public <T> List<T> findByTopicId(String topicName, Class<T> entityClass) throws MongoException {
        Query query = new Query();
        query.addCriteria(Criteria.where("topicName").is(topicName));
        return this.findByQuery(query, entityClass);
    }

    @Override
    public <T> DBCursor findCursorByTopicName(String topicName, Class<T> entityClass) {
        Document document = entityClass.getAnnotation(Document.class);
        DBCollection collection = mongoOperations.getCollection(document.collection());

        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("topicName", topicName);

        DBCursor cursor = collection.find(searchQuery);
        return cursor;
    }
    
    @Override
    public <T> DBCursor findCursorForAll(Class<T> entityClass) {
        Document document = entityClass.getAnnotation(Document.class);
        DBCollection collection = mongoOperations.getCollection(document.collection());
        DBCursor cursor = collection.find();
        return cursor;
    }

    @Override
    public <T> T findOneByQuery(Query query, Class<T> entityClass) throws MongoException {
        T result = mongoOperations.findOne(query, entityClass);
        if (result == null) {
            throw new MongoException("Object <" + entityClass.getName() + "> with query <" + query + "> not found.");
        }
        return result;
    }
    
    @Override
    public <T> T findOneById(Object id, Class<T> entityClass) throws MongoException {
        T result = mongoOperations.findById(id, entityClass);
        if (result == null) {
            throw new MongoException("Object <" + entityClass.getName() + "> with id <" + id + "> not found.");
        }
        return result;
    }

    @Override
    public <T> T findOneByText(String text, Class<T> entityClass) throws MongoException {
        Query query = new Query();
        query.addCriteria(Criteria.where("text").is(text));
        return this.findOneByQuery(query, entityClass);
    }

    @Override
    public <T> T findOneByTopicIdSortBy(String topicName, String field, Direction direction, Class<T> entityClass) {
        Query query = new Query();
        query.addCriteria(Criteria.where("topicName").is(topicName));
        query.with(new Sort(direction, field));
        return this.findOneByQuery(query, entityClass);
    }

    @Override
    public MongoConverter getConverter() {
        return mongoOperations.getConverter();
    }


    @Override
    public <T> void removeCollection(Class<T> entityClass) {
        mongoOperations.dropCollection(entityClass);

    }

    @Override
    public <T> void save(T object) {
        mongoOperations.save(object);

    }

    @Override
    public <T> long countByTopicId(String topicName, Class<T> entityClass) {
        Query query = new Query();
        query.addCriteria(Criteria.where("topicName").is(topicName));
        return mongoOperations.count(query, entityClass);
    }

}