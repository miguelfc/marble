package org.marble.processor.stanford.service;

import java.util.Map;

import org.marble.processor.stanford.exception.InvalidMessageException;

public interface ProcessorService {

    double processMessage(String message, Map<String, Object> options) throws InvalidMessageException;

}
