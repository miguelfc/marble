package org.marble.commons.executor.processor;

import java.util.LinkedHashSet;
import org.marble.commons.executor.Executor;
import org.marble.model.model.JobParameters;

public interface ProcessorExecutor extends Executor {

    public static final String MARBLE_FILTER = "filter";
    public static final String MARBLE_FILTER_FROM_DATE = "fromDate";
    public static final String MARBLE_FILTER_TO_DATE = "toDate";
    public static final String MARBLE_FILTER_FROM_ID = "fromId";
    public static final String MARBLE_FILTER_TO_ID = "toId";
    
    void setExtraParameters(LinkedHashSet<JobParameters> extraParameters);
}
