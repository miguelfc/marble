package org.marble.commons.service;

import java.beans.Introspector;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import org.marble.commons.domain.repository.JobRepository;
import org.marble.commons.domain.model.Job;
import org.marble.commons.domain.model.Topic;
import org.marble.commons.exception.InvalidExecutionException;
import org.marble.commons.exception.InvalidModuleException;
import org.marble.commons.exception.InvalidTopicException;
import org.marble.commons.executor.extractor.ExtractorExecutor;
import org.marble.commons.executor.plotter.PlotterExecutor;
import org.marble.commons.executor.processor.ProcessorExecutor;
import org.marble.commons.executor.processor.ProcessorExecutorImpl;
import org.marble.commons.model.JobStatus;
import org.marble.commons.model.JobType;
import org.marble.commons.model.ProcessParameters;
import org.marble.commons.model.JobModuleParameters;
import org.marble.commons.model.ExecutorParameter;
import org.marble.commons.model.JobModuleDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobServiceImpl implements JobService {

    private static final Logger log = LoggerFactory.getLogger(JobServiceImpl.class);

    @Autowired
    JobRepository jobDao;

    @Autowired
    TopicService topicService;

    @Autowired
    PlotService plotService;

    @Autowired
    ModuleService moduleService;
    
    @Autowired
    ProcessorExecutor processorExecutor;
    
    @Autowired
    @Qualifier(value = "taskExecutor")
    private TaskExecutor taskExecutor;
    @Autowired
    private ApplicationContext context;

    @Override
    public Job findOne(BigInteger id) throws InvalidExecutionException {
        Job execution = jobDao.findOne(id);
        if (execution == null) {
            throw new InvalidExecutionException("E1");
        }
        return execution;
    }
    
    public Long deleteByTopicName(String name) {
        return jobDao.deleteByTopic_name(name);
    }

    @Override
    public void appendToLog(BigInteger id, String log) throws InvalidExecutionException {
        Job execution = jobDao.findOne(id);
        if (execution == null) {
            throw new InvalidExecutionException("E2");
        }
        execution.appendLog(log);
        jobDao.save(execution);
        return;
    }

    @Override
    public Job save(Job execution) throws InvalidExecutionException {
        execution = jobDao.save(execution);
        if (execution == null) {
            throw new InvalidExecutionException("E3");
        }
        return execution;
    }

    @Override
    @Transactional
    public BigInteger executeExtractor(String topicName) throws InvalidTopicException, InvalidExecutionException {
        log.info("Executing the extractor for topic <" + topicName + ">.");

        Job execution = new Job();

        Topic topic = topicService.findOne(topicName);

        execution.setStatus(JobStatus.Initialized);
        execution.setType(JobType.Extractor);
        execution.setTopic(topic);
        
        execution = this.save(execution);

        log.info("Starting execution <" + execution.getId() + ">... now!");
        ExtractorExecutor executor = (ExtractorExecutor) context.getBean("twitterExtractionExecutor");
        executor.setExecution(execution);
        taskExecutor.execute(executor);

        log.info("Executor launched.");

        return execution.getId();
    }

    @Override
    @Transactional
    public BigInteger executeProcessor(String topicName, Set<ProcessParameters> processParameters) throws InvalidTopicException,
            InvalidExecutionException, InvalidModuleException {
        
        if (topicName != null) {
            log.info("Executing the processor for topic <" + topicName + ">.");
        } else {
            log.info("Executing the validation of this processor.");
        }
        
        // prepare the execution

        Job execution = new Job();
        log.error("Parameters: ");
        for (ProcessParameters parameter: processParameters) {
            log.error("- " + parameter);
        }

        execution.setStatus(JobStatus.Initialized);
        execution.setType(JobType.Processor);
        execution.setParameters(processParameters);

        if (topicName != null) {
            Topic topic = topicService.findOne(topicName);
            topic.setLastProcessParameters(processParameters);
            topicService.save(topic);
            execution.setTopic(topic);
        }

        execution = this.save(execution);

        log.info("Starting execution <" + execution.getId() + ">... now!");
        //ProcessorExecutor processorExecutor = new ProcessorExecutorImpl();
        processorExecutor.setExecution(execution);
        taskExecutor.execute(processorExecutor);
        
        log.info("Executor launched.");

        return execution.getId();
    }

    @Override
    @Transactional
    public BigInteger executeProcessor( Set<ProcessParameters> processParameters) throws InvalidTopicException,
            InvalidExecutionException, InvalidModuleException {
        return this.executeProcessor(null,   processParameters);
    }

    @Override
    @Transactional
    public BigInteger executePlotter(String topicName, JobModuleParameters moduleParameters) throws InvalidTopicException,
            InvalidExecutionException, InvalidModuleException {
        log.info("Executing the processor for topic <" + topicName + ">.");

        // First, a check is made in order to prevent Injection Attacks
        JobModuleDefinition module = moduleService.getPlotterModule(moduleParameters.getModule());
        if (module == null) {
            String message = "The module <" + moduleParameters.getModule() + "> is invalid.";
            log.error(message);
            throw new InvalidModuleException(message);
        }

        // Second, check the operation is valid
        if (moduleParameters.getOperation() == null || !module.getOperations().contains(new ExecutorParameter(moduleParameters.getOperation()))) {
            String message = "The operation <" + moduleParameters.getOperation() + "> for module <" + moduleParameters.getModule()
            + "> is invalid.";
            log.error(message);
            throw new InvalidModuleException(message);
        }

        // Fourth, prepare the execution
        Job execution = new Job();

        Topic topic = topicService.findOne(topicName);

        execution.setStatus(JobStatus.Initialized);
        execution.setType(JobType.Plotter);
        execution.setTopic(topic);
        execution.setModuleParameters(moduleParameters);

        execution = this.save(execution);

        log.info("Starting execution <" + Introspector.decapitalize(module.getSimpleName()) + "|" + execution.getId() + ">... now!");
        PlotterExecutor executor = (PlotterExecutor) context.getBean(Introspector.decapitalize(module.getSimpleName()));
        executor.setExecution(execution);
        taskExecutor.execute(executor);

        log.info("Executor launched.");
        return execution.getId();

    }

    @Override
    public Long count() {
        return jobDao.count();
    }
    
    @Override
    public Long countByTopicName(String topicName) {
        return jobDao.countByTopic_name(topicName);
    }
}
