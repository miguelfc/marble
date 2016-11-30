package org.marble.commons.domain.projections;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.marble.model.domain.model.Job;
import org.marble.model.model.JobParameters;
import org.marble.model.model.JobStatus;
import org.marble.model.model.JobType;
import org.marble.util.BigIntegerSerializer;
import org.marble.util.ChartsIdsSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Projection(name = "jobExtended", types = { Job.class }) 
public interface JobExtendedProjection {
    @JsonSerialize(using = BigIntegerSerializer.class)
    BigInteger getId();
    JobType getType();
    JobStatus getStatus();
    String getLog();
    Date getCreatedAt();
    Date getUpdatedAt();
    Set<JobParameters> getJobParameters();
    
    @Value("#{target.topic != null? target.topic.name : null}")
    String getTopicName();
    
    
    @Value("#{target.charts != null? target.charts : null}")
    @JsonSerialize(using = ChartsIdsSerializer.class)
    List getCharts();
    
}

