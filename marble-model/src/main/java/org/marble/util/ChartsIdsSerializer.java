package org.marble.util;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import org.marble.model.domain.model.Chart;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ChartsIdsSerializer extends JsonSerializer<List<Chart>> {
    @Override
    public void serialize(List<Chart> value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
            JsonProcessingException {
        jgen.writeStartArray();
        
        for (Chart chart : value) {
            jgen.writeStartObject();
            jgen.writeFieldName("name");
            jgen.writeString(chart.getName());
            jgen.writeFieldName("id");
            jgen.writeString(chart.getId().toString());
            jgen.writeEndObject();
            //jgen.writeString(chart.getId().toString());
        }
        
        jgen.writeEndArray();
        
        //jgen.writeString("Resultado");
    }
}