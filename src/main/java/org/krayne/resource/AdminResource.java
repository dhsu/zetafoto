package org.krayne.resource;

import com.codahale.metrics.annotation.Timed;
import org.krayne.datasource.PhotoAlbumDataSource;
import org.krayne.init.Initializer;
import org.krayne.resource.response.Responses;
import org.krayne.util.Futures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

@Path("/zetafoto/v1/admin")
public class AdminResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminResource.class);

    private final PhotoAlbumDataSource photoAlbumDataSource;
    private final Initializer initializer;
    private final String initialAlbumsUrl;
    private final String initialPhotosUrl;

    public AdminResource(PhotoAlbumDataSource photoAlbumDataSource, Initializer initializer, String initialAlbumsUrl, String initialPhotosUrl) {
        this.photoAlbumDataSource = photoAlbumDataSource;
        this.initializer = initializer;
        this.initialAlbumsUrl = initialAlbumsUrl;
        this.initialPhotosUrl = initialPhotosUrl;
    }

    @POST
    @Timed
    @Path("/init")
    public void init(@Suspended AsyncResponse asyncResponse) {
        this.initializer.init(this.initialAlbumsUrl, this.initialPhotosUrl).whenComplete(Futures.handle(
            v -> asyncResponse.resume(Responses.ok()),
            throwable -> {
                LOGGER.error("Admin resource failed to initialize data", throwable);
                asyncResponse.resume(Responses.internalServerError());
            }
        ));
    }

    @GET
    @Timed
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public void getAll(@Suspended AsyncResponse asyncResponse) {
//        this.photoAlbumDataSource.getAll().whenComplete(Futures.handle(
//                all -> asyncResponse.resume(Responses.of(albumContent)),
//                throwable -> {
//                    LOGGER.error("Album resource failed to get album content for album, " + albumId, throwable);
//                    asyncResponse.resume(Responses.internalServerError());
//                }
//        ));
    }
}
