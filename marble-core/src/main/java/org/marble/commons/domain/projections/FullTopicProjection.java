package org.marble.commons.domain.projections;

import org.marble.model.domain.model.Topic;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "fullTopic", types = { Topic.class }) 
public interface FullTopicProjection {
    String getName();
}

