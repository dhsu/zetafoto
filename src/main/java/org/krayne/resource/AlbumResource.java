package org.krayne.resource;

import com.codahale.metrics.annotation.Timed;
import org.krayne.datasource.PhotoAlbumDataSource;
import org.krayne.model.Album;
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

@Path("/zetafoto/v1/albums")
public class AlbumResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlbumResource.class);

    private final PhotoAlbumDataSource photoAlbumDataSource;

    public AlbumResource(PhotoAlbumDataSource photoAlbumDataSource) {
        this.photoAlbumDataSource = photoAlbumDataSource;
    }

    @GET
    @Timed
    @Path("/{albumId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void getAlbum(@NotNull @PathParam("albumId") Long albumId, @Suspended AsyncResponse asyncResponse) {
        this.photoAlbumDataSource.getAlbum(albumId).whenComplete(Futures.handle(
            album -> asyncResponse.resume(Responses.of(album)),
            throwable -> {
                LOGGER.error("Album resource failed to get album, " + albumId, throwable);
                asyncResponse.resume(Responses.internalServerError());
            }
        ));
    }

    @GET
    @Timed
    @Path("/{albumId}/content")
    @Produces(MediaType.APPLICATION_JSON)
    public void getAlbumContent(@NotNull @PathParam("albumId") Long albumId, @Suspended AsyncResponse asyncResponse) {
        this.photoAlbumDataSource.getAlbumContent(albumId).whenComplete(Futures.handle(
            albumContent -> asyncResponse.resume(Responses.of(albumContent)),
            throwable -> {
                LOGGER.error("Album resource failed to get album content for album, " + albumId, throwable);
                asyncResponse.resume(Responses.internalServerError());
            }
        ));
    }

    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    public void getAlbums(@Suspended AsyncResponse asyncResponse) {
        this.photoAlbumDataSource.getAlbums().whenComplete(Futures.handle(
                albums -> asyncResponse.resume(Responses.ok(albums)),
                throwable -> {
                    LOGGER.error("Album resource failed to get albums", throwable);
                    asyncResponse.resume(Responses.internalServerError());
                }
        ));
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void createAlbum(@NotNull @Valid Album album, @Suspended AsyncResponse asyncResponse) {
        this.photoAlbumDataSource.createAlbum(album).whenComplete(Futures.handle(
            opResult -> asyncResponse.resume(Responses.from(opResult)),
            throwable -> {
                LOGGER.error("Album resource failed to create album, " + album.getId(), throwable);
                asyncResponse.resume(Responses.internalServerError());
            }
        ));
    }

    @PUT
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void updateAlbum(@NotNull @Valid Album album, @Suspended AsyncResponse asyncResponse) {
        this.photoAlbumDataSource.updateAlbum(album).whenComplete(Futures.handle(
            opResult -> asyncResponse.resume(Responses.from(opResult)),
            throwable -> {
                LOGGER.error("Album resource failed to update album, " + album.getId(), throwable);
                asyncResponse.resume(Responses.internalServerError());
            }
        ));
    }

    @DELETE
    @Timed
    @Path("/{albumId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteAlbum(@NotNull @PathParam("albumId") Long albumId, @Suspended AsyncResponse asyncResponse) {
        this.photoAlbumDataSource.deleteAlbum(albumId).whenComplete(Futures.handle(
            opResult -> asyncResponse.resume(Responses.from(opResult)),
            throwable -> {
                LOGGER.error("Album resource failed to get album, " + albumId, throwable);
                asyncResponse.resume(Responses.internalServerError());
            }
        ));
    }
}
