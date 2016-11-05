package org.marble.processor.simple.service;

import java.util.Map;

public interface ProcessorService {

    double processMessage(String message, Map<String, Object> options);

}
