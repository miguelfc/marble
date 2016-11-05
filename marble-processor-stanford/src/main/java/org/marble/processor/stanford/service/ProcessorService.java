package org.marble.processor.stanford.service;

import java.util.Map;

public interface ProcessorService {

    double processMessage(String message, Map<String, Object> options);

}
