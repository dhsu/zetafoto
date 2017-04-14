package org.krayne.resource.response;

import org.krayne.datasource.OpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

public final class Responses {
    private Responses() {}

    private static final Logger LOGGER = LoggerFactory.getLogger(Responses.class);

    public static Response badRequest() {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("")
                .build();
    }

    public static Response badRequest(Error error) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    public static Response conflict() {
        return Response.status(Response.Status.CONFLICT)
                .entity("")
                .build();
    }

    public static Response created() {
        return Response.status(Response.Status.CREATED).build();
    }

    public static Response internalServerError() {
        return Response.serverError().build();
    }

    public static Response internalServerError(Error error) {
        return Response.serverError()
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    public static Response notFound() {
        return Response.status(Response.Status.NOT_FOUND)
                .entity("")
                .build();
    }

    public static Response ok() {
        return Response.ok().build();
    }

    public static Response noContent() {
        return Response.noContent().build();
    }

    public static Response resetContent() {
        return Response.status(Response.Status.RESET_CONTENT).build();
    }

    public static Response of(Optional<?> o) {
        if (o.isPresent()) {
            return Response.ok(o.get()).build();
        } else {
            return notFound();
        }
    }

    public static Response from(OpResult opResult) {
        switch (opResult.getType()) {
            case DELETED: return noContent();
            case UPDATED: return ok();
            case CREATED: return created();
            case NON_EXISTENT: return notFound();
            case ALREADY_EXISTING: return conflict();
            case STATE_MISMATCH: return opResult.getMessage().map(message -> badRequest(new Error(message))).orElse(badRequest());
            default: {
                LOGGER.error("Can create http response from opResult with type=" + opResult.getType());
                return internalServerError();
            }
        }
    }
}
