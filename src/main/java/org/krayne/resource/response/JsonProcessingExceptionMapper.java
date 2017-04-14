package org.krayne.resource.response;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.PropertyBindingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class JsonProcessingExceptionMapper implements ExceptionMapper<JsonProcessingException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonProcessingExceptionMapper.class);

    @Override
    public Response toResponse(JsonProcessingException e) {
        if (e instanceof JsonMappingException) {
            if (e instanceof PropertyBindingException) {
                // IgnoredPropertyException and UnrecognizedPropertyException are both PropertyBindingExceptions
                PropertyBindingException ex = (PropertyBindingException) e;
                return Responses.badRequest(new Error("Invalid property, " + ex.getPropertyName()));
            } else if (e instanceof InvalidFormatException) {
                InvalidFormatException ex = (InvalidFormatException) e;
                String message = "Invalid format on line " + ex.getLocation().getLineNr() + ", column " + ex.getLocation().getColumnNr() + ": " + ex.getValue();
                return Responses.badRequest(new Error(message));
            }
            JsonMappingException ex = (JsonMappingException) e;
            String message = "Error on line " + ex.getLocation().getLineNr() + ", column " + ex.getLocation().getColumnNr();
            return Responses.badRequest(new Error(message));
        } else if (e instanceof JsonParseException) {
            return Responses.badRequest(new Error("Invalid json"));
        } else if (e instanceof JsonGenerationException) {
            LOGGER.error("Failed to generate json", e);
            return Responses.internalServerError(new Error("Failed to generate json"));
        }

        return Responses.badRequest();
    }
}