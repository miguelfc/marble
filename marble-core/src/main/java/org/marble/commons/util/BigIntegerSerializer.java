package org.marble.commons.util;

import java.io.IOException;
import java.math.BigInteger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class BigIntegerSerializer extends JsonSerializer<BigInteger> {
    @Override
    public void serialize(BigInteger value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
            JsonProcessingException {
        jgen.writeString(value.toString());
    }
}