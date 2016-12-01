package org.marble.commons.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import org.marble.commons.model.HomeInformation;

@Service
public class InformationServiceImpl implements InformationService {
    
    @Autowired
    TopicService topicService;
    @Autowired
    JobService executionService;
    @Autowired
    ChartService chartService;
    @Autowired
    PostService postService;

    @Override
    public HomeInformation getHomeInformation() {
        HomeInformation homeInformation = new HomeInformation();
        homeInformation.setTopics(topicService.count());
        homeInformation.setExecutions(executionService.count());
        homeInformation.setCharts(chartService.count());
        homeInformation.setPosts(postService.count());
        return homeInformation;
    }
        
}
