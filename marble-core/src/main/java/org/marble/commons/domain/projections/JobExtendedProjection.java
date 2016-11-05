package org.marble.commons.domain.projections;

import java.math.BigInteger;
import java.util.Date;

import org.marble.commons.domain.model.Job;
import org.marble.commons.domain.model.Topic;
import org.marble.commons.model.JobCommand;
import org.marble.commons.model.JobModuleParameters;
import org.marble.commons.model.JobStatus;
import org.marble.commons.model.JobType;
import org.marble.commons.util.BigIntegerSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Projection(name = "jobExtended", types = { Job.class }) 
public interface JobExtendedProjection {
    @JsonSerialize(using = BigIntegerSerializer.class)
    BigInteger getId();
    JobType getType();
    JobStatus getStatus();
    JobCommand getCommand();
    String getLog();
    Date getCreatedAt();
    Date getUpdatedAt();
    JobModuleParameters getModuleParameters();
    
    @Value("#{target.topic != null? target.topic.name : null}")
    String getTopicName();
    
    @Value("#{target.plot != null? target.plot.id : null}")
    @JsonSerialize(using = BigIntegerSerializer.class)
    BigInteger getPlotId();
}

