package org.marble.commons.executor;

import org.marble.model.domain.model.Job;

public interface Executor extends Runnable {

    @Override
    public void run();

    void setJob(Job execution);
}
