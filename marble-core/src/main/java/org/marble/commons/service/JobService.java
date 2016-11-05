package org.marble.commons.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import org.marble.commons.domain.model.Job;
import org.marble.commons.exception.InvalidExecutionException;
import org.marble.commons.exception.InvalidModuleException;
import org.marble.commons.exception.InvalidTopicException;
import org.marble.commons.model.JobModuleParameters;
import org.marble.commons.model.ProcessParameters;

public interface JobService {

    public Job save(Job execution) throws InvalidExecutionException;

    public Job findOne(BigInteger id) throws InvalidExecutionException;
    
    public Long deleteByTopicName(String topicName);

    public void appendToLog(BigInteger id, String log) throws InvalidExecutionException;

    public BigInteger executeExtractor(String topicName) throws InvalidTopicException, InvalidExecutionException;

    public BigInteger executeProcessor(String topicName, Set<ProcessParameters> processParameters)
            throws InvalidTopicException, InvalidExecutionException, InvalidModuleException;

    public BigInteger executePlotter(String topicName, JobModuleParameters plotParameters)
            throws InvalidTopicException,
            InvalidExecutionException, InvalidModuleException;

    Long count();
    
    Long countByTopicName(String topicName);

    BigInteger executeProcessor(Set<ProcessParameters> processParameters) throws InvalidTopicException, InvalidExecutionException, InvalidModuleException;
}
