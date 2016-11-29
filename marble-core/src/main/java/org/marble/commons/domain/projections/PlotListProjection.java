package org.marble.commons.domain.projections;

import java.math.BigInteger;
import java.util.Date;

import org.marble.model.domain.model.Plot;
import org.marble.util.BigIntegerSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Projection(name = "plotList", types = { Plot.class }) 
public interface PlotListProjection {
    @JsonSerialize(using = BigIntegerSerializer.class)
    BigInteger getId();
    String getName();
    String getDescription();
    Date getCreatedAt();
    
    @Value("#{target.topic != null? target.topic.name : null}")
    String getTopicName();
    
    @Value("#{target.jobId}")
    @JsonSerialize(using = BigIntegerSerializer.class)
    BigInteger getJobId();
}

