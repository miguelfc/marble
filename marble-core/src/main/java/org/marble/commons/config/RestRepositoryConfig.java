package org.marble.commons.config;

import org.marble.commons.domain.model.GeneralProperty;
import org.marble.commons.domain.model.TwitterApiKey;
import org.marble.commons.domain.projections.JobExtendedProjection;
import org.marble.commons.domain.projections.ChartExtendedProjection;
import org.marble.commons.domain.projections.ChartListProjection;
import org.marble.model.domain.model.Job;
import org.marble.model.domain.model.Chart;
import org.marble.model.domain.model.Post;
import org.marble.model.domain.model.Topic;
import org.marble.commons.domain.projections.FullTopicProjection;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

@Configuration
public class RestRepositoryConfig extends RepositoryRestMvcConfiguration {

    // show id's of Entities
    @Override
    protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Topic.class);
        config.exposeIdsFor(Chart.class);
        config.exposeIdsFor(Job.class);
        config.exposeIdsFor(TwitterApiKey.class);
        config.exposeIdsFor(GeneralProperty.class);
        config.exposeIdsFor(Post.class);
        config.getProjectionConfiguration()
                .addProjection(FullTopicProjection.class)
                .addProjection(JobExtendedProjection.class)
                .addProjection(ChartListProjection.class)
                .addProjection(ChartExtendedProjection.class);
        config.setBasePath("/api");
    }
}