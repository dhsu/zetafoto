package org.krayne.resource;

import com.codahale.metrics.annotation.Timed;
import org.krayne.datasource.PhotoAlbumDataSource;
import org.krayne.model.Photo;
import org.krayne.resource.response.Responses;
import org.krayne.util.Futures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

@Path("/zetafoto/v1/photos")
public class PhotoResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoResource.class);

    private final PhotoAlbumDataSource photoAlbumDataSource;

    public PhotoResource(PhotoAlbumDataSource photoAlbumDataSource) {
        this.photoAlbumDataSource = photoAlbumDataSource;
    }

    @GET
    @Timed
    @Path("/{photoId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void getPhoto(@NotNull @PathParam("photoId") Long photoId, @Suspended AsyncResponse asyncResponse) {
        this.photoAlbumDataSource.getPhoto(photoId).whenComplete(Futures.handle(
            photo -> asyncResponse.resume(Responses.of(photo)),
            throwable -> {
                LOGGER.error("Photo resource failed to get photo, " + photoId, throwable);
                asyncResponse.resume(Responses.internalServerError());
            }
        ));
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void createPhoto(@NotNull @Valid Photo photo, @Suspended AsyncResponse asyncResponse) {
        this.photoAlbumDataSource.createPhoto(photo).whenComplete(Futures.handle(
            opResult -> asyncResponse.resume(Responses.from(opResult)),
            throwable -> {
                LOGGER.error("Photo resource failed to create photo, " + photo.getId(), throwable);
                asyncResponse.resume(Responses.internalServerError());
            }
        ));
    }

    @PUT
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void updatePhoto(@NotNull @Valid Photo photo, @Suspended AsyncResponse asyncResponse) {
        this.photoAlbumDataSource.updatePhoto(photo).whenComplete(Futures.handle(
            opResult -> asyncResponse.resume(Responses.from(opResult)),
            throwable -> {
                LOGGER.error("Photo resource failed to update photo, " + photo.getId(), throwable);
                asyncResponse.resume(Responses.internalServerError());
            }
        ));
    }

    @DELETE
    @Timed
    @Path("/{photoId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deletePhoto(@NotNull @PathParam("photoId") Long photoId, @Suspended AsyncResponse asyncResponse) {
        this.photoAlbumDataSource.deletePhoto(photoId).whenComplete(Futures.handle(
            opResult -> asyncResponse.resume(Responses.from(opResult)),
            throwable -> {
                LOGGER.error("Album resource failed to get album, " + photoId, throwable);
                asyncResponse.resume(Responses.internalServerError());
            }
        ));
    }
}
