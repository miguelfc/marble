package org.marble.commons.service;

import org.marble.commons.domain.repository.MongoFileRepository;
import org.marble.model.domain.model.MongoFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSFile;

@Service
public class MongoFileServiceImpl implements MongoFileService {

  @Autowired
  MongoFileRepository mongoFileRepository;
  
  @Override
  public MongoFile<DBObject> findById(String id) {
    return mongoFileRepository.findById(id);
  }
  
  @Override
  public MongoFile<DBObject> findByFilename(String filename) {
    return mongoFileRepository.findByFilename(filename);
  }

  @Override
  public <T> MongoFile<T> findByFilename(String filename, Class<T> metadataType) {
    return mongoFileRepository.findByFilename(filename, metadataType);
  }

  @Override
  public String[] listFilenames(String locationPattern, Criteria... additionalCriteria) {
    return mongoFileRepository.listFilenames(locationPattern, additionalCriteria);
  }

  @Override
  public GridFSFile insert(MongoFile<?> file) {
    return mongoFileRepository.insert(file);
  }

  @Override
  public void delete(Query query) {
    mongoFileRepository.delete(query);
  }
}
