package org.marble.commons.domain.projections;

import java.util.Date;

import org.marble.commons.domain.model.Topic;
import org.marble.commons.model.JobCommand;
import org.marble.commons.model.JobModuleParameters;
import org.marble.commons.model.JobStatus;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "fullTopic", types = { Topic.class }) 
public interface FullTopicProjection {
    String getName();
}

