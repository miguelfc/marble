package org.marble.commons.domain.projections;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;

import org.marble.model.domain.model.Chart;
import org.marble.util.BigIntegerSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mongodb.BasicDBObject;

@Projection(name = "chartExtended", types = { Chart.class }) 
public interface ChartExtendedProjection {
    
    @JsonSerialize(using = BigIntegerSerializer.class)
    BigInteger getId();
    String getName();
    String getDescription();
    String getType();
    String getCustomType();
    
    @Value("#{target.topic != null? target.topic.name : null}")
    String getTopicName();
    
    @JsonSerialize(using = BigIntegerSerializer.class)
    BigInteger getJobId();
    
    BasicDBObject getOptions();
    BasicDBObject getData();
    ArrayList<String> getFigures();
    
    Date getCreatedAt();
}

