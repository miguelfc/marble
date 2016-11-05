package org.marble.commons.service;

import java.util.List;

import org.marble.commons.model.ExecutorInfo;
import org.marble.commons.model.HomeInformation;

public interface InformationService {

    HomeInformation getHomeInformation();

    List<ExecutorInfo> getProcessorsInfo();
    
    List<ExecutorInfo> getPlottersInfo();

}
