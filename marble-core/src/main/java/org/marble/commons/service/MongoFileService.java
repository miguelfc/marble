package org.marble.commons.service;

import org.marble.model.domain.model.MongoFile;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSFile;

public interface MongoFileService {
  
  public MongoFile<DBObject> findById(String id); 
  
  public MongoFile<DBObject> findByFilename(String filename);

  public <T> MongoFile<T> findByFilename(String filename, Class<T> metadataType);

  public String[] listFilenames(String locationPattern, Criteria... additionalCriteria);
  
  public GridFSFile insert(MongoFile<?> file);
  
  public void delete(Query query);
  
}
