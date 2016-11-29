package org.marble.commons.domain.projections;

import java.math.BigInteger;
import java.util.Date;

import org.marble.model.domain.model.Plot;
import org.marble.util.BigIntegerSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mongodb.BasicDBObject;

@Projection(name = "plotExtended", types = { Plot.class }) 
public interface PlotExtendedProjection {
    
    @JsonSerialize(using = BigIntegerSerializer.class)
    BigInteger getId();
    String getName();
    String getDescription();
    String getType();
    
    @Value("#{target.topic != null? target.topic.name : null}")
    String getTopicName();
    
    @JsonSerialize(using = BigIntegerSerializer.class)
    BigInteger getJobId();
    
    BasicDBObject getOptions();
    BasicDBObject getData();
    
    Date getCreatedAt();
}

