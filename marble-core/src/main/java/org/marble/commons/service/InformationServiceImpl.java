package org.marble.commons.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import org.marble.commons.model.ExecutorInfo;
import org.marble.commons.model.HomeInformation;
import org.marble.commons.model.JobModuleDefinition;

@Service
public class InformationServiceImpl implements InformationService {
    
    @Autowired
    TopicService topicService;
    @Autowired
    JobService executionService;
    @Autowired
    PlotService plotService;
    @Autowired
    PostService postService;
    @Autowired
    ModuleService moduleService;

    @Override
    public HomeInformation getHomeInformation() {
        HomeInformation homeInformation = new HomeInformation();
        homeInformation.setTopics(topicService.count());
        homeInformation.setExecutions(executionService.count());
        homeInformation.setPlots(plotService.count());
        homeInformation.setPosts(postService.count());
        return homeInformation;
    }
    
    @Override
    public List<ExecutorInfo> getProcessorsInfo() {
        List<ExecutorInfo> infos = new ArrayList<>(); 
        List<JobModuleDefinition> modules = moduleService.getProcessorModules();
        for (JobModuleDefinition module : modules) {
            ExecutorInfo info = new ExecutorInfo(module);
            infos.add(info);
        }
        return infos;
    }
    
    @Override
    public List<ExecutorInfo> getPlottersInfo() {
        List<ExecutorInfo> infos = new ArrayList<>(); 
        List<JobModuleDefinition> modules = moduleService.getPlotterModules();
        for (JobModuleDefinition module : modules) {
            ExecutorInfo info = new ExecutorInfo(module);
            infos.add(info);
        }
        return infos;
    }
    
        
}
