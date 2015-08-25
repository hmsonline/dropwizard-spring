package com.hmsonline.dropwizard.spring;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;
import java.text.MessageFormat;

public final class ApplicationContextTypeDeserializer extends JsonDeserializer<SpringConfiguration.ApplicationContextType> {

    @Override
    public SpringConfiguration.ApplicationContextType deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
        final String jsonValue = parser.getText();

        for (SpringConfiguration.ApplicationContextType applicationContextType : SpringConfiguration.ApplicationContextType.values()) {
            if (applicationContextType.getName().equals(jsonValue)) {
                return applicationContextType;
            }
        }

        throw new JsonMappingException(
                MessageFormat.format(
                        "Configuration Error: appContextType must be either \"{0}\" or \"{1}\"",
                        SpringConfiguration.ApplicationContextType.WEB_APPLICATION_CONTEXT.getName(),
                        SpringConfiguration.ApplicationContextType.APPLICATION_CONTEXT.getName()
                )
        );

    }

}
