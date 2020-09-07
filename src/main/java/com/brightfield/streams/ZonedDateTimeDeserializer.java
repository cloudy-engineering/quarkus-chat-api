package com.brightfield.streams;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class ZonedDateTimeDeserializer extends StdDeserializer<ZonedDateTime> {

    public ZonedDateTimeDeserializer() {
        this(null);
    }

    public ZonedDateTimeDeserializer(Class<ZonedDateTime> vc) {
        super(vc);
    }

    @Override
    public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String stringDate = p.getText();
        TemporalAccessor accessor= DateTimeFormatter.ISO_DATE_TIME.parse(stringDate);

        return ZonedDateTime.from(accessor);
    }
}
