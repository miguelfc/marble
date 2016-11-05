package org.marble.commons.executor;

import org.marble.commons.domain.model.Job;

public interface Executor extends Runnable {

    @Override
    public void run();

    void setExecution(Job execution);
}
