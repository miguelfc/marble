package org.marble.model.serializers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class CustomDateAndTimeDeserializer extends JsonDeserializer<Date> {

    private static final Logger log = LoggerFactory.getLogger(CustomDateAndTimeDeserializer.class);
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat(
            "EEE MMM dd HH:mm:ss zzz yyyy");

    @Override
    public Date deserialize(JsonParser paramJsonParser,
            DeserializationContext paramDeserializationContext)
            throws IOException, JsonProcessingException {
        String str = paramJsonParser.getText().trim();
        log.error("Deserializing " + str);
        try {
            return dateFormat.parse(str);
        } catch (ParseException e) {

        }
        return paramDeserializationContext.parseDate(str);
    }
}