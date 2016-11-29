package org.marble.commons.model;

import java.math.BigInteger;

import org.marble.util.BigIntegerSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class JobRestResult extends RestResult {
    @JsonSerialize(using = BigIntegerSerializer.class)
    private BigInteger id;

    public JobRestResult() {
        super();
    }

    public JobRestResult(String message) {
        super(message);
    }

    public JobRestResult(BigInteger executionId) {
        this.id = executionId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }
}
