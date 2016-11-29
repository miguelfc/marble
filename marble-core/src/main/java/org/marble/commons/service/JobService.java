package org.marble.commons.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import org.marble.commons.exception.InvalidExecutionException;
import org.marble.commons.exception.InvalidModuleException;
import org.marble.commons.exception.InvalidTopicException;
import org.marble.model.domain.model.Job;
import org.marble.model.model.JobParameters;

public interface JobService {

    public Job save(Job execution) throws InvalidExecutionException;

    public Job findOne(BigInteger id) throws InvalidExecutionException;
    
    public Long deleteByTopicName(String topicName);

    public void appendToLog(BigInteger id, String log) throws InvalidExecutionException;

    public BigInteger executeExtractor(String topicName) throws InvalidTopicException, InvalidExecutionException;

    public BigInteger executeProcessor(String topicName, Set<JobParameters> processParameters)
            throws InvalidTopicException, InvalidExecutionException, InvalidModuleException;

    BigInteger executeProcessor(Set<JobParameters> processParameters) throws InvalidTopicException, InvalidExecutionException, InvalidModuleException;

    Long count();
    
    Long countByTopicName(String topicName);


    BigInteger executePlotter(String topicName, Set<JobParameters> processParameters) throws InvalidTopicException, InvalidExecutionException, InvalidModuleException;
}
