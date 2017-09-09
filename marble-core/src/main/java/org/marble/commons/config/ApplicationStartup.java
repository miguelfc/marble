package org.marble.commons.config;

import org.marble.commons.service.JobService;
import org.marble.commons.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    JobService jobService;

    @Autowired
    TopicService topicService;
    
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        jobService.cleanOldJobs();
        topicService.restartStreamingTopics();
        return;
    }
}