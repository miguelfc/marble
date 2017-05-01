package org.marble.commons.service;

import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

import org.marble.commons.domain.repository.JobRepository;
import org.marble.commons.exception.InvalidExecutionException;
import org.marble.commons.exception.InvalidModuleException;
import org.marble.commons.exception.InvalidTopicException;
import org.marble.commons.executor.extractor.ExtractorExecutor;
import org.marble.commons.executor.plotter.PlotterExecutor;
import org.marble.commons.executor.processor.ProcessorExecutor;
import org.marble.commons.executor.streamer.StreamerExecutor;
import org.marble.commons.executor.streamer.TwitterStreamerExecutor;
import org.marble.model.domain.model.Job;
import org.marble.model.domain.model.Topic;
import org.marble.model.model.JobParameters;
import org.marble.model.model.JobStatus;
import org.marble.model.model.JobType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    ChartService plotService;

    @Autowired
    ProcessorExecutor processorExecutor;

    @Autowired
    PlotterExecutor plotterExecutor;

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
        executor.setJob(execution);
        taskExecutor.execute(executor);

        log.info("Executor launched.");

        return execution.getId();
    }

    @Override
    @Transactional
    public BigInteger executeStreamer(String topicName) throws InvalidTopicException, InvalidExecutionException {
        log.info("Executing the streamer for topic <" + topicName + ">.");

        Job execution = new Job();

        Topic topic = topicService.findOne(topicName);

        execution.setStatus(JobStatus.Initialized);
        execution.setType(JobType.Streamer);
        execution.setTopic(topic);

        execution = this.save(execution);

        log.info("Starting execution <" + execution.getId() + ">... now!");
        StreamerExecutor executor = (StreamerExecutor) context.getBean("twitterStreamerExecutor");
        executor.setJob(execution);
        taskExecutor.execute(executor);

        log.info("Executor launched.");

        return execution.getId();
    }

    @Override
    @Transactional
    public BigInteger stopStreamer(String topicName) throws InvalidTopicException, InvalidExecutionException {
        log.info("Stopping the streamer for topic <" + topicName + ">.");

        Pageable page = new PageRequest(0, 100);
        Page<Job> results;
        Boolean atLeastOne = false;
        do {
            results = jobDao.findByTopic_nameAndTypeAndStatus(topicName, JobType.Streamer, JobStatus.Running, page);
            for (Job job : results.getContent()) {
                atLeastOne = true;
                job.setStatus(JobStatus.Stopped);
                job = this.save(job);

                log.info("Stoping execution <" + job.getId() + ">... now!");
                TwitterStreamerExecutor executor = (TwitterStreamerExecutor) context.getBean("twitterStreamerExecutor");
                executor.stopStreaming(job);

                log.info("Executor launched.");
                return job.getId();
            }
            page = page.next();
        } while (results.hasNext());
        if (atLeastOne) {
            return BigInteger.ZERO;
        }
        return null;
    }

    @Transactional
    private BigInteger executeProcessor(String topicName, Set<JobParameters> parameters, Job job, LinkedHashSet<JobParameters> extraParameters) throws InvalidTopicException, InvalidExecutionException, InvalidModuleException {

        if (topicName != null) {
            log.info("Executing the processor for topic <" + topicName + ">.");
        } else {
            log.info("Executing the validation of this processor.");
        }

        // prepare the execution

        if (job == null) {
            job = new Job();
            job.setStatus(JobStatus.Initialized);
            job.setType(JobType.Processor);

            log.debug("Execution Parameters: ");
            for (JobParameters parameter : parameters) {
                log.debug("- " + parameter);
            }

            job.setParameters(parameters);

            if (topicName != null) {
                Topic topic = topicService.findOne(topicName);
                topic.setLastProcessParameters(job.getParameters());
                topicService.save(topic);
                job.setTopic(topic);
            }
            
            
            job = this.save(job);
        }

        if (extraParameters != null) {
            processorExecutor.setExtraParameters(extraParameters);
        }

        log.info("Starting execution <" + job.getId() + ">... now!");
        processorExecutor.setJob(job);
        taskExecutor.execute(processorExecutor);

        log.info("Executor launched.");

        return job.getId();
    }

    @Override
    @Transactional
    public BigInteger executeProcessor(LinkedHashSet<JobParameters> processParameters) throws InvalidTopicException, InvalidExecutionException, InvalidModuleException {
        return this.executeProcessor(null, processParameters, null, null);
    }

    @Override
    @Transactional
    public BigInteger executeProcessor(String topicName, LinkedHashSet<JobParameters> processParameters) throws InvalidTopicException, InvalidExecutionException, InvalidModuleException {
        return this.executeProcessor(topicName, processParameters, null, null);
    }

    @Override
    @Transactional
    public BigInteger executeProcessor(String topicName, Job job, LinkedHashSet<JobParameters> extraParameters) throws InvalidTopicException, InvalidExecutionException, InvalidModuleException {
        return this.executeProcessor(topicName, null, job, extraParameters);
    }

    @Override
    @Transactional
    public BigInteger executePlotter(String topicName, LinkedHashSet<JobParameters> parameters) throws InvalidTopicException, InvalidExecutionException {

        if (topicName != null) {
            log.info("Executing the plotter for topic <" + topicName + ">.");
        } else {
            log.error("A topic is needed for the plotter to work. Aborting.");
            return null;
        }

        // prepare the execution

        Job execution = new Job();
        log.error("Parameters: ");
        for (JobParameters parameter : parameters) {
            log.error("- " + parameter);
        }

        execution.setStatus(JobStatus.Initialized);
        execution.setType(JobType.Plotter);
        execution.setParameters(parameters);

        if (topicName != null) {
            Topic topic = topicService.findOne(topicName);
            topic.setLastPlotterParameters(parameters);
            topicService.save(topic);
            execution.setTopic(topic);
        }

        execution = this.save(execution);

        log.info("Starting execution <" + execution.getId() + ">... now!");
        // ProcessorExecutor plotterExecutor = new ProcessorExecutorImpl();
        plotterExecutor.setJob(execution);
        taskExecutor.execute(plotterExecutor);

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

    @Override
    public void cleanOldJobs() {
        Pageable page = new PageRequest(0, 100);
        Page<Job> results;
        do {
            results = jobDao.findByStatus(JobStatus.Running, page);
            for (Job job : results.getContent()) {
                String msg = "Marking job <" + job.getId() + "> as aborted, as application was rebooted.";
                log.info(msg);
                job.appendLog(msg);
                job.setStatus(JobStatus.Aborted);
                try {
                    job = this.save(job);
                } catch (InvalidExecutionException e) {
                    log.error("An error occurred while persisting the job.");
                }

            }
            page = page.next();
        } while (results.hasNext());

        return;
    }
}
