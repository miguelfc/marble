package org.marble.processor.stanford.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    public <T> List<T> findAll(Class<T> entityClass) {
        return mongoOperations.findAll(entityClass);
    }

    @Override
    public <T> void removeCollection(Class<T> entityClass) {
        mongoOperations.dropCollection(entityClass);

    }

    @Override
    public <T> void save(T object) {
        mongoOperations.save(object);

    }

}