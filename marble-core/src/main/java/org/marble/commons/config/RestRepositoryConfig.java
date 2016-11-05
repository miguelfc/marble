package org.marble.commons.config;

import org.marble.commons.domain.model.GeneralProperty;
import org.marble.commons.domain.model.Job;
import org.marble.commons.domain.model.Post;
import org.marble.commons.domain.model.Plot;
import org.marble.commons.domain.model.Topic;
import org.marble.commons.domain.model.TwitterApiKey;
import org.marble.commons.domain.projections.JobExtendedProjection;
import org.marble.commons.domain.projections.PlotExtendedProjection;
import org.marble.commons.domain.projections.PlotListProjection;
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
        config.exposeIdsFor(Plot.class);
        config.exposeIdsFor(Job.class);
        config.exposeIdsFor(TwitterApiKey.class);
        config.exposeIdsFor(GeneralProperty.class);
        config.exposeIdsFor(Post.class);
        config.getProjectionConfiguration()
                .addProjection(FullTopicProjection.class)
                .addProjection(JobExtendedProjection.class)
                .addProjection(PlotListProjection.class)
                .addProjection(PlotExtendedProjection.class);
        config.setBasePath("/api");
    }
}